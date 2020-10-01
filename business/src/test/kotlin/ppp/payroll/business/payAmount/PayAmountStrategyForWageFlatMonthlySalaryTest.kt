package ppp.payroll.business.payAmount

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import ppp.payroll.Wage
import ppp.payroll.WageFlatMonthlySalary
import ppp.payroll.business.*
import ppp.payroll.business.payDay.LAST_WORKING_DAY_OF_WINTER
import java.time.LocalDate
import java.util.*

class PayAmountStrategyForWageFlatMonthlySalaryTest {
    // given
    private val employeeId = UUID.randomUUID()

    @Test
    fun `первая зарплата исчисляется со дня принятия на работу, включая этот день`() {
        // given
        val startWorkDay = LAST_WORKING_DAY_OF_WINTER.minusDays(3)
        val payCheckRepo = noPayment()
        val strategy: PayAmountStrategy = PayAmountStrategyForWageFlatMonthlySalary(payCheckRepo)
        val wage: Wage = WageFlatMonthlySalary(employeeId, 20000, startWorkDay)

        // when
        val result = strategy.amount(wage, LAST_WORKING_DAY_OF_WINTER)

        // then
        assertThat(result).isEqualTo(4000)
        payCheckRepo.lastPayDayWasCalledWith(employeeId)
    }

    @Test
    fun `после смены ставки зарплата исчисляется, начиная со следующего дня`() {
        // given
        val changeWageDay = LAST_WORKING_DAY_OF_WINTER.minusDays(3)
        // при смене ставки с работником был произведён расчёт
        val payCheckRepo = givenThereWasAPaymentOn(changeWageDay)
        val strategy: PayAmountStrategy = PayAmountStrategyForWageFlatMonthlySalary(payCheckRepo)
        val wage: Wage = WageFlatMonthlySalary(employeeId, 20000, changeWageDay)

        // when
        val result = strategy.amount(wage, LAST_WORKING_DAY_OF_WINTER)

        // then
        assertThat(result).isEqualTo(3000)
        payCheckRepo.lastPayDayWasCalledWith(employeeId)
    }

    @Test
    fun `обычно работник получает месячную зарплату целиком`() {
        // given
        // предыдущий расчёт произвели в последний рабочий день предыдущего месяца
        val lastPayDayOfPreviousMonth = LocalDate.of(2020, 1, 31)
        val payCheckRepo = givenThereWasAPaymentOn(lastPayDayOfPreviousMonth)
        val strategy: PayAmountStrategy = PayAmountStrategyForWageFlatMonthlySalary(payCheckRepo)
        // а работает работник тут уже давно
        val recruitmentDay = LocalDate.of(2000, 1, 1)
        val monthlySalary = 3141
        val wage: Wage = WageFlatMonthlySalary(employeeId, monthlySalary, recruitmentDay)

        // when
        val result = strategy.amount(wage, LAST_WORKING_DAY_OF_WINTER)

        // then
        assertThat(result).isEqualTo(monthlySalary)
        payCheckRepo.lastPayDayWasCalledWith(employeeId)
    }

    @Test
    fun `получим ошибку, если день принятия на работу находится в будущем`() {
        // given
        val payCheckRepo = noPayment()
        val strategy: PayAmountStrategy = PayAmountStrategyForWageFlatMonthlySalary(payCheckRepo)
        val wage: Wage = WageFlatMonthlySalary(employeeId, 1)

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
