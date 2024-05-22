package jp.techacademy.otowa.kimura.apiapp

interface FragmentCallback {
    //Itemをクリックした時の処理
    fun onClickItem(shop:Shop)

    // お気に入り追加時の処理
    fun onAddFavorite(shop: Shop)

    // お気に入り削除時の処理
    fun onDeleteFavorite(id: String)
}