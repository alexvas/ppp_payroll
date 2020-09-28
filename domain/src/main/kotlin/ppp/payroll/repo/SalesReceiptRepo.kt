package ppp.payroll.repo

import ppp.payroll.SalesReceipt
import java.util.*
import kotlin.collections.LinkedHashSet

object SalesReceiptRepo {

    private val salesReceipts: MutableSet<SalesReceipt> = LinkedHashSet()

    private val modificationLock: Any = Any()

    fun add(receipt: SalesReceipt) {
        synchronized(modificationLock) {
            doAdd(receipt)
        }
    }

    private fun doAdd(receipt: SalesReceipt) {
        require(!salesReceipts.contains(receipt)) {
            "It is not allowed to add any sales receipt to the repo twice"
        }
        require(
                EmployeeRepo.hasEmployee(receipt.employeeId)
        ) {
            "Employee for receipt $receipt not found"
        }
        salesReceipts.add(receipt)
    }

    fun allReceipts() = salesReceipts.toList()

    fun receiptsFor(employeeId: UUID) = salesReceipts.asSequence()
            .filter { it.employeeId == employeeId }
            .toList()

}
