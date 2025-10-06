package huertohogar.ui

import huertohogar.model.Product
import huertohogar.model.Order
import huertohogar.model.OrderItem
import huertohogar.service.InventoryService
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object AsciiUI {
    fun clearScreen() {
        // ANSI clear
        print("\u001b[H\u001b[2J")
        System.out.flush()
    }

    fun header(title: String) {
        val width = 56
        println("╔" + "═".repeat(width) + "╗")
        val mid = "║ ${title.padEnd(width - 2)} ║"
        println(mid)
        println("╚" + "═".repeat(width) + "╝")
    }

    fun boxedMenu(options: List<String>) {
        val width = 56
        println("┌" + "─".repeat(width) + "┐")
        for ((i, opt) in options.withIndex()) {
            val line = "${i + 1}) $opt"
            println("│ ${line.padEnd(width - 2)} │")
        }
        println("└" + "─".repeat(width) + "┘")
    }

    fun printProducts(products: List<Product>) {
        val idW = 10
        val nameW = 24
        val priceW = 8
        println("+" + "-".repeat(idW) + "+" + "-".repeat(nameW) + "+" + "-".repeat(priceW) + "+" + "-".repeat(8) + "+")
        println(String.format("| %-${idW - 1}s| %-${nameW - 1}s| %${priceW - 1}s| %7s|", "ID", "Nombre", "Precio", "Cantidad"))
        println("+" + "-".repeat(idW) + "+" + "-".repeat(nameW) + "+" + "-".repeat(priceW) + "+" + "-".repeat(8) + "+")
        for (p in products) {
            println(String.format("| %-${idW - 1}.8s| %-${nameW - 1}.22s| %${priceW - 1}.2f| %7.2f|", p.id, p.name, p.price, p.quantity))
        }
        println("+" + "-".repeat(idW) + "+" + "-".repeat(nameW) + "+" + "-".repeat(priceW) + "+" + "-".repeat(8) + "+")
    }

    fun printReceipt(order: Order, inventoryService: InventoryService) {
        val df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val time = Instant.ofEpochMilli(order.timestamp).atZone(ZoneId.systemDefault()).format(df)
        println("\n===== RECIBO HuertoHogar =====")
        println("ID: ${order.id}")
        println("Fecha: $time")
        println("-------------------------------")
        println(String.format("%-24s %6s %8s %8s", "Producto", "Cant", "Precio", "Subt"))
        var total = 0.0
        for (it in order.items) {
            val p = inventoryService.findById(it.productId)
            val name = p?.name ?: it.productId
            val price = p?.price ?: 0.0
            val subtotal = price * it.quantity
            total += subtotal
            println(String.format("%-24.22s %6.2f %8.2f %8.2f", name, it.quantity, price, subtotal))
        }
        println("-------------------------------")
        println(String.format("%31s %8.2f", "TOTAL:", total))
        println(String.format("%31s %8.2f", "PAGADO:", order.paid))
        println(String.format("%31s %8.2f", "CAMBIO:", order.paid - total))
        println("===============================\n")
    }
}
