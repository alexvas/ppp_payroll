package ppp.payroll

import java.util.*

data class SalesReceipt(
        override val employeeId: UUID,
        val id: UUID,
        val amount: Int,
        var commissionPayed: Boolean = false
): EmployeeFeature
