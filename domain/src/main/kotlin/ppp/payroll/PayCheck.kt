package ppp.payroll

import java.time.LocalDate
import java.util.*

data class PayCheck(
        override val employeeId: UUID,
        val date: LocalDate,
        val amount: Int,
        val log: String,
): EmployeeFeature
