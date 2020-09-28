package ppp.payroll

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import ppp.payroll.repo.EmployeeRepo
import java.util.*

class UserTests {

    @Test
    fun `добавляем работников в репозиторий`() {
        val petya: Employee = EmployeeFactory.createHourlyRatedEmployee(
                UUID.randomUUID(),
                "Петя",
                "где-то",
                100
        )

        val vasya: Employee = EmployeeFactory.createFlatMonthlySalariedEmployee(
                UUID.randomUUID(),
                "Вася",
                "там-то",
                562,
        )

        EmployeeRepo.add(petya)
        EmployeeRepo.add(vasya)

        val ulya: Employee = EmployeeFactory.createCommissionedEmployee(
                UUID.randomUUID(),
                "Юля",
                "не здесь",
                362,
                10.0
        )

        EmployeeRepo.add(ulya)

        assertThat(EmployeeRepo.list()).hasSize(3)
        assertThat(EmployeeRepo.list()).contains(petya, vasya, ulya)
    }

    @Test
    fun `нельзя создать работника без имени`() {
        assertThatThrownBy {
            EmployeeFactory.createHourlyRatedEmployee(
                    UUID.randomUUID(),
                    "",
                    "где-то",
                    100
            )
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("name")
    }

    @Test
    fun `нельзя создать работника без адреса`() {
        assertThatThrownBy {
            EmployeeFactory.createHourlyRatedEmployee(
                    UUID.randomUUID(),
                    "выаывавы",
                    "",
                    100
            )
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("address")
    }

    @Test
    fun `нельзя создать работника с отрицательной почасовой ставкой`() {
        assertThatThrownBy {
            EmployeeFactory.createHourlyRatedEmployee(
                    UUID.randomUUID(),
                    "выаывавы",
                    "вавава",
                    -100
            )
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("rate")
    }

    @Test
    fun `нельзя создать работника с нулевой почасовой ставкой`() {
        assertThatThrownBy {
            EmployeeFactory.createHourlyRatedEmployee(
                    UUID.randomUUID(),
                    "выаывавы",
                    "вавава",
                    0
            )
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("rate")
    }

    @Test
    fun `нельзя создать работника с отрицательной зарплатой`() {
        assertThatThrownBy {
            EmployeeFactory.createFlatMonthlySalariedEmployee(
                    UUID.randomUUID(),
                    "выаывавы",
                    "вавава",
                    -1
            )
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("salary")
    }

    @Test
    fun `нельзя создать работника с нулевой зарплатой`() {
        assertThatThrownBy {
            EmployeeFactory.createFlatMonthlySalariedEmployee(
                    UUID.randomUUID(),
                    "выаывавы",
                    "вавава",
                    0
            )
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("salary")
    }

    @Test
    fun `нельзя создать работника с нулевой комиссией`() {
        assertThatThrownBy {
            EmployeeFactory.createCommissionedEmployee(
                    UUID.randomUUID(),
                    "выаывавы",
                    "вавава",
                    10,
                    0.0
            )
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("commission")
    }

    @Test
    fun `нельзя создать работника с комиссией, превышающей 100%`() {
        assertThatThrownBy {
            EmployeeFactory.createCommissionedEmployee(
                    UUID.randomUUID(),
                    "выаывавы",
                    "вавава",
                    10,
                    110.0
            )
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("commission")
    }

}