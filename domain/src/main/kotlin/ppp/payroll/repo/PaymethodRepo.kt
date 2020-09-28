package ppp.payroll.repo

import ppp.payroll.PayMethod
import java.util.*
import kotlin.collections.LinkedHashSet

object PaymethodRepo {

    private val items: MutableSet<PayMethod> = LinkedHashSet()

    private val modificationLock: Any = Any()

    fun add(payMethod: PayMethod) {
        synchronized(modificationLock) {
            doAdd(payMethod)
        }
    }

    private fun doAdd(payMethod: PayMethod) {
        require(items.none { it.employeeId == payMethod.employeeId }) {
            "It is not allowed to pay any employee twice"
        }
        require(
                EmployeeRepo.hasEmployee(payMethod.employeeId)
        ) {
            "Employee for payMethod $payMethod not found"
        }
        items.add(payMethod)
    }

    fun allItems() = items.toList()

    fun itemsFor(employeeId: UUID) = items.asSequence()
            .filter { it.employeeId == employeeId }
            .toList()

}
