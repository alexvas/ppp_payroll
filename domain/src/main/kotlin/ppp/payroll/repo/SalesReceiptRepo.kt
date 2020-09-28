package ppp.payroll.repo

import ppp.payroll.SalesReceipt
import java.util.*
import kotlin.collections.LinkedHashSet

object SalesReceiptRepo {

    private val items: MutableSet<SalesReceipt> = LinkedHashSet()

    private val modificationLock: Any = Any()

    fun add(receipt: SalesReceipt) {
        synchronized(modificationLock) {
            doAdd(receipt)
        }
    }

    private fun doAdd(receipt: SalesReceipt) {
        require(!items.contains(receipt)) {
            "It is not allowed to add any sales receipt to the repo twice"
        }
        require(
                EmployeeRepo.hasEmployee(receipt.employeeId)
        ) {
            "Employee for receipt $receipt not found"
        }
        items.add(receipt)
    }

    fun allItems() = items.toList()

    fun itemsFor(employeeId: UUID) = items.asSequence()
            .filter { it.employeeId == employeeId }
            .toList()

}
