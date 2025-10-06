package huertohogar.repo

import huertohogar.model.Order
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

@Serializable
data class Orders(val orders: MutableList<Order> = mutableListOf())

class OrdersRepository(private val storagePath: String = "orders.json") {
    private val json = Json { prettyPrint = true }

    fun load(): Orders {
        val path = Paths.get(storagePath)
        if (!Files.exists(path)) return Orders()
        val text = File(storagePath).readText()
        return try {
            json.decodeFromString<Orders>(text)
        } catch (e: Exception) {
            Orders()
        }
    }

    fun save(orders: Orders) {
        val text = json.encodeToString(orders)
        File(storagePath).writeText(text)
    }
}
