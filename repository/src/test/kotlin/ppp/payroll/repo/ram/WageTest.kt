package ppp.payroll.repo.ram

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Percentage.withPercentage
import org.junit.jupiter.api.Test
import ppp.payroll.*
import java.util.*

class WageTest {

    private val employeeRepo: EmployeeRepo = EmployeeRepoImpl()

    private val wageRepo: WageRepo = WageRepoImpl(employeeRepo)

    @Test
    fun `создаём работника с почасовой ставкой`() {
        val employee = Employee()
        employeeRepo.add(employee)
        val wage: Wage = WageHourlyRate(
                employee.id,
                100,
        )
        wageRepo.add(wage)
        val savedWage = wageRepo.getFeatureFor(employee.id)
        assertThat(savedWage).isNotNull
        assertThat(savedWage!!.type).isEqualTo(WageType.HOURLY_RATE)
        assertThat((savedWage as WageHourlyRate).hourlyRate).isEqualTo(100)
    }

    @Test
    fun `создаём работника с фиксированной ставкой`() {
        val employee = Employee()
        employeeRepo.add(employee)
        val wage: Wage = WageFlatMonthlySalary(
                employee.id,
                120,
        )
        wageRepo.add(wage)
        val savedWage = wageRepo.getFeatureFor(employee.id)
        assertThat(savedWage).isNotNull
        assertThat(savedWage!!.type).isEqualTo(WageType.FLAT_MONTHLY_SALARY)
        assertThat((savedWage as WageFlatMonthlySalary).monthlySalary).isEqualTo(120)
    }

    @Test
    fun `создаём работника с комисионными`() {
        val employee = Employee()
        employeeRepo.add(employee)
        val wage: Wage = WageCommission(
                employee.id,
                8,
                12.0
        )
        wageRepo.add(wage)
        val savedWage = wageRepo.getFeatureFor(employee.id)
        assertThat(savedWage).isNotNull
        assertThat(savedWage!!.type).isEqualTo(WageType.COMMISSION)
        val commission = savedWage as WageCommission
        assertThat(commission.monthlySalary).isEqualTo(8)
        assertThat(commission.commission).isCloseTo(12.0, withPercentage(0.00001))
    }

    @Test
    fun `нельзя создать работника с отрицательной почасовой ставкой`() {
        Assertions.assertThatThrownBy {
            WageHourlyRate(
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
            WageHourlyRate(
                    UUID.randomUUID(),
                    0
            )
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("rate")
    }

    @Test
    fun `нельзя создать работника с отрицательной фиксированной ставкой`() {
        Assertions.assertThatThrownBy {
            WageFlatMonthlySalary(
                    UUID.randomUUID(),
                    -1
            )
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("salary")
    }

    @Test
    fun `нельзя создать работника с нулевой фиксированной ставкой`() {
        Assertions.assertThatThrownBy {
            WageFlatMonthlySalary(
                    UUID.randomUUID(),
                    0
            )
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("salary")
    }

    @Test
    fun `нельзя создать работника с нулевыми комиссионными`() {
        Assertions.assertThatThrownBy {
            WageCommission(
                    UUID.randomUUID(),
                    10,
                    0.0
            )
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("commission")
    }

    @Test
    fun `нельзя создать работника с комиссионными, превышающими 100%`() {
        Assertions.assertThatThrownBy {
            WageCommission(
                    UUID.randomUUID(),
                    10,
                    110.0
            )
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("commission")
    }

    @Test
    fun `меняем зарплату на фиксированную ставку`() {
        val employee = Employee()
        employeeRepo.add(employee)
        val hourly: Wage = WageHourlyRate(
                employee.id,
                100,
        )
        wageRepo.add(hourly)

        val flat: Wage = WageFlatMonthlySalary(
                employee.id,
                120,
        )
        wageRepo.update(employee.id) {
            flat
        }
        assertThat(wageRepo.getFeatureFor(employee.id)).isEqualTo(flat)
    }

    @Test
    fun `меняем зарплату на комиссионные`() {
        val employee = Employee()
        employeeRepo.add(employee)
        val hourly: Wage = WageHourlyRate(
                employee.id,
                100,
        )
        wageRepo.add(hourly)

        val commission: Wage = WageCommission(
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
    fun `меняем зарплату на почасовую ставку`() {
        val employee = Employee()
        employeeRepo.add(employee)

        val flat: Wage = WageFlatMonthlySalary(
                employee.id,
                120,
        )
        wageRepo.add(flat)
        val hourly: Wage = WageHourlyRate(
                employee.id,
                100,
        )
        wageRepo.update(employee.id) {
            hourly
        }
        assertThat(wageRepo.getFeatureFor(employee.id)).isEqualTo(hourly)
    }

}
