package ppp.payroll.business.payDay

import io.mockk.clearMocks
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import ppp.payroll.Wage
import ppp.payroll.WageFlatMonthlySalary
import ppp.payroll.business.PayDayStrategy
import ppp.payroll.business.PayDayStrategyForWageFlatMonthlySalary
import ppp.payroll.business.lastPayDayWasCalledWith
import ppp.payroll.business.noPayment
import java.time.LocalDate
import java.util.*

val LAST_WORKING_DAY_OF_WINTER: LocalDate = LocalDate.of(2020, 2, 28)

class PayDayStrategyForWageFlatMonthlySalaryTest {
    // given
    private val employeeId = UUID.randomUUID()
    private val noPayment = noPayment()
    private val strategy: PayDayStrategy = PayDayStrategyForWageFlatMonthlySalary(noPayment)
    private val wage: Wage = WageFlatMonthlySalary(employeeId, 17)

    @AfterEach
    fun clearCalls() {
        clearMocks(noPayment, answers = false)
    }

    @Test
    fun `в последний рабочий день месяца надо платить зарплату новым работникам на фиксированной ставке`() {
        // when
        val result = strategy.isPayDayFor(wage, LAST_WORKING_DAY_OF_WINTER)

        // then
        assertThat(result).isTrue
        noPayment.lastPayDayWasCalledWith(employeeId)
    }

    @Test
    fun `_не_ в последний рабочий день месяца зарплату новым работникам на фиксированной ставке платить не надо`() {
        // when
        val result = strategy.isPayDayFor(wage, LAST_WORKING_DAY_OF_WINTER.minusDays(5))

        // then
        assertThat(result).isFalse
        noPayment.lastPayDayWasCalledWith(employeeId)
    }


}