package jp.techacademy.otowa.kimura.apiapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import jp.techacademy.otowa.kimura.apiapp.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //IntentからURL取得→URLのWebページをWebViewの表示する
        binding.webView.loadUrl(intent.getStringExtra(KEY_URL).toString())
    }

    //companion object:クラスのインスタンスを生成せずに、メソッドやプロパティを定義するための機能
    companion object {
        private const val KEY_URL = "key_url"
        //startメソッド: WebActivityの遷移処理をしている
        //引数としてActivityとUrlを受け取る
        fun start(activity: Activity, url: String) {
            //画面遷移を行うためにActivityクラスのStartActivityメソッド呼び出し
            activity.startActivity(
                Intent(activity, WebViewActivity::class.java).putExtra(
                    KEY_URL,
                    url
                )
            )
        }
    }
}