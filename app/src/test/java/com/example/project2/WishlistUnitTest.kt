package com.example.project2

import org.junit.Assert.assertEquals
import org.junit.Test

class WishlistUnitTest {

    @Test
    fun wishlistItem_creation_holdsCorrectValues() {
        val item = WishlistItem(
            name = "Wireless Headphones",
            price = "99.99",
            url = "https://example.com/headphones"
        )

        assertEquals("Wireless Headphones", item.name)
        assertEquals("99.99", item.price)
        assertEquals("https://example.com/headphones", item.url)
    }

    @Test
    fun priceParsing_handlesPriceWithAndWithoutDollarSign() {
        val price1 = "$49.99".replace("$", "").toDoubleOrNull()
        val price2 = "49.99".replace("$", "").toDoubleOrNull()
        val price3 = "$1,200.00".replace("$", "").replace(",", "").toDoubleOrNull()

        assertEquals(49.99, price1!!, 0.001)
        assertEquals(49.99, price2!!, 0.001)
        assertEquals(1200.00, price3!!, 0.001)
    }

    @Test
    fun urlFormatting_prependsHttpsIfMissing() {
        fun formatUrl(rawUrl: String): String {
            return if (!rawUrl.startsWith("http://", ignoreCase = true) &&
                !rawUrl.startsWith("https://", ignoreCase = true)
            ) {
                "https://$rawUrl"
            } else {
                rawUrl
            }
        }

        assertEquals("https://amazon.com", formatUrl("amazon.com"))
        assertEquals("https://www.nike.com/shoes", formatUrl("www.nike.com/shoes"))
        assertEquals("http://example.com", formatUrl("http://example.com"))
        assertEquals("https://example.com", formatUrl("https://example.com"))
    }
}
