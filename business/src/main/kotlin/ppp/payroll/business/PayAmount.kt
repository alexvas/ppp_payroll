package ppp.payroll.business

import ppp.payroll.*
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import kotlin.math.roundToInt

interface PayAmountStrategy {
    fun amount(wage: Wage, day: LocalDate): Int
}

class PayAmountStrategyForWageFlatMonthlySalary(private val payCheckRepo: PayCheckRepo): PayAmountStrategy {

    override fun amount(wage: Wage, day: LocalDate): Int {
        require(wage is WageFlatMonthlySalary) {
            "unsupported wage type: ${wage.type}"
        }
        val lastPayDay = payCheckRepo.lastPayDay(wage.employeeId)
        val startDate = lastPayDay ?: wage.startDay

        val employeeWorkedPreliminary = countWorkDays(startDate, day)
        // a settlement is always final
        val employeeWorked = if (lastPayDay == null)
            // also count start date in case of no last payment
            employeeWorkedPreliminary + 1
        else
            employeeWorkedPreliminary

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

fun WageCommission.toWageFlatMonthlySalary() = WageFlatMonthlySalary(employeeId, monthlySalary, startDay)


class PayAmountStrategyForWageCommission(payCheckRepo: PayCheckRepo, private val receiptRepo: SalesReceiptRepo) : PayAmountStrategy {

    private val flatMonthlySalaryStrategy = PayAmountStrategyForWageFlatMonthlySalary(payCheckRepo)

    override fun amount(wage: Wage, day: LocalDate): Int {
        require(wage is WageCommission) {
            "unsupported wage type: ${wage.type}"
        }
        return flatMonthlySalaryStrategy.amount(wage.toWageFlatMonthlySalary(), day) + salesCommission(wage)
    }

    private fun salesCommission(wage: WageCommission): Int {
        val unpaid = receiptRepo.unpaidReceipts(wage.employeeId)
        if (unpaid.isEmpty()) return 0
        val commission = (unpaid.sumBy { it.amount } * wage.commission / 100).roundToInt()
        receiptRepo.markReceiptsAsPaid(unpaid)
        return commission
    }

}