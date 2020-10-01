package ppp.payroll.business.payAmount

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import ppp.payroll.Wage
import ppp.payroll.WageCommission
import ppp.payroll.business.*
import ppp.payroll.business.payDay.LAST_WORKING_DAY_OF_WINTER
import java.time.LocalDate
import java.util.*

class PayAmountStrategyForWageCommissionTest {
    // given
    private val employeeId = UUID.randomUUID()

    @Nested
    inner class NoSales {

        @Test
        fun `первая зарплата исчисляется со дня принятия на работу, включая этот день`() {
            // given
            val startWorkDay = LAST_WORKING_DAY_OF_WINTER.minusDays(3)
            val payCheckRepo = noPayment()
            val receiptRepo = noSalesReceipt()
            val strategy: PayAmountStrategy = PayAmountStrategyForWageCommission(payCheckRepo, receiptRepo)
            val wage: Wage = WageCommission(employeeId, 20000, 15.0, startWorkDay)

            // when
            val result = strategy.amount(wage, LAST_WORKING_DAY_OF_WINTER)

            // then
            assertThat(result).isEqualTo(4000)
            payCheckRepo.lastPayDayWasCalledWith(employeeId)
            receiptRepo.unpaidReceiptsCalled(employeeId)
        }


        @Test
        fun `после смены ставки зарплата исчисляется, начиная со следующего дня`() {
            // given
            val changeWageDay = LAST_WORKING_DAY_OF_WINTER.minusDays(3)
            // при смене ставки с работником был произведён расчёт
            val payCheckRepo = givenThereWasAPaymentOn(changeWageDay)
            val receiptRepo = noSalesReceipt()
            val strategy: PayAmountStrategy = PayAmountStrategyForWageCommission(payCheckRepo, receiptRepo)
            val wage: Wage = WageCommission(employeeId, 20000, 15.0, changeWageDay)

            // when
            val result = strategy.amount(wage, LAST_WORKING_DAY_OF_WINTER)

            // then
            assertThat(result).isEqualTo(3000)
            payCheckRepo.lastPayDayWasCalledWith(employeeId)
            receiptRepo.unpaidReceiptsCalled(employeeId)
        }

        @Test
        fun `обычно работник получает месячную зарплату вместе с комиссией от продаж`() {
            // given
            // предыдущий расчёт произвели в последний рабочий день предыдущего месяца
            val lastPayDayOfPreviousMonth = LocalDate.of(2020, 1, 31)
            val payCheckRepo = givenThereWasAPaymentOn(lastPayDayOfPreviousMonth)
            val receiptRepo = noSalesReceipt()
            val strategy: PayAmountStrategy = PayAmountStrategyForWageCommission(payCheckRepo, receiptRepo)
            // а работает работник тут уже давно
            val recruitmentDay = LocalDate.of(2000, 1, 1)
            val monthlySalary = 3141
            val wage: Wage = WageCommission(employeeId, monthlySalary, 15.0, recruitmentDay)

            // when
            val result = strategy.amount(wage, LAST_WORKING_DAY_OF_WINTER)

            // then
            assertThat(result).isEqualTo(monthlySalary)
            payCheckRepo.lastPayDayWasCalledWith(employeeId)
            receiptRepo.unpaidReceiptsCalled(employeeId)
        }

        @Test
        fun `получим ошибку, если день принятия на работу находится в будущем`() {
            // given
            val payCheckRepo = noPayment()
            val receiptRepo = noSalesReceipt()
            val strategy: PayAmountStrategy = PayAmountStrategyForWageCommission(payCheckRepo, receiptRepo)
            val wage: Wage = WageCommission(employeeId, 1, 15.0)

            // when
            assertThatThrownBy {
                strategy.amount(wage, LAST_WORKING_DAY_OF_WINTER)
            }
                    // then
                    .isInstanceOf(RuntimeException::class.java)
                    .hasMessageContaining("future")
            payCheckRepo.lastPayDayWasCalledWith(employeeId)
        }

    }

