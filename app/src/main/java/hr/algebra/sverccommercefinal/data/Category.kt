package hr.algebra.sverccommercefinal.data

sealed class Category(val category:String){

    object Clothes:Category("clothes")
    object Electronics:Category("electronics")
    object Accessory:Category("accessory")
    object Furniture:Category("furniture")
    object Outdoors:Category("outdoors")
    object Tools:Category("tools")

}
