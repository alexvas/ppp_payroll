package ppp.payroll

import java.time.Instant
import java.util.*

data class SalesReceipt(val employeeId: UUID, val date: Instant, val amount: Int)
