package ppp.payroll.business

import ppp.payroll.Wage
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*

interface PayDayStrategy {
    fun isPayDayFor(wage: Wage, day: LocalDate): Boolean
}

class PayDayStrategyForWageHourlyRate : PayDayStrategy {

    override fun isPayDayFor(wage: Wage, day: LocalDate): Boolean {
        val previousPayDay: LocalDate? = findPreviousPayDay(wage.employeeId)
        if (previousPayDay == day) return false

        return day.dayOfWeek == DayOfWeek.FRIDAY
    }

    private fun findPreviousPayDay(employeeId: UUID): LocalDate? {
        return null
    }

}