package www.payroll.business

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ppp.payroll.Wage
import ppp.payroll.WageHourlyRate
import ppp.payroll.business.PayDayStrategy
import ppp.payroll.business.PayDayStrategyForWageHourlyRate
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*

class PayDayStrategyForWageHourlyRateTest {


    @Test
    fun `проверяем, что по пятницам надо платить зарплату новым работникам на почасовой ставке`() {
        // given
        val strategy: PayDayStrategy = PayDayStrategyForWageHourlyRate()
        val wage: Wage = WageHourlyRate(UUID.randomUUID(), 17)
        val day = LocalDate.now().with(DayOfWeek.FRIDAY)

        // when
        val result = strategy.isPayDayFor(wage, day)

        // then
        assertThat(result).isTrue
    }

    @Test
    fun `проверяем, что по субботам платить зарплату новым работникам на почасовой ставке _не_ надо`() {
        // given
        val strategy: PayDayStrategy = PayDayStrategyForWageHourlyRate()
        val wage: Wage = WageHourlyRate(UUID.randomUUID(), 17)
        val day = LocalDate.now().with(DayOfWeek.SATURDAY)

        // when
        val result = strategy.isPayDayFor(wage, day)

        // then
        assertThat(result).isFalse
    }

}