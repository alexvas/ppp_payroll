package ppp.payroll.repo

import ppp.payroll.Employee
import java.util.*
import kotlin.collections.LinkedHashSet

object EmployeeRepo {

    private val employees: MutableSet<Employee> = LinkedHashSet()

    private val addLock: Any = Any()

    fun add(employee: Employee) {
        synchronized(addLock) {
            doAdd(employee)
        }
    }

    private fun doAdd(employee: Employee) {
        require(!employees.contains(employee)) {
            "It is not allowed to add any employee to the repo twice"
        }
        employees += employee
    }

    fun list() = employees.toList()

    fun remove(id: UUID) {
        employees.removeIf { id == it.id }
    }
}