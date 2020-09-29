package ppp.payroll

import java.time.Instant
import java.util.*

data class TimeCard(override val employeeId: UUID, val date: Instant, val hours: Int): EmployeeFeature
