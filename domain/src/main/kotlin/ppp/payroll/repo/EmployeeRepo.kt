package ppp.payroll.repo

import ppp.payroll.Employee
import java.util.*
import kotlin.collections.LinkedHashSet

object EmployeeRepo {

    private val employees: MutableSet<Employee> = LinkedHashSet()

    private val modificationLock: Any = Any()

    fun add(employee: Employee) {
        synchronized(modificationLock) {
            doAdd(employee)
        }
    }

    private fun doAdd(employee: Employee) {
        require(!employees.contains(employee)) {
            "It is not allowed to add any employee to the repo twice"
        }
        employees += employee
    }

    fun allEmployees() = employees.toList()

    fun remove(id: UUID) {
        synchronized(modificationLock) {
            doRemove(id)
        }
    }

    private fun doRemove(id: UUID) {
        require(timeCardRepo.featuresFor(id).isEmpty()) {
            "It is not allowed to remove employee with id $id: he or she has time card(s)"
        }
        require(salesReceiptRepo.featuresFor(id).isEmpty()) {
            "It is not allowed to remove employee with id $id: he or she has sales receipt(s)"
        }
        require(unionChargeRepo.getFeatureFor(id) == null) {
            "It is not allowed to remove employee with id $id: he or she has union charge"
        }
        require(payMethodRepo.getFeatureFor(id) == null) {
            "It is not allowed to remove employee with id $id: he or she has pay method"
        }

        employees.removeIf { id == it.id }
    }

    fun hasEmployee(userId: UUID): Boolean = employees.any { it.id == userId }
}