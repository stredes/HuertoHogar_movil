package huertohogar.repo

import huertohogar.model.Product
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

@Serializable
data class Inventory(val products: MutableList<Product> = mutableListOf())

class InventoryRepository(private val storagePath: String = "inventory.json") {
    private val json = Json { prettyPrint = true }

    fun load(): Inventory {
        val path = Paths.get(storagePath)
        if (!Files.exists(path)) return Inventory()
        val text = File(storagePath).readText()
        return try {
            json.decodeFromString<Inventory>(text)
        } catch (e: Exception) {
            Inventory()
        }
    }

    fun save(inventory: Inventory) {
        val text = json.encodeToString(inventory)
        File(storagePath).writeText(text)
    }
}
