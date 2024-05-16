package jp.techacademy.otowa.kimura.apiapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import jp.techacademy.otowa.kimura.apiapp.databinding.RecyclerFavoriteBinding

class FavoriteAdapter : ListAdapter<FavoriteShop, FavoriteItemViewHolder>(FavoriteCallback()) {

    // お気に入り画面から削除するときのコールバック（ApiFragmentへ通知するメソッド)
    var onClickDeleteFavorite: ((FavoriteShop) -> Unit)? = null

    //Itemをクリックした時のメソッド
    var onClickItem: ((String) -> Unit)? = null

    /**
     * ViewHolderを生成して返す
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteItemViewHolder {
        // ViewBindingを引数にApiItemViewHolderを生成
        val view =
            RecyclerFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteItemViewHolder(view)
    }

    //指定された位置のViewにFavoriteShopの情報をセットする
    override fun onBindViewHolder(holder: FavoriteItemViewHolder, position: Int) {
        holder.bind(getItem(position), position, this)
    }
}

    //お気に入りが登録されている時のリスト

class FavoriteItemViewHolder(private val binding: RecyclerFavoriteBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(favoriteShop: FavoriteShop, position: Int, adapter: FavoriteAdapter) {
        binding.rootView.apply {
            // 偶数番目と奇数番目で背景色を変更させる
            binding.rootView.setBackgroundColor(
                ContextCompat.getColor(
                    binding.rootView.context,
                    if (position % 2 == 0) android.R.color.white else android.R.color.darker_gray
                )
            )
            //クリック時のイベントリスナーを割り当て
            setOnClickListener {
                adapter.onClickItem?.invoke(favoriteShop.url)
            }
        }
        //nameTextViewのTextプロパティに代入されたオブジェクトのnameのプロパティを代入
        binding.nameTextView.text = favoriteShop.name

        //Picassoというライブラリを使ってImageView画像をはめ込む
        Picasso.get().load(favoriteShop.imageUrl).into(binding.imageView)

        //星をタップした時の処理
        //レイアウトのほしのアイコンは塗りつぶしなしなので設定不要
        binding.favoriteImageView.setOnClickListener{
            adapter.onClickDeleteFavorite?.invoke(favoriteShop)
            adapter.notifyItemChanged(position)
        }
    }
}

//データの差分（追加、削除、更新）を確認
internal class FavoriteCallback : DiffUtil.ItemCallback<FavoriteShop>() {

    override fun areItemsTheSame(oldItem: FavoriteShop, newItem: FavoriteShop): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FavoriteShop, newItem: FavoriteShop): Boolean {
        return oldItem.equals(newItem)
    }
}
