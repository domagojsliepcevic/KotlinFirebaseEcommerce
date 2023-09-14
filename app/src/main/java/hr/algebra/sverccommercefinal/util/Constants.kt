package hr.algebra.sverccommercefinal.util

object Constants {
    // Defines a constant string for the name of the user collection in Firestore.
    const val USER_COLLECTION = "user"


    //Defines a constant string for collection path for products in firestore
    const val PRODUCT_COLLECTION = "Products"

    //Defines a constant string for user  subcollection cart in firestore
    const val CART_SUBCOLLECTION = "cart"

    //Defines a constant string for user  subcollection cart product id value in firestore
    const val CART_SUBCOLLECTION_PRODUCT_ID = "product.id"

    //Defines a constant string for generated document ID in firestore
    const val FIRESTORE_DOCUMENT_NAME = "__name__"

    //Defines a constant string for category name in Firestore.
    const val PRICE_FIELD = "price"


    //Defines a constant string for category name in Firestore.
    const val CATEGORY_FIELD = "category"

    //Defines a constant string for offer percentage field in Firestore
    const val OFFER_PERCENTAGE_FIELD = "offerPercentage"


    //Defines a constant string for special product category value field in Firestore.
    const val SPECIAL_PRODUCT_CATEGORY_VALUE = "Special Products"

    //Defines a constant string for best deals product category value field in Firestore.
    const val BEST_DEALS_PRODUCT_CATEGORY_VALUE = "Best Deals"

    //Defines a constant string for best products category value field in Firestore.
    const val BEST_PRODUCT_CATEGORY_VALUE = "Best Products"
}