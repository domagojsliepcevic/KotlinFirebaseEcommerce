package hr.algebra.sverccommercefinal.data

sealed class Category(val category:String){

    object Clothes:Category("Clothes")
    object Electronics:Category("Electronics")
    object Accessory:Category("Accessory")
    object Furniture:Category("Furniture")
    object Outdoors:Category("Outdoors")
    object Tools:Category("Tools")
    object ToysAndGames:Category("Toys and Games")

}
