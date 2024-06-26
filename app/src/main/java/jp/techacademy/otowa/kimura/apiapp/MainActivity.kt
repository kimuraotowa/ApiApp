package jp.techacademy.otowa.kimura.apiapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import androidx.appcompat.app.AlertDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import jp.techacademy.otowa.kimura.apiapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), FragmentCallback {
    private lateinit var binding: ActivityMainBinding

    private val viewPagerAdapter by lazy { ViewPagerAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ViewPager2の初期化
        binding.viewPager2.apply {
            adapter = viewPagerAdapter
            // スワイプの向き横（ORIENTATION_VERTICAL を指定すれば縦スワイプで実装可能です）
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            // ViewPager2で保持する画面数
            offscreenPageLimit = viewPagerAdapter.itemCount
        }

        // TabLayoutの初期化
        // TabLayoutとViewPager2を紐づける
        // TabLayoutのTextを指定する
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.setText(viewPagerAdapter.titleIds[position])
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            // タブが選択された際に呼ばれる
            override fun onTabSelected(tab: TabLayout.Tab) {
                showFavoriteTabInfo(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    //FragmentCallbackで追加したonClickItem()メソッド
    override fun onClickItem(shop:Shop) {
        WebViewActivity.start(this, shop)
    }




     //お気に入りタブにトーストを表示

    private fun showFavoriteTabInfo(tab: TabLayout.Tab) {
        if (tab.position == VIEW_PAGER_POSITION_FAVORITE && FavoriteShop.findAll()
                .isEmpty()
        ) {
            Toast.makeText(this@MainActivity, R.string.empty_favorite, Toast.LENGTH_SHORT)
                .show()
        }
    }


      //Favoriteに追加するときのメソッド(Fragment -> Activity へ通知する)
    override fun onAddFavorite(shop: Shop) {
        FavoriteShop.insert(FavoriteShop().apply {
            id = shop.id
            name = shop.name
            imageUrl = shop.logoImage
            url = shop.couponUrls.sp.ifEmpty { shop.couponUrls.pc }
            address = shop.address
        })
        (viewPagerAdapter.fragments[VIEW_PAGER_POSITION_FAVORITE] as FavoriteFragment).updateData()
    }


     // Favoriteから削除するときのメソッド(Fragment -> Activity へ通知する)
    override fun onDeleteFavorite(id: String) {
        showConfirmDeleteFavoriteDialog(id)
    }

    //ダイアログ表示
    private fun showConfirmDeleteFavoriteDialog(id: String) {
        AlertDialog.Builder(this)
                //ダイアログのタイトルを設定
                //タイトルのテキストリソースのID
            .setTitle(R.string.delete_favorite_dialog_title)
            //ダイアログのメッセージを設定
            .setMessage(R.string.delete_favorite_dialog_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                deleteFavorite(id)
                if (binding.tabLayout.selectedTabPosition == VIEW_PAGER_POSITION_FAVORITE) {
                    showFavoriteTabInfo(binding.tabLayout.getTabAt(binding.tabLayout.selectedTabPosition)!!)
                }
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .create()
            .show()
    }

    private fun deleteFavorite(id: String) {
        FavoriteShop.delete(id)
        (viewPagerAdapter.fragments[VIEW_PAGER_POSITION_API] as ApiFragment).updateView()
        (viewPagerAdapter.fragments[VIEW_PAGER_POSITION_FAVORITE] as FavoriteFragment).updateData()
    }


    companion object {
        private const val VIEW_PAGER_POSITION_API = 0
        private const val VIEW_PAGER_POSITION_FAVORITE = 1
    }

    override fun onResume() {
        super.onResume()

        //try{}catch():例外をスキップしたり、内容ごとに処理をおこない異常終了を防ぐことができる
        try {
            //新着のリスト更新
            (viewPagerAdapter.fragments[VIEW_PAGER_POSITION_API] as ApiFragment).updateView()
            //お気に入りリストの更新
            (viewPagerAdapter.fragments[VIEW_PAGER_POSITION_FAVORITE] as FavoriteFragment).updateData()
        }catch (e:Exception){}

    }

}
