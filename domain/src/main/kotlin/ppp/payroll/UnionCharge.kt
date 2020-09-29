package ppp.payroll

import java.time.Instant
import java.util.*

data class UnionCharge(
        override val employeeId: UUID,
        val amount: Int,
        val issued: Instant = Instant.now(),
        val paid: Boolean = false,
) : EmployeeFeature
