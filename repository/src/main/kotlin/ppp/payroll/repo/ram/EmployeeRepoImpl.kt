package ppp.payroll.repo.ram

import ppp.payroll.Employee
import ppp.payroll.EmployeeRepo
import java.util.*


class EmployeeRepoImpl : EmployeeRepo {

    private val employees: MutableMap<UUID, Employee> = LinkedHashMap()

    private val modificationLock: Any = Any()

    private val removalListeners: MutableList<EmployeeRepo.RemovalListener> = mutableListOf()

    override fun add(employee: Employee) {
        synchronized(modificationLock) {
            doAdd(employee)
        }
    }

    override fun get(employeeId: UUID): Employee? {
        return employees[employeeId]
    }

    private fun doAdd(employee: Employee) {
        require(!employees.containsKey(employee.id)) {
            "It is not allowed to add any employee to the repo twice"
        }
        employees[employee.id] = employee
    }

    override fun update(employeeId: UUID, updater: EmployeeRepo.Updater) {
        synchronized(modificationLock) {
            doUpdate(employeeId, updater)
        }
    }

    private fun doUpdate(employeeId: UUID, updater: EmployeeRepo.Updater) {
        val original = employees[employeeId]
        require(original != null) {
            "It is not allowed to update employee not in the repo"
        }
        val updated = updater.update(original).copy(id = employeeId)
        employees[employeeId] = updated
    }

    override fun allEmployees() = employees.values.toList()

    override fun remove(employeeId: UUID) {
        synchronized(modificationLock) {
            doRemove(employeeId)
        }
    }

    private fun doRemove(id: UUID) {
        employees.remove(id)
        removalListeners.forEach { it.removed(id) }
    }

    override fun hasEmployee(employeeId: UUID): Boolean = employees.containsKey(employeeId)

    override fun addRemovalListener(removalListener: EmployeeRepo.RemovalListener) {
        removalListeners += removalListener
    }
}