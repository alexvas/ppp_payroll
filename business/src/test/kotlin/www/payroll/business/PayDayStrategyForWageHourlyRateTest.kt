package www.payroll.business

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ppp.payroll.PayCheck
import ppp.payroll.PayCheckRepo
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
        val strategy: PayDayStrategy = PayDayStrategyForWageHourlyRate(emptyPayCheckRepo())
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
        val strategy: PayDayStrategy = PayDayStrategyForWageHourlyRate(emptyPayCheckRepo())
        val wage: Wage = WageHourlyRate(UUID.randomUUID(), 17)
        val day = LocalDate.now().with(DayOfWeek.SATURDAY)

        // when
        val result = strategy.isPayDayFor(wage, day)

        // then
        assertThat(result).isFalse
    }

    @Test
    fun `если выплата в конкретный день уже была, то по новой платить в тот же день уже не надо`() {
        // given
        val employeeId = UUID.randomUUID()
        val wage: Wage = WageHourlyRate(employeeId, 17)
        val day = LocalDate.now().with(DayOfWeek.FRIDAY)

        val payCheckRepo = givenThereIsAPaymentForTheDayFor(employeeId, day)

        val strategy: PayDayStrategy = PayDayStrategyForWageHourlyRate(payCheckRepo)

        // when
        val result = strategy.isPayDayFor(wage, day)

        // then
        assertThat(result).isFalse
    }

    @Test
    fun `если выплата была хоть день назад, то придётся платить по новой`() {
        // given
        val employeeId = UUID.randomUUID()
        val wage: Wage = WageHourlyRate(employeeId, 17)
        val day = LocalDate.now().with(DayOfWeek.FRIDAY)

        val payCheckRepo = givenThereIsAPaymentForTheDayFor(employeeId, day.minusDays(1))

        val strategy: PayDayStrategy = PayDayStrategyForWageHourlyRate(payCheckRepo)

        // when
        val result = strategy.isPayDayFor(wage, day)

        // then
        assertThat(result).isTrue
    }

}

fun emptyPayCheckRepo(): PayCheckRepo {
    val payCheckRepo: PayCheckRepo = mockk()
    every {
        payCheckRepo.featuresFor(any())
    } returns listOf()
    return payCheckRepo
}

fun givenThereIsAPaymentForTheDayFor(employeeId: UUID, day: LocalDate): PayCheckRepo {
    val payCheck = PayCheck(employeeId, day, 1349873, "уже уплочено")
    val payCheckRepo: PayCheckRepo = mockk()
    every {
        payCheckRepo.featuresFor(any())
    } returns listOf(payCheck)
    return payCheckRepo
}
