package ppp.payroll

import java.util.*

data class UnionCharge(
        override val employeeId: UUID,
        val amount: Int,
): EmployeeFeature
