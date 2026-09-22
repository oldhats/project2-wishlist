package com.example.project2

/**
 * Data model representing a single item in the user's Wishlist.
 *
 * @property name The display name of the product/item.
 * @property price The formatted or numeric price string (e.g. "29.99" or "$29.99").
 * @property url The web address where the item can be viewed or purchased.
 */
data class WishlistItem(
    val name: String,
    val price: String,
    val url: String
)
