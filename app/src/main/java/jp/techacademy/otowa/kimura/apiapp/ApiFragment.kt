package jp.techacademy.otowa.kimura.apiapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.moshi.Moshi
import jp.techacademy.otowa.kimura.apiapp.databinding.FragmentApiBinding
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException

class ApiFragment : Fragment() {
    private var _binding: FragmentApiBinding? = null
    private val binding get() = _binding!!

    private val apiAdapter by lazy { ApiAdapter() }
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ここから初期化処理を行う
        // RecyclerViewの初期化
        binding.recyclerView.apply {
            adapter = apiAdapter
            layoutManager = LinearLayoutManager(requireContext()) // 一列ずつ表示
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            updateData()
        }
        updateData()
    }

    private fun updateData() {
        val url = StringBuilder()
            .append(getString(R.string.base_url)) // https://webservice.recruit.co.jp/hotpepper/gourmet/v1/
            .append("?key=").append(getString(R.string.api_key)) // Apiを使うためのApiKey
            .append("&start=").append(1) // 何件目からのデータを取得するか
            .append("&count=").append(COUNT) // 1回で20件取得する
            .append("&keyword=")
            .append(getString(R.string.api_keyword)) // お店の検索ワード。ここでは例として「ランチ」を検索
            .append("&format=json") // ここで利用しているAPIは戻りの形をxmlかjsonが選択することができる。Androidで扱う場合はxmlよりもjsonの方が扱いやすいので、jsonを選択
            .toString()
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { // Error時の処理
                e.printStackTrace()
                handler.post {
                    updateRecyclerView(listOf())
                }
            }

            override fun onResponse(call: Call, response: Response) { // 成功時の処理
                // Jsonを変換するためのAdapterを用意
                val moshi = Moshi.Builder().build()
                val jsonAdapter = moshi.adapter(ApiResponse::class.java)

                var list = listOf<Shop>()
                response.body?.string()?.also {
                    val apiResponse = jsonAdapter.fromJson(it)
                    if (apiResponse != null) {
                        list = apiResponse.results.shop
                    }
                }
                handler.post {
                    updateRecyclerView(list)
                }
            }
        })
    }

    private fun updateRecyclerView(list: List<Shop>) {
        apiAdapter.submitList(list)
        // SwipeRefreshLayoutのくるくるを消す
        binding.swipeRefreshLayout.isRefreshing = false
    }

    companion object {
        // 1回のAPIで取得する件数
        private const val COUNT = 3
    }
}