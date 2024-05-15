package jp.techacademy.otowa.kimura.apiapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import jp.techacademy.otowa.kimura.apiapp.databinding.RecyclerFavoriteBinding

class ApiAdapter : ListAdapter<Shop,ApiItemViewHolder>(ApiItemCallback()) {

    //一覧画面から登録するときのコールバック（FavoriteFragmentへの通知するメソッド）
    var onClickAddFavorite: ((Shop) -> Unit)? = null

    //一覧画面から削除するときのコールバック（ApiFragmentへの通知するメソッド）
    var onClickDeleteFavorite: ((Shop) -> Unit)? = null

    //ViewHolderを生成して返す
    override fun onCreateViewHolder(parent:ViewGroup,viewType:Int):ApiItemViewHolder{
        //ViewBindingを引数にApiItemViewHolderを生成
        val view =
            RecyclerFavoriteBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ApiItemViewHolder(view)
    }

    //指定された位置(position)のViewにShopの情報をセットする
    override fun onBindViewHolder(holder:ApiItemViewHolder,position:Int){
        holder.bind(getItem(position),position,this)
    }
}

//リスト内の1行の内容を保持する
class ApiItemViewHolder(private val binding:RecyclerFavoriteBinding):
    RecyclerView.ViewHolder(binding.root){
    fun bind(shop: Shop,position: Int,adapter: ApiAdapter){
        //偶数（しろ）と奇数（黒）で背景の色を変更
        //setBackgroundColor:背景色設定
        binding.rootView.setBackgroundColor(
            //ContextCompat.getColor:色の値を取得
            ContextCompat.getColor(
                //色のIDを渡す
                binding.rootView.context,
                //position % 2 == 0で偶数か奇数か
                if(position % 2 == 0) android.R.color.white else android.R.color.darker_gray
            )
        )
        //nameTextViewのTextプロパティに代入されたオブジェクトのnameプロパティを代入
        binding.nameTextView.text = shop.name

        //Picassoライブラリを使い、imageViewにdata.logoのurlの画像を読み込ませる
        Picasso.get().load(shop.logoImage).into(binding.imageView)

        //星の処理
        binding.favoriteImageView.apply {
            //お気に入り状態をデータベースから検索
            //指定したShopがお気に入りに登録されているかどうかを判定している。
            val isFavorite = FavoriteShop.findBy(shop.id)!= null

            //白抜きの星の設定
            //画像の切り替えを行なっている
            //setImageResource:画像を設定するためのメソッド。引数に設定しているIDを渡す。
            setImageResource(if (isFavorite)R.drawable.ic_star else R.drawable.ic_star_bprder)

            //星をタップした時の処理
            //お気に入りにしているかの判定
            //お気に入りに登録されている場合（星マークが塗りつぶされている場合）は、お気に入りから削除する処理を呼び出します。
            // 具体的には、ApiFragmentのonClickDeleteFavoriteメソッドを呼び出します
            setOnClickListener{
                if (isFavorite){//削除の処理
                    adapter.onClickDeleteFavorite?.invoke(shop)
                }else{//追加の処理
                    adapter.onClickAddFavorite?.invoke(shop)
                }
                //星が更新されていることをAdapterに通知
                adapter.notifyItemChanged(position)
            }
        }

    }
}

//データの差分（追加、削除、変更）を確認するクラス
internal class ApiItemCallback : DiffUtil.ItemCallback<Shop>(){
    //areItemsTheSameとareContentsTheSamをオーバオーライド：データの比較方法を記載
    override fun areItemsTheSame(oldItem: Shop, newItem: Shop): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Shop, newItem: Shop): Boolean {
        return oldItem == newItem
    }
}