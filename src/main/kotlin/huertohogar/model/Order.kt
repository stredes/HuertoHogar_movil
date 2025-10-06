package huertohogar.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderItem(
    val productId: String,
    val quantity: Double
)

@Serializable
data class Order(
    val id: String,
    val items: List<OrderItem>,
    val total: Double,
    val paid: Double = 0.0,
    val customer: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
