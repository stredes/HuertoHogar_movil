package huertohogar.service

import huertohogar.repo.InventoryRepository
import huertohogar.repo.OrdersRepository
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertEquals

class OrderServiceTest {
    @Test
    fun testCreateOrder() {
        val invPath = "test_inv.json"
        val ordPath = "test_orders.json"
        try { java.io.File(invPath).delete(); java.io.File(ordPath).delete() } catch (_: Exception) {}

        val invRepo = InventoryRepository(invPath)
        val ordRepo = OrdersRepository(ordPath)
        val invService = InventoryService(invRepo)
        val orderService = OrderService(ordRepo, invRepo)

        val p = invService.addProduct("Lechuga", 1.5, 5.0)
        val items = listOf(huertohogar.model.OrderItem(p.id, 2.0))
        val order = orderService.createOrder(items)
        assertNotNull(order)
        val updated = invService.findById(p.id)
        assertEquals(3.0, updated?.quantity)

        // cleanup
        try { java.io.File(invPath).delete(); java.io.File(ordPath).delete() } catch (_: Exception) {}
    }
}
