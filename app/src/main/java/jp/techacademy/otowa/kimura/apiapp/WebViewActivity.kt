package jp.techacademy.otowa.kimura.apiapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import jp.techacademy.otowa.kimura.apiapp.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebViewBinding
    private var isFavorite = false
    private lateinit var favoriteShop: FavoriteShop

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val name = intent.getStringExtra(KEY_NAME)
        val id = intent.getStringExtra(KEY_ID)
        val address = intent.getStringExtra(KEY_ADDRESS)
        val logoImage = intent.getStringExtra(KEY_IMAGE)
        val couponUrls = intent.getStringExtra(KEY_COUPONURLS)

        Log.d("shop", id!!)
        Log.d("shop", address!!)
        Log.d("shop", logoImage!!)
        Log.d("shop", couponUrls!!)
        Log.d("shop", name!!)

        binding.webView.loadUrl(intent.getStringExtra(KEY_COUPONURLS).toString())

        favoriteShop = FavoriteShop(id, logoImage, name, couponUrls, address)

        binding.favoriteImageView.apply {
            // お気に入り状態を取得
            isFavorite = FavoriteShop.findBy(favoriteShop.id) != null

            // 星の画像を設定
            setImageResource(if (isFavorite) R.drawable.ic_star else R.drawable.ic_star_bprder)

            // 星をタップした時の処理
            setOnClickListener {
                if (isFavorite) {
                    FavoriteShop.delete(favoriteShop.id)
                } else {
                    FavoriteShop.insert(favoriteShop)
                }
                isFavorite = !isFavorite
                setImageResource(if (isFavorite) R.drawable.ic_star else R.drawable.ic_star_bprder)
            }
        }
    }

    //companion object:クラスのインスタンスを生成せずに、メソッドやプロパティを定義するための機能

    companion object {
        private const val KEY_NAME= "key_name"
        private const val KEY_ID = "key_id"
        private const val KEY_ADDRESS = "key_address"
        private const val KEY_IMAGE = "key_logoimage"
        private const val KEY_COUPONURLS = "key_couponUrls"


        //startメソッド: WebActivityの遷移処理をしている
        //引数としてActivityとUrlを受け取る
        fun start(activity: Activity, shop: Shop ) {
            //画面遷移を行うためにActivityクラスのStartActivityメソッド呼び出し
            activity.startActivity(
                Intent(activity, WebViewActivity::class.java)
                    .putExtra(KEY_ADDRESS,shop.address)
                    .putExtra(KEY_COUPONURLS,shop.couponUrls.sp.ifEmpty { shop.couponUrls.pc })
                    .putExtra(KEY_ID, shop.id)
                    .putExtra(KEY_IMAGE,shop.logoImage)
                    .putExtra(KEY_NAME, shop.name)
            )
        }
    }
}

