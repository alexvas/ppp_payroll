package ppp.payroll.business.payDay

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ppp.payroll.Wage
import ppp.payroll.WageHourlyRate
import ppp.payroll.business.*
import java.util.*

class PayDayStrategyForWageHourlyRateTest {
    // given
    private val employeeId = UUID.randomUUID()
    private val wage: Wage = WageHourlyRate(employeeId, 17)

    @Test
    fun `проверяем, что по пятницам надо платить зарплату новым работникам на почасовой ставке`() {
        // given
        val payCheckRepo = noPayment()
        val strategy: PayDayStrategy = PayDayStrategyForWageHourlyRate(payCheckRepo)

        // when
        val result = strategy.isPayDayFor(wage, FRIDAY)

        // then
        assertThat(result).isTrue
        payCheckRepo.lastPayDayWasCalledWith(employeeId)
    }

    @Test
    fun `проверяем, что по субботам платить зарплату новым работникам на почасовой ставке _не_ надо`() {
        // given
        val payCheckRepo = noPayment()
        val strategy: PayDayStrategy = PayDayStrategyForWageHourlyRate(payCheckRepo)

        // when
        val result = strategy.isPayDayFor(wage, FRIDAY.plusDays(1))

        // then
        assertThat(result).isFalse
        payCheckRepo.lastPayDayWasCalledWith(employeeId)
    }

    @Test
    fun `если выплата в конкретный день уже была, то по новой платить в тот же день уже не надо`() {
        // given
        val payCheckRepo = givenThereWasAPaymentOn(FRIDAY)
        val strategy: PayDayStrategy = PayDayStrategyForWageHourlyRate(payCheckRepo)

        // when
        val result = strategy.isPayDayFor(wage, FRIDAY)

        // then
        assertThat(result).isFalse
        payCheckRepo.lastPayDayWasCalledWith(employeeId)
    }

    @Test
    fun `если выплата была хоть день назад, то придётся платить по новой`() {
        // given
        val payCheckRepo = givenThereWasAPaymentOn(FRIDAY.minusDays(1))
        val strategy: PayDayStrategy = PayDayStrategyForWageHourlyRate(payCheckRepo)

        // when
        val result = strategy.isPayDayFor(wage, FRIDAY)

        // then
        assertThat(result).isTrue
        payCheckRepo.lastPayDayWasCalledWith(employeeId)
    }

}
