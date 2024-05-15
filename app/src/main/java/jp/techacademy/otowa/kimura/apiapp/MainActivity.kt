package jp.techacademy.otowa.kimura.apiapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import jp.techacademy.otowa.kimura.apiapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding


    private val viewPagerAdapter by lazy { ViewPagerAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //ViewPager2の初期化
        binding.viewPage2.apply {
            adapter = viewPagerAdapter
            //スワイプの向きORIENTATION_HORIZONTALを指定すれば縦スワイプで実装可能
            //orientation:スワイプの方向を決めるもの。指定なし：横向き
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            //ViewPagerで保持する画面の数
            //offscreenPagerLimit:お気に入りが追加されたタイミングでお気に入りのfragmentの絵を更新。設定が必要
            offscreenPageLimit = viewPagerAdapter.itemCount
        }

        //TabLayoutの初期化//
        //TabLayoutとviewPager2を連携
        //ラムダ式：各タブと位置に対する処理
        TabLayoutMediator(binding.tabLayout,binding.viewPage2){ tab,position ->
            tab.setText(viewPagerAdapter.titleIds[position])
        }.attach()//連携を有効にさせる
    }
}