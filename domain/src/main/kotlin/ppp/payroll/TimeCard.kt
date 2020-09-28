package ppp.payroll

import java.time.Instant
import java.util.*

data class TimeCard(val employeeId: UUID, val date: Instant, val hours: Int)
