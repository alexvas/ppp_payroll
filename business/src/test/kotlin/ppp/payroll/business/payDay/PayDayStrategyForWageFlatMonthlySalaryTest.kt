package ppp.payroll.business.payDay

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ppp.payroll.Wage
import ppp.payroll.WageFlatMonthlySalary
import ppp.payroll.business.PayDayStrategy
import ppp.payroll.business.PayDayStrategyForWageFlatMonthlySalary
import java.time.LocalDate
import java.util.*

val LAST_WORKING_DAY_OF_WINTER: LocalDate = LocalDate.of(2020, 2, 28)

class PayDayStrategyForWageFlatMonthlySalaryTest {

    @Test
    fun `в последний рабочий день месяца надо платить зарплату новым работникам на фиксированной ставке`() {
        // given
        val strategy: PayDayStrategy = PayDayStrategyForWageFlatMonthlySalary(emptyPayCheckRepo())
        val wage: Wage = WageFlatMonthlySalary(UUID.randomUUID(), 17)

        // when
        val result = strategy.isPayDayFor(wage, LAST_WORKING_DAY_OF_WINTER)

        // then
        assertThat(result).isTrue
    }

    @Test
    fun `_не_ в последний рабочий день месяца зарплату новым работникам на фиксированной ставке платить не надо`() {
        // given
        val strategy: PayDayStrategy = PayDayStrategyForWageFlatMonthlySalary(emptyPayCheckRepo())
        val wage: Wage = WageFlatMonthlySalary(UUID.randomUUID(), 17)

        // when
        val result = strategy.isPayDayFor(wage, LAST_WORKING_DAY_OF_WINTER.minusDays(5))

        // then
        assertThat(result).isFalse
    }


}