package ppp.payroll.business

import ppp.payroll.PayCheckRepo
import ppp.payroll.Wage
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Period
import java.time.temporal.TemporalAdjusters

const val DAYS_IN_WEEK = 7

fun LocalDate.isFriday() = dayOfWeek == DayOfWeek.FRIDAY

interface PayDayStrategy {
    fun isPayDayFor(wage: Wage, day: LocalDate): Boolean
}

abstract class PayDayStrategyBase(private val payCheckRepo: PayCheckRepo) : PayDayStrategy {
    override fun isPayDayFor(wage: Wage, day: LocalDate): Boolean {
        val lastPayDay = payCheckRepo.lastPayDay(wage.employeeId)
        if (day == lastPayDay) return false
        return doFindIsPayDayFor(wage, day)
    }

    internal abstract fun doFindIsPayDayFor(wage: Wage, day: LocalDate): Boolean

}

class PayDayStrategyForWageHourlyRate(payCheckRepo: PayCheckRepo) : PayDayStrategyBase(payCheckRepo) {

    override fun doFindIsPayDayFor(wage: Wage, day: LocalDate) = day.isFriday()

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
        val lastPayDay = payCheckRepo.lastPayDay(wage.employeeId)
        return if (lastPayDay == null)
            day.isFriday()
        else
            Period.between(lastPayDay, day).days > DAYS_IN_WEEK
    }

}
