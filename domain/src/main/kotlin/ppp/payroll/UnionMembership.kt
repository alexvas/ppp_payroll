package ppp.payroll

import java.util.*

data class UnionMembership(
        override val employeeId: UUID,
        val dueRate: Int,
): EmployeeFeature
