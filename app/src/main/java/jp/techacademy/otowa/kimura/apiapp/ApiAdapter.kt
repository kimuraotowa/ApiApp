package jp.techacademy.otowa.kimura.apiapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import jp.techacademy.otowa.kimura.apiapp.databinding.RecyclerFavoriteBinding

//第一引数Shop：データを保持するクラス
//第二引数ApiItemViewHolder:RecyclerViewで表示させる1行の内容を保持するクラス
//onCreateViewHolderとonBindViewHolederをオーバーライド：Shopの情報がAdapterでほじされ、RecyclerViewで表示される
class ApiAdapter : ListAdapter<Shop,ApiItemViewHolder>(ApiItemCallback()) {
    //ViewHolderを生成して返す
    override fun onCreateViewHolder(parent:ViewGroup,viewType:Int):ApiItemViewHolder{
        //ViewBindingを引数にApiItemViewHolderを生成
        val view =
            RecyclerFavoriteBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ApiItemViewHolder(view)
    }

    //指定された位置(position)のViewにShopの情報をセットする
    override fun onBindViewHolder(holder: ApiItemViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }
}

//リスト内の1行の内容を保持する
class ApiItemViewHolder(private val binding:RecyclerFavoriteBinding):
        RecyclerView.ViewHolder(binding.root){
            fun bind(shop:Shop,position:Int){
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

                //白抜き星マークの画像を指定
                binding.favoriteImageView.setImageResource(R.drawable.ic_star_bprder)
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