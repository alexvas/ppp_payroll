package ppp.payroll

import ppp.payroll.repo.EmployeeFeature
import java.time.Instant
import java.util.*

data class TimeCard(override val employeeId: UUID, val date: Instant, val hours: Int): EmployeeFeature
