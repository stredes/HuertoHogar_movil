package huertohogar.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String,
    val name: String,
    var price: Double,
    var quantity: Double
)