    @Nested
    inner class SalesBeforeLastPayment {


        @Test
        fun `после смены ставки зарплата исчисляется, начиная со следующего дня`() {
            // given
            val changeWageDay = LAST_WORKING_DAY_OF_WINTER.minusDays(3)
            // при смене ставки с работником был произведён расчёт
            val payCheckRepo = givenThereWasAPaymentOn(changeWageDay)
            val receiptRepo = givenThereWasAnUnpaidSalesReceiptFor(employeeId)
            val strategy: PayAmountStrategy = PayAmountStrategyForWageCommission(payCheckRepo, receiptRepo)
            val wage: Wage = WageCommission(employeeId, 20000, 15.0, changeWageDay)

            // when
            val result = strategy.amount(wage, LAST_WORKING_DAY_OF_WINTER)

            // then
            assertThat(result).isEqualTo(3030)
            payCheckRepo.lastPayDayWasCalledWith(employeeId)
            receiptRepo.unpaidReceiptsCalledThenMarkedAsPaid(employeeId)
        }

        @Test
        fun `обычно работник получает месячную зарплату вместе с комиссией от продаж`() {
            // given
            // предыдущий расчёт произвели в последний рабочий день предыдущего месяца
            val lastPayDayOfPreviousMonth = LocalDate.of(2020, 1, 31)
            val payCheckRepo = givenThereWasAPaymentOn(lastPayDayOfPreviousMonth)
            val receiptRepo = givenThereWasAnUnpaidSalesReceiptFor(employeeId)
            val strategy: PayAmountStrategy = PayAmountStrategyForWageCommission(payCheckRepo, receiptRepo)
            // а работает работник тут уже давно
            val recruitmentDay = LocalDate.of(2000, 1, 1)
            val monthlySalary = 3141
            val wage: Wage = WageCommission(employeeId, monthlySalary, 15.0, recruitmentDay)

            // when
            val result = strategy.amount(wage, LAST_WORKING_DAY_OF_WINTER)

            // then
            assertThat(result).isEqualTo(monthlySalary + 30)
            payCheckRepo.lastPayDayWasCalledWith(employeeId)
            receiptRepo.unpaidReceiptsCalledThenMarkedAsPaid(employeeId)
        }

    }

    @Nested
    inner class SalesAfterLastPayment {


        @Test
        fun `после смены ставки зарплата исчисляется, начиная со следующего дня`() {
            // given
            val changeWageDay = LAST_WORKING_DAY_OF_WINTER.minusDays(3)
            // при смене ставки с работником был произведён расчёт
            val payCheckRepo = givenThereWasAPaymentOn(changeWageDay)
            val receiptRepo = noSalesReceipt()
            val strategy: PayAmountStrategy = PayAmountStrategyForWageCommission(payCheckRepo, receiptRepo)
            val wage: Wage = WageCommission(employeeId, 20000, 15.0, changeWageDay)

            // when
            val result = strategy.amount(wage, LAST_WORKING_DAY_OF_WINTER)

            // then
            assertThat(result).isEqualTo(3000)
            payCheckRepo.lastPayDayWasCalledWith(employeeId)
            receiptRepo.unpaidReceiptsCalled(employeeId)
        }

        @Test
        fun `обычно работник получает месячную зарплату вместе с комиссией от продаж`() {
            // given
            // предыдущий расчёт произвели в последний рабочий день предыдущего месяца
            val lastPayDayOfPreviousMonth = LocalDate.of(2020, 1, 31)
            val payCheckRepo = givenThereWasAPaymentOn(lastPayDayOfPreviousMonth)
            val receiptRepo = noSalesReceipt()
            val strategy: PayAmountStrategy = PayAmountStrategyForWageCommission(payCheckRepo, receiptRepo)
            // а работает работник тут уже давно
            val recruitmentDay = LocalDate.of(2000, 1, 1)
            val monthlySalary = 3141
            val wage: Wage = WageCommission(employeeId, monthlySalary, 15.0, recruitmentDay)

            // when
            val result = strategy.amount(wage, LAST_WORKING_DAY_OF_WINTER)

            // then
            assertThat(result).isEqualTo(monthlySalary)
            payCheckRepo.lastPayDayWasCalledWith(employeeId)
            receiptRepo.unpaidReceiptsCalled(employeeId)
        }

    }

}
