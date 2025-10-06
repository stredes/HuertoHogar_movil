package huertohogar.service

import huertohogar.repo.OrdersRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class SalesSummary(val date: LocalDate, val total: Double, val orders: Int, val items: Int)

class SalesReportService(private val ordersRepo: OrdersRepository) {
    fun dailySummary(): List<SalesSummary> {
        val orders = ordersRepo.load().orders
        val zone = ZoneId.systemDefault()
        val grouped = orders.groupBy { o ->
            Instant.ofEpochMilli(o.timestamp).atZone(zone).toLocalDate()
        }

        return grouped.map { (date, ordersOfDay) ->
            val total = ordersOfDay.sumOf { it.total }
            val items = ordersOfDay.sumOf { it.items.sumOf { it.quantity }.toInt() }
            SalesSummary(date, total, ordersOfDay.size, items)
        }.sortedBy { it.date }
    }

    fun exportCsv(path: String) {
        val orders = ordersRepo.load().orders
        val sb = StringBuilder()
        sb.append("orderId,timestamp,total,paid,customer,items\n")
        for (o in orders) {
            val itemsStr = o.items.joinToString("|") { "${it.productId}:${it.quantity}" }
            sb.append("${o.id},${o.timestamp},${o.total},${o.paid},${o.customer ?: ""},\"${itemsStr}\"\n")
        }
        java.io.File(path).writeText(sb.toString())
    }
}
