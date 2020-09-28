package ppp.payroll.repo

import ppp.payroll.UnionCharge
import java.util.*
import kotlin.collections.LinkedHashSet

object UnionRepo {

    private val items: MutableSet<UnionCharge> = LinkedHashSet()

    private val modificationLock: Any = Any()

    fun add(charge: UnionCharge) {
        synchronized(modificationLock) {
            doAdd(charge)
        }
    }

    private fun doAdd(charge: UnionCharge) {
        require(items.none { it.employeeId == charge.employeeId }) {
            "It is not allowed to charge any employee twice"
        }
        require(
                EmployeeRepo.hasEmployee(charge.employeeId)
        ) {
            "Employee for charge $charge not found"
        }
        items.add(charge)
    }

    fun allItems() = items.toList()

    fun itemsFor(employeeId: UUID) = items.asSequence()
            .filter { it.employeeId == employeeId }
            .toList()

}
