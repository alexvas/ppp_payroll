package ppp.payroll.business

import ppp.payroll.*

data class EmployeeCreation(
        val employee: Employee,
        val employeeDetail: EmployeeDetail,
        val wage: Wage,
        val payMethod: PayMethodDirect,
) {
    var membership: UnionMembership? = null
}
