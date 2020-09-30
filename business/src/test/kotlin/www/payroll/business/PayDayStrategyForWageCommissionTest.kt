package www.payroll.business

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ppp.payroll.Wage
import ppp.payroll.WageCommission
import ppp.payroll.business.PayDayStrategy
import ppp.payroll.business.PayDayStrategyForWageCommission
import java.time.LocalDate
import java.util.*

private val FRIDAY: LocalDate = LocalDate.of(2020, 3, 27)

class PayDayStrategyForWageCommissionTest {

    @Test
    fun `в пятницу надо платить зарплату новым работникам с комиссионными`() {
        // given
        val strategy: PayDayStrategy = PayDayStrategyForWageCommission(emptyPayCheckRepo())
        val wage: Wage = WageCommission(UUID.randomUUID(), 17, 3.0)

        // when
        val result = strategy.isPayDayFor(wage, FRIDAY)

        // then
        assertThat(result).isTrue
    }

    @Test
    fun `а _не_ в пятницу _не_ надо платить зарплату новым работникам с комиссионными`() {
        // given
        val strategy: PayDayStrategy = PayDayStrategyForWageCommission(emptyPayCheckRepo())
        val wage: Wage = WageCommission(UUID.randomUUID(), 17, 3.0)
        val day = FRIDAY.minusDays(1)

        // when
        val result = strategy.isPayDayFor(wage, day)

        // then
        assertThat(result).isFalse
    }

    @Test
    fun `если с прошлого платежа _не_ прошло две недели, то _не_ надо платить зарплату новым работникам с комиссионными`() {
        // given
        val employeeId = UUID.randomUUID()
        val wage: Wage = WageCommission(employeeId, 17, 3.0)

        val payCheckRepo = givenThereIsAPaymentForTheDayFor(employeeId, FRIDAY.minusDays(7))

        val strategy: PayDayStrategy = PayDayStrategyForWageCommission(payCheckRepo)

        // when
        val result = strategy.isPayDayFor(wage, FRIDAY)

        // then
        assertThat(result).isFalse
    }


}