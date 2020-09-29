package ppp.payroll

import ppp.payroll.repo.EmployeeFeature
import java.util.*

data class UnionCharge(override val employeeId: UUID, val amount: Int): EmployeeFeature
