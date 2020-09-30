package www.payroll.business

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ppp.payroll.Wage
import ppp.payroll.WageFlatMonthlySalary
import ppp.payroll.business.PayDayStrategy
import ppp.payroll.business.PayDayStrategyForWageFlatMonthlySalary
import java.time.LocalDate
import java.util.*

class PayDayStrategyForWageFlatMonthlySalaryTest {

    @Test
    fun `в последний рабочий день месяца надо платить зарплату новым работникам на почасовой ставке`() {
        // given
        val strategy: PayDayStrategy = PayDayStrategyForWageFlatMonthlySalary(emptyPayCheckRepo())
        val wage: Wage = WageFlatMonthlySalary(UUID.randomUUID(), 17)
        val day = LocalDate.of(2020, 3, 31) // это был вторник

        // when
        val result = strategy.isPayDayFor(wage, day)

        // then
        assertThat(result).isTrue
    }

    @Test
    fun `_не_ в последний рабочий день месяца зарплату новым работникам на почасовой ставке платить не надо`() {
        // given
        val strategy: PayDayStrategy = PayDayStrategyForWageFlatMonthlySalary(emptyPayCheckRepo())
        val wage: Wage = WageFlatMonthlySalary(UUID.randomUUID(), 17)
        val day = LocalDate.of(2020, 3, 27) // это была пятница

        // when
        val result = strategy.isPayDayFor(wage, day)

        // then
        assertThat(result).isFalse
    }


}