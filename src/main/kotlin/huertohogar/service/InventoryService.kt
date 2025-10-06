package huertohogar.service

import huertohogar.model.Product
import huertohogar.repo.Inventory
import huertohogar.repo.InventoryRepository
import java.util.UUID

class InventoryService(private val repo: InventoryRepository) {
    private fun loadInventory() = repo.load()

    fun listProducts(): List<Product> = loadInventory().products

    fun addProduct(name: String, price: Double, quantity: Double): Product {
        val inventory = loadInventory()
        val product = Product(UUID.randomUUID().toString(), name, price, quantity)
        inventory.products.add(product)
        repo.save(inventory)
        return product
    }

    fun findById(id: String): Product? = loadInventory().products.find { it.id == id }

    fun updateQuantity(id: String, delta: Double): Boolean {
        val inventory = loadInventory()
        val p = inventory.products.find { it.id == id } ?: return false
        val newQty = p.quantity + delta
        if (newQty < 0.0) return false
        p.quantity = newQty
        repo.save(inventory)
        return true
    }

    fun removeProduct(id: String): Boolean {
        val inventory = loadInventory()
        val removed = inventory.products.removeIf { it.id == id }
        if (removed) repo.save(inventory)
        return removed
    }
}
