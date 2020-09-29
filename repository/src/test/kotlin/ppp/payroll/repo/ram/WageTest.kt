package ppp.payroll.repo.ram

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Percentage.withPercentage
import org.junit.jupiter.api.Test
import ppp.payroll.*
import java.util.*

class WageTest {

    private val employeeRepo: EmployeeRepo = EmployeeRepoImpl()

    private val wageRepo: MonoRepo<Wage> = MonoRepoBase(employeeRepo)

    @Test
    fun `создаём работника с почасовой ставкой`() {
        val employee = Employee()
        employeeRepo.add(employee)
        val wage: Wage = HourlyRate(
                employee.id,
                100,
        )
        wageRepo.add(wage)
        val savedWage = wageRepo.getFeatureFor(employee.id)
        assertThat(savedWage).isNotNull
        assertThat(savedWage!!.type).isEqualTo(WageType.HOURLY_RATE)
        assertThat((savedWage as HourlyRate).hourlyRate).isEqualTo(100)
    }

    @Test
    fun `создаём работника с постоянной зарплатой`() {
        val employee = Employee()
        employeeRepo.add(employee)
        val wage: Wage = FlatMonthlySalary(
                employee.id,
                120,
        )
        wageRepo.add(wage)
        val savedWage = wageRepo.getFeatureFor(employee.id)
        assertThat(savedWage).isNotNull
        assertThat(savedWage!!.type).isEqualTo(WageType.FLAT_MONTHLY_SALARY)
        assertThat((savedWage as FlatMonthlySalary).monthlySalary).isEqualTo(120)
    }

    @Test
    fun `создаём работника с процентной выплатой`() {
        val employee = Employee()
        employeeRepo.add(employee)
        val wage: Wage = Commission(
                employee.id,
                8,
                12.0
        )
        wageRepo.add(wage)
        val savedWage = wageRepo.getFeatureFor(employee.id)
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

    @Test
    fun `меняем зарплату на постоянную`() {
        val employee = Employee()
        employeeRepo.add(employee)
        val hourly: Wage = HourlyRate(
                employee.id,
                100,
        )
        wageRepo.add(hourly)

        val flat: Wage = FlatMonthlySalary(
                employee.id,
                120,
        )
        wageRepo.update(employee.id) {
            flat
        }
        assertThat(wageRepo.getFeatureFor(employee.id)).isEqualTo(flat)
    }

    @Test
    fun `меняем зарплату на процентную выплату`() {
        val employee = Employee()
        employeeRepo.add(employee)
        val hourly: Wage = HourlyRate(
                employee.id,
                100,
        )
        wageRepo.add(hourly)

        val commission: Wage = Commission(
                employee.id,
                8,
                12.0
        )
        wageRepo.update(employee.id) {
            commission
        }
        assertThat(wageRepo.getFeatureFor(employee.id)).isEqualTo(commission)
    }

    @Test
    fun `меняем зарплату на почасовую`() {
        val employee = Employee()
        employeeRepo.add(employee)

        val flat: Wage = FlatMonthlySalary(
                employee.id,
                120,
        )
        wageRepo.add(flat)
        val hourly: Wage = HourlyRate(
                employee.id,
                100,
        )
        wageRepo.update(employee.id) {
            hourly
        }
        assertThat(wageRepo.getFeatureFor(employee.id)).isEqualTo(hourly)
    }

}
