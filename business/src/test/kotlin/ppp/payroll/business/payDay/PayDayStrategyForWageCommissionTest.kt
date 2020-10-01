package ppp.payroll.business.payDay

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ppp.payroll.Wage
import ppp.payroll.WageCommission
import ppp.payroll.business.*
import java.time.LocalDate
import java.util.*

val FRIDAY: LocalDate = LocalDate.of(2020, 3, 27)

class PayDayStrategyForWageCommissionTest {
    // given
    private val employeeId = UUID.randomUUID()
    private val wage: Wage = WageCommission(employeeId, 17, 3.0)

    @Test
    fun `в пятницу надо платить зарплату новым работникам с комиссионными`() {
        // given
        val payCheckRepo = noPayment()
        val strategy: PayDayStrategy = PayDayStrategyForWageCommission(payCheckRepo)

        // when
        val result = strategy.isPayDayFor(wage, FRIDAY)

        // then
        assertThat(result).isTrue
        payCheckRepo.lastPayDayWasCalledWith(employeeId)
    }

    @Test
    fun `а _не_ в пятницу _не_ надо платить зарплату новым работникам с комиссионными`() {
        // given
        val payCheckRepo = noPayment()
        val strategy: PayDayStrategy = PayDayStrategyForWageCommission(payCheckRepo)

        // when
        val result = strategy.isPayDayFor(wage, FRIDAY.minusDays(1))

        // then
        assertThat(result).isFalse
        payCheckRepo.lastPayDayWasCalledWith(employeeId)
    }

    @Test
    fun `если с прошлого платежа _не_ прошло две недели, то _не_ надо платить зарплату новым работникам с комиссионными`() {
        // given
        val payCheckRepo = givenThereWasAPaymentOn(FRIDAY.minusDays(7))
        val strategy: PayDayStrategy = PayDayStrategyForWageCommission(payCheckRepo)

        // when
        val result = strategy.isPayDayFor(wage, FRIDAY)

        // then
        assertThat(result).isFalse
        payCheckRepo.lastPayDayWasCalledWith(employeeId)
    }


}