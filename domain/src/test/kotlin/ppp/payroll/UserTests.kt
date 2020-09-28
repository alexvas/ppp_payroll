package ppp.payroll

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import ppp.payroll.repo.EmployeeRepo
import ppp.payroll.repo.SalesReceiptRepo
import ppp.payroll.repo.TimeCardRepo
import java.time.Instant
import java.util.*

class UserTests {
    private val petya: Employee = EmployeeFactory.createHourlyRatedEmployee(
            UUID.randomUUID(),
            "Петя",
            "где-то",
            100
    )

    private val vasya: Employee = EmployeeFactory.createFlatMonthlySalariedEmployee(
            UUID.randomUUID(),
            "Вася",
            "там-то",
            562,
    )

    @BeforeAll
    fun setup() {
        EmployeeRepo.add(petya)
        EmployeeRepo.add(vasya)
    }

    @Test
    fun `добавляем работников в репозиторий`() {

        val initialSize = EmployeeRepo.allEmployees().size

        val ulya: Employee = EmployeeFactory.createCommissionedEmployee(
                UUID.randomUUID(),
                "Юля",
                "не здесь",
                362,
                10.0
        )

        EmployeeRepo.add(ulya)

        assertThat(EmployeeRepo.allEmployees()).hasSize(initialSize + 1)
        assertThat(EmployeeRepo.allEmployees()).contains(petya, vasya, ulya)
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

    @Test
    fun `нельзя дважды добавить работника в репозиторий`() {
        val zhenya: Employee = EmployeeFactory.createCommissionedEmployee(
                UUID.randomUUID(),
                "Женя",
                "опять не здесь",
                363,
                12.0
        )

        EmployeeRepo.add(zhenya)
        assertThatThrownBy {
            EmployeeRepo.add(zhenya)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("twice")
    }

    @Test
    fun `удаляем работника из репозитория`() {
        val fedya: Employee = EmployeeFactory.createCommissionedEmployee(
                UUID.randomUUID(),
                "Федя",
                "ччч",
                363,
                12.0
        )

        EmployeeRepo.add(fedya)
        EmployeeRepo.remove(fedya.id)

        assertThat(EmployeeRepo.allEmployees())
                .doesNotContainSequence(fedya)
    }

    @Test
    fun `нельзя удалить работника с учтённым временем`() {
        val ulyana: Employee = EmployeeFactory.createCommissionedEmployee(
                UUID.randomUUID(),
                "Ульяна",
                "ччч",
                63,
                1.0
        )
        EmployeeRepo.add(ulyana)
        val card = TimeCard(ulyana.id, Instant.now(), 2)
        TimeCardRepo.add(card)
        assertThatThrownBy {
            EmployeeRepo.remove(ulyana.id)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("has time card(s)")
    }

    @Test
    fun `нельзя удалить работника с продажами`() {
        val efim: Employee = EmployeeFactory.createCommissionedEmployee(
                UUID.randomUUID(),
                "Ефим",
                "ччч",
                63,
                1.0
        )
        EmployeeRepo.add(efim)
        val receipt = SalesReceipt(efim.id, Instant.now(), 700)
        SalesReceiptRepo.add(receipt)
        assertThatThrownBy {
            EmployeeRepo.remove(efim.id)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("has sales receipt(s)")
    }

}