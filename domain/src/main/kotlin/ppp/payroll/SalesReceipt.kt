package ppp.payroll

import ppp.payroll.repo.EmployeeFeature
import java.time.Instant
import java.util.*

data class SalesReceipt(override val employeeId: UUID, val date: Instant, val amount: Int): EmployeeFeature
