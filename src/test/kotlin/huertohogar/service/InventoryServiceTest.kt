package huertohogar.service

import huertohogar.repo.InventoryRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InventoryServiceTest {
    @Test
    fun testAddAndUpdate() {
        val repo = InventoryRepository("test_inventory.json")
        // ensure clean
        try { java.io.File("test_inventory.json").delete() } catch (_: Exception) {}
        val service = InventoryService(repo)
    val p = service.addProduct("Tomate", 2.5, 10.0)
        assertEquals("Tomate", p.name)
    val ok = service.updateQuantity(p.id, -2.0)
        assertTrue(ok)
        val fetched = service.findById(p.id)
        assertEquals(8.0, fetched?.quantity)
        // cleanup
        try { java.io.File("test_inventory.json").delete() } catch (_: Exception) {}
    }
}
