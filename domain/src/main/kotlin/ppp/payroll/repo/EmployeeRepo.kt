package ppp.payroll.repo

import ppp.payroll.Employee
import java.util.*
import kotlin.collections.LinkedHashSet

object EmployeeRepo {
    fun interface RemovalListener {
        fun removed(employeeId: UUID)
    }

    private val employees: MutableSet<Employee> = LinkedHashSet()

    private val modificationLock: Any = Any()

    private val removalListeners: MutableList<RemovalListener> = mutableListOf()

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
        employees.removeIf { id == it.id }
        removalListeners.forEach { it.removed(id) }
    }

    fun hasEmployee(userId: UUID): Boolean = employees.any { it.id == userId }

    fun addRemovalListener(removalListener: RemovalListener) {
        removalListeners += removalListener
    }
}