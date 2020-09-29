package ppp.payroll

import java.time.Instant
import java.util.*

data class PayCheck(
        override val employeeId: UUID,
        val date: Instant,
        val amount: Int,
        val log: String,
): EmployeeFeature
