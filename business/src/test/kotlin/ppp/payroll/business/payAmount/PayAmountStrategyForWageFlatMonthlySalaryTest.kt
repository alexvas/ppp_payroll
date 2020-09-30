package ppp.payroll.business.payAmount

import io.mockk.confirmVerified
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import ppp.payroll.Wage
import ppp.payroll.WageFlatMonthlySalary
import ppp.payroll.business.PayAmountStrategy
import ppp.payroll.business.PayAmountStrategyForWageFlatMonthlySalary
import ppp.payroll.business.payDay.LAST_WORKING_DAY_OF_WINTER
import ppp.payroll.business.payDay.emptyPayCheckRepo
import java.util.*

class PayAmountStrategyForWageFlatMonthlySalaryTest {

    @Test
    fun `первая зарплата исчисляется со дня принятия на работу`() {
        // given
        val employeeId = UUID.randomUUID()
        val payCheckRepo = emptyPayCheckRepo()

        val strategy: PayAmountStrategy = PayAmountStrategyForWageFlatMonthlySalary(payCheckRepo)
        val wage: Wage = WageFlatMonthlySalary(employeeId, 20000, LAST_WORKING_DAY_OF_WINTER.minusDays(3))

        // when
        val result = strategy.amount(wage, LAST_WORKING_DAY_OF_WINTER)

        // then
        assertThat(result).isEqualTo(3000)
        verify {
            payCheckRepo.featuresFor(employeeId)
        }
        confirmVerified(
                payCheckRepo,
        )
    }

    @Test
    fun `получим ошибку, если день принятия на работу находится в будущем`() {
        // given
        val employeeId = UUID.randomUUID()
        val payCheckRepo = emptyPayCheckRepo()

        val strategy: PayAmountStrategy = PayAmountStrategyForWageFlatMonthlySalary(payCheckRepo)
        val wage: Wage = WageFlatMonthlySalary(employeeId, 1)

        // when
        assertThatThrownBy {
            strategy.amount(wage, LAST_WORKING_DAY_OF_WINTER)
        }
                // then
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("future")
        verify {
            payCheckRepo.featuresFor(employeeId)
        }
        confirmVerified(
                payCheckRepo,
        )
    }
}
