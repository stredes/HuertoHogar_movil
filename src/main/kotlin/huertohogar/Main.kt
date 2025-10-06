package huertohogar

import huertohogar.repo.InventoryRepository
import huertohogar.repo.OrdersRepository
import huertohogar.service.InventoryService
import huertohogar.service.OrderService
import huertohogar.service.SalesReportService
import java.util.Scanner
import huertohogar.ui.AsciiUI

fun main() {
    val repo = InventoryRepository()
    val service = InventoryService(repo)
    val scanner = Scanner(System.`in`)

    AsciiUI.clearScreen()
    AsciiUI.header("HuertoHogar — Verdulería móvil")

    val ordersRepo = OrdersRepository()
    val orderService = OrderService(ordersRepo, repo)
    // No PIN required (modo sin PIN)

    loop@ while (true) {
        AsciiUI.boxedMenu(listOf("Listar productos","Agregar producto","Actualizar cantidad","Eliminar producto","Crear pedido","Listar pedidos","Buscar producto por nombre","Reporte ventas diario","Salir"))
        print("Opción: ")
        when (scanner.nextLine().trim()) {
            "1" -> {
                val list = service.listProducts()
                if (list.isEmpty()) println("Inventario vacío")
                else list.forEach { p -> println("${p.id} | ${p.name} — €${"%.2f".format(p.price)} — qty:${p.quantity}") }
            }
            "2" -> {
                print("Nombre: ")
                val name = scanner.nextLine().trim()
                print("Precio: ")
                val price = scanner.nextLine().toDoubleOrNull() ?: 0.0
                print("Cantidad: ")
                val qty = scanner.nextLine().toDoubleOrNull() ?: 0.0
                val p = service.addProduct(name, price, qty)
                println("Producto agregado: ${p.id}")
            }
            "3" -> {
                print("ID producto: ")
                val id = scanner.nextLine().trim()
                print("Delta cantidad (+/-): ")
                val delta = scanner.nextLine().toDoubleOrNull() ?: 0.0
                val ok = service.updateQuantity(id, delta)
                println(if (ok) "Cantidad actualizada" else "Producto no encontrado")
            }
            "4" -> {
                print("ID producto: ")
                val id = scanner.nextLine().trim()
                print("Confirma eliminar producto $id ? (s/N): ")
                val conf = scanner.nextLine().trim().lowercase()
                if (conf == "s" || conf == "si") {
                    val ok = service.removeProduct(id)
                    println(if (ok) "Producto eliminado" else "Producto no encontrado")
                } else println("Operación cancelada")
            }
            "5" -> {
                // Crear pedido: pedir varias líneas productId:qty, vacío para terminar
                println("Crear pedido — introduce líneas 'productId cantidad' (cantidad puede ser decimal, ej 0.5) y deja vacío para terminar")
                val items = mutableListOf<huertohogar.model.OrderItem>()
                while (true) {
                    print("productoId cantidad: ")
                    val line = scanner.nextLine().trim()
                    if (line.isEmpty()) break
                    val parts = line.split(Regex("\\s+"))
                    if (parts.size < 2) { println("Formato inválido"); continue }
                    val pid = parts[0]
                    val qty = parts[1].toDoubleOrNull() ?: -1.0
                    if (qty <= 0.0) { println("Cantidad inválida"); continue }
                    items.add(huertohogar.model.OrderItem(pid, qty))
                }
                if (items.isEmpty()) { println("Pedido cancelado") }
                else {
                    val order = orderService.createOrder(items)
                    if (order == null) println("No se pudo crear el pedido (stock insuficiente o producto no existe)")
                    else {
                    println("Pedido creado: ${order.id} — total €${"%.2f".format(order.total)}")
                        // Cobro rápido
                        print("Monto entregado por el cliente: €")
                        val paid = scanner.nextLine().toDoubleOrNull() ?: 0.0
                        val change = paid - order.total
                        if (paid < order.total) println("Pago insuficiente: faltan €${"%.2f".format(order.total - paid)}")
                        else println("Cambio: €${"%.2f".format(change)}")
                        // Guardar pago en el pedido (crear nuevo objeto con paid)
                        val paidOrder = order.copy(paid = paid)
                        // replace the last saved order with paidOrder
                        val all = ordersRepo.load()
                        all.orders.removeIf { it.id == order.id }
                        all.orders.add(paidOrder)
                        ordersRepo.save(all)
                        // show ascii receipt
                        AsciiUI.printReceipt(paidOrder, service)
                    }
                }
            }
            "6" -> {
                val orders = orderService.listOrders()
                if (orders.isEmpty()) println("No hay pedidos")
                else orders.forEach { o -> println("${o.id} — items:${o.items.size} — total: €${"%.2f".format(o.total)} — pagado: €${"%.2f".format(o.paid)}") }
            }
            "8" -> {
                // Sales report
                val reportService = SalesReportService(ordersRepo)
                val summaries = reportService.dailySummary()
                if (summaries.isEmpty()) println("No hay datos de ventas")
                else summaries.forEach { s -> println("${s.date} — pedidos:${s.orders} — items:${s.items} — total: €${"%.2f".format(s.total)}") }
            }
            "7" -> {
                print("Nombre a buscar: ")
                val q = scanner.nextLine().trim().lowercase()
                val found = service.listProducts().filter { it.name.lowercase().contains(q) }
                if (found.isEmpty()) println("No se encontraron productos")
                else found.forEach { p -> println("${p.id} | ${p.name} — €${"%.2f".format(p.price)} — qty:${p.quantity}") }
            }
            "9" -> {
                print("Ruta CSV (por defecto sales.csv): ")
                val path = scanner.nextLine().trim().ifEmpty { "sales.csv" }
                val reportService = SalesReportService(ordersRepo)
                reportService.exportCsv(path)
                println("Exportado $path")
            }
            "10" -> break@loop
            else -> println("Opción inválida")
        }
    }

    println("Hasta luego — HuertoHogar")
}
