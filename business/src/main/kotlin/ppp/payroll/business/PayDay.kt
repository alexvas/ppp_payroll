package ppp.payroll.business

import ppp.payroll.PayCheckRepo
import ppp.payroll.Wage
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.*

interface PayDayStrategy {
    fun isPayDayFor(wage: Wage, day: LocalDate): Boolean
}

abstract class PayDayStrategyBase(private val payCheckRepo: PayCheckRepo) : PayDayStrategy {
    override fun isPayDayFor(wage: Wage, day: LocalDate): Boolean {
        val previousPayDay: LocalDate? = findPreviousPayDay(wage.employeeId)
        if (previousPayDay == day) return false
        return doFindIsPayDayFor(wage, day)
    }

    internal abstract fun doFindIsPayDayFor(wage: Wage, day: LocalDate): Boolean

    private fun findPreviousPayDay(employeeId: UUID) =
            payCheckRepo.featuresFor(employeeId).asSequence()
                    .map { it.date }
                    .maxOrNull()

}

class PayDayStrategyForWageHourlyRate(payCheckRepo: PayCheckRepo) : PayDayStrategyBase(payCheckRepo) {

    override fun doFindIsPayDayFor(wage: Wage, day: LocalDate): Boolean {
        return day.dayOfWeek == DayOfWeek.FRIDAY
    }

}

class PayDayStrategyForWageFlatMonthlySalary(payCheckRepo: PayCheckRepo) : PayDayStrategyBase(payCheckRepo) {

    override fun doFindIsPayDayFor(wage: Wage, day: LocalDate): Boolean {
        return day == findLastWorkingDayOfMonth(day)
    }

    private fun findLastWorkingDayOfMonth(theSameMonthDay: LocalDate): LocalDate {
        var day: LocalDate = theSameMonthDay.with(TemporalAdjusters.lastDayOfMonth())
        while (isHoliday(day))
            day = day.minusDays(1)

        return day
    }

    private fun isHoliday(day: LocalDate): Boolean {
        // for the sake of simplicity do not account for public holidays
        val dayOfWeek = day.dayOfWeek
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY
    }
}