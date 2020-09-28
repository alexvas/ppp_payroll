package ppp.payroll.repo

import ppp.payroll.Employee

object EmployeeRepo {

    private val employees: MutableSet<Employee> = LinkedHashSet()

    fun add(employee: Employee) {
        employees += employee
    }

    fun list() = employees.toList()

}