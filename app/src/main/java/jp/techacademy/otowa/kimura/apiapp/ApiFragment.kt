package jp.techacademy.otowa.kimura.apiapp

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.moshi.Moshi
import jp.techacademy.otowa.kimura.apiapp.databinding.FragmentApiBinding
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException

class ApiFragment : Fragment() {
    private var _binding: FragmentApiBinding? = null
    private val binding get() = _binding!!

    //遅らせる
    private val apiAdapter by lazy { ApiAdapter() }
    private val handler = Handler(Looper.getMainLooper())

    //MainでいうonCreate：Fragmentができ流時に呼ばれる
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApiBinding.inflate(inflater, container, false)
        return binding.root
    }

    //onViewCrested:View(画面)作成時に呼び出される→Viewの表示ができる
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ここから初期化処理を行う
        // RecyclerViewの初期化
        binding.recyclerView.apply {
            adapter = apiAdapter
            //レイアウトはLineaLayoutで作成する
            layoutManager = LinearLayoutManager(requireContext()) // 一列ずつ表示
        }
        //上から下にスワイプを行うと更新する
        binding.swipeRefreshLayout.setOnRefreshListener {
            updateData()
        }
        updateData()
    }

    //API通信を行いデータを取得している
    private fun updateData() {
        //はじめにURLを作成
        val url = StringBuilder()
            .append(getString(R.string.base_url)) // https://webservice.recruit.co.jp/hotpepper/gourmet/v1/
            .append("?key=").append(getString(R.string.api_key)) // Apiを使うためのApiKey
            .append("&start=").append(1) // 何件目からのデータを取得するか
            .append("&count=").append(COUNT) // 1回で20件取得する
            .append("&keyword=")
            .append(getString(R.string.api_keyword)) // お店の検索ワード。ここでは例として「ランチ」を検索
            .append("&format=json") // ここで利用しているAPIは戻りの形をxmlかjsonが選択することができる。Androidで扱う場合はxmlよりもjsonの方が扱いやすいので、jsonを選択
           //上記の内容を文字列にする
            .toString()
        //Client：Httpを行う本体
        val client = OkHttpClient.Builder()
                //ログに通信の詳細を出すことができる
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
        val request = Request.Builder()
            .url(url)
            .build()

        //Http通信を行う。
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { // Error時の処理
                e.printStackTrace()
                handler.post {
                    //RecyclerViewの空のListで更新
                    updateRecyclerView(listOf())
                }
            }

            override fun onResponse(call: Call, response: Response) { // 成功時の処理
                // Jsonを変換するためのAdapterを用意
                val moshi = Moshi.Builder().build()
                val jsonAdapter = moshi.adapter(ApiResponse::class.java)

                var list = listOf<Shop>()
                //body:本体の中身を見る
                response.body?.string()?.also {
                    //JsonデータからApiResponseへの変換を行う
                    val apiResponse = jsonAdapter.fromJson(it)
                    //!=null:何かが入っている状態
                    if (apiResponse != null) {
                        list = apiResponse.results.shop
                    }
                }
                //handler.post{やりたいことを書く}：スレッドをまたげる
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
        private const val COUNT = 20
    }
}
