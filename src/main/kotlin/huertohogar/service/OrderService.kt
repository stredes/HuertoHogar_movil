package huertohogar.service

import huertohogar.model.Order
import huertohogar.model.OrderItem
import huertohogar.repo.OrdersRepository
import huertohogar.repo.InventoryRepository
import java.util.UUID

class OrderService(
    private val ordersRepo: OrdersRepository,
    private val inventoryRepo: InventoryRepository
) {
    private val orders = ordersRepo.load()

    fun listOrders(): List<Order> = orders.orders

    fun createOrder(items: List<OrderItem>): Order? {
        // Load fresh inventory to see latest changes
        val inventory = inventoryRepo.load()

        // Validate stock
        for (it in items) {
            val p = inventory.products.find { pr -> pr.id == it.productId } ?: return null
            if (p.quantity < it.quantity) return null
        }

        // Deduct stock and compute total
        var total = 0.0
        for (it in items) {
            val p = inventory.products.find { pr -> pr.id == it.productId }!!
            p.quantity -= it.quantity
            total += p.price * it.quantity
        }

        // Save inventory
        inventoryRepo.save(inventory)

        val order = Order(UUID.randomUUID().toString(), items, total)
        orders.orders.add(order)
        ordersRepo.save(orders)
        return order
    }
}
