package ppp.payroll.business

import ppp.payroll.PayCheckRepo
import ppp.payroll.Wage
import ppp.payroll.WageFlatMonthlySalary
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

interface PayAmountStrategy {
    fun amount(wage: Wage, day: LocalDate): Int
}

class PayAmountStrategyForWageFlatMonthlySalary(private val payCheckRepo: PayCheckRepo): PayAmountStrategy {

    override fun amount(wage: Wage, day: LocalDate): Int {
        require(wage is WageFlatMonthlySalary) {
            "unsupported wage type: ${wage.type}"
        }
        val startDate = payCheckRepo.findPreviousPayDay(wage.employeeId) ?: wage.startDate

        val employeeWorked: Int = countWorkDays(startDate, day)

        val firstDayOfMonth = day.with(TemporalAdjusters.firstDayOfMonth())
        val lastDayOfMonth = day.with(TemporalAdjusters.lastDayOfMonth())
        val workDaysInMonth: Int = countWorkDays(firstDayOfMonth, lastDayOfMonth)
        if (employeeWorked == workDaysInMonth) {
            return wage.monthlySalary
        }
        check(employeeWorked < workDaysInMonth) {
            "employee worked $employeeWorked, but there are only $workDaysInMonth work days in month"
        }

        return (wage.monthlySalary * employeeWorked) / workDaysInMonth
    }

    private fun countWorkDays(startDate: LocalDate, day: LocalDate): Int {
        require(!startDate.isAfter(day)) {
            "start date $startDate is in future relative to pay day $day"
        }
        var counter = 0
        var endDate = day
        while (startDate.isBefore(endDate)) {
            if (!isHoliday(endDate)) {
                ++counter
            }
            endDate = endDate.minusDays(1)
        }
        return counter
    }

}