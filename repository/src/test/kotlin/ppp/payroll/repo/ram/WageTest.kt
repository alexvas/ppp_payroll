package ppp.payroll.repo.ram

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Percentage.withPercentage
import org.junit.jupiter.api.Test
import ppp.payroll.*
import java.util.*

class WageTest {

    @Test
    fun `создаём работника с почасовой ставкой`() {
        val artur = Employee(
                UUID.randomUUID(),
                "Артур",
                "где-то",
        )
        employeeRepo.add(artur)
        val wage: Wage = HourlyRate(
                artur.id,
                100,
        )
        wageRepo.add(wage)
        val savedWage = wageRepo.getFeatureFor(artur.id)
        assertThat(savedWage).isNotNull
        assertThat(savedWage!!.type).isEqualTo(WageType.HOURLY_RATE)
        assertThat((savedWage as HourlyRate).hourlyRate).isEqualTo(100)
    }

    @Test
    fun `создаём работника с постоянной зарплатой`() {
        val onufriy = Employee(
                UUID.randomUUID(),
                "Онуфрий",
                "где-то",
        )
        employeeRepo.add(onufriy)
        val wage: Wage = FlatMonthlySalary(
                onufriy.id,
                120,
        )
        wageRepo.add(wage)
        val savedWage = wageRepo.getFeatureFor(onufriy.id)
        assertThat(savedWage).isNotNull
        assertThat(savedWage!!.type).isEqualTo(WageType.FLAT_MONTHLY_SALARY)
        assertThat((savedWage as FlatMonthlySalary).monthlySalary).isEqualTo(120)
    }

    @Test
    fun `создаём работника с процентной выплатой`() {
        val epifan = Employee(
                UUID.randomUUID(),
                "Епифан",
                "где-то",
        )
        employeeRepo.add(epifan)
        val wage: Wage = Commission(
                epifan.id,
                8,
                12.0
        )
        wageRepo.add(wage)
        val savedWage = wageRepo.getFeatureFor(epifan.id)
        assertThat(savedWage).isNotNull
        assertThat(savedWage!!.type).isEqualTo(WageType.COMMISSION)
        val commission = savedWage as Commission
        assertThat(commission.monthlySalary).isEqualTo(8)
        assertThat(commission.commission).isCloseTo(12.0, withPercentage(0.00001))
    }

    @Test
    fun `нельзя создать работника с отрицательной почасовой ставкой`() {
        Assertions.assertThatThrownBy {
            HourlyRate(
                    UUID.randomUUID(),
                    -100
            )
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("rate")
    }

    @Test
    fun `нельзя создать работника с нулевой почасовой ставкой`() {
        Assertions.assertThatThrownBy {
            HourlyRate(
                    UUID.randomUUID(),
                    0
            )
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("rate")
    }

    @Test
    fun `нельзя создать работника с отрицательной зарплатой`() {
        Assertions.assertThatThrownBy {
            FlatMonthlySalary(
                    UUID.randomUUID(),
                    -1
            )
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("salary")
    }

    @Test
    fun `нельзя создать работника с нулевой зарплатой`() {
        Assertions.assertThatThrownBy {
            FlatMonthlySalary(
                    UUID.randomUUID(),
                    0
            )
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("salary")
    }

    @Test
    fun `нельзя создать работника с нулевой комиссией`() {
        Assertions.assertThatThrownBy {
            Commission(
                    UUID.randomUUID(),
                    10,
                    0.0
            )
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("commission")
    }

    @Test
    fun `нельзя создать работника с комиссией, превышающей 100%`() {
        Assertions.assertThatThrownBy {
            Commission(
                    UUID.randomUUID(),
                    10,
                    110.0
            )
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("commission")
    }

}
