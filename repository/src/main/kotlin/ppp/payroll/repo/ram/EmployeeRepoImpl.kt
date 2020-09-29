package ppp.payroll.repo.ram

import ppp.payroll.Employee
import ppp.payroll.EmployeeRepo
import java.util.*
import kotlin.collections.LinkedHashSet


class EmployeeRepoImpl : EmployeeRepo {

    private val employees: MutableSet<Employee> = LinkedHashSet()

    private val modificationLock: Any = Any()

    private val removalListeners: MutableList<EmployeeRepo.RemovalListener> = mutableListOf()

    override fun add(employee: Employee) {
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

    override fun allEmployees() = employees.toList()

    override fun remove(id: UUID) {
        synchronized(modificationLock) {
            doRemove(id)
        }
    }

    private fun doRemove(id: UUID) {
        employees.removeIf { id == it.id }
        removalListeners.forEach { it.removed(id) }
    }

    override fun hasEmployee(userId: UUID): Boolean = employees.any { it.id == userId }

    override fun addRemovalListener(removalListener: EmployeeRepo.RemovalListener) {
        removalListeners += removalListener
    }
}