package ppp.payroll.business

import ppp.payroll.PayCheckRepo
import ppp.payroll.Wage
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Period
import java.time.temporal.TemporalAdjusters
import java.util.*

const val DAYS_IN_WEEK = 7

interface PayDayStrategy {
    fun isPayDayFor(wage: Wage, day: LocalDate): Boolean
}

fun PayCheckRepo.findPreviousPayDay(employeeId: UUID) =
        featuresFor(employeeId).asSequence()
                .map { it.date }
                .maxOrNull()

abstract class PayDayStrategyBase(private val payCheckRepo: PayCheckRepo) : PayDayStrategy {
    override fun isPayDayFor(wage: Wage, day: LocalDate): Boolean {
        val previousPayDay = payCheckRepo.findPreviousPayDay(wage.employeeId)
        if (day == previousPayDay) return false
        return doFindIsPayDayFor(wage, day)
    }

    internal abstract fun doFindIsPayDayFor(wage: Wage, day: LocalDate): Boolean

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
}

class PayDayStrategyForWageCommission(private val payCheckRepo: PayCheckRepo) : PayDayStrategyBase(payCheckRepo) {

    override fun doFindIsPayDayFor(wage: Wage, day: LocalDate): Boolean {
        val isFriday = day.dayOfWeek == DayOfWeek.FRIDAY
        val previousPayDay = payCheckRepo.findPreviousPayDay(wage.employeeId) ?: return isFriday
        return Period.between(previousPayDay, day).days > DAYS_IN_WEEK
    }

}

fun isHoliday(day: LocalDate): Boolean {
    // for the sake of simplicity do not account for public holidays
    val dayOfWeek = day.dayOfWeek
    return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY
}
