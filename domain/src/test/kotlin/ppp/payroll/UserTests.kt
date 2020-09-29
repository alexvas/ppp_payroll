package ppp.payroll

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import ppp.payroll.repo.*
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
        employeeRepo.add(petya)
        employeeRepo.add(vasya)
    }

    @Test
    fun `добавляем работников в репозиторий`() {

        val initialSize = employeeRepo.allEmployees().size

        val ulya: Employee = EmployeeFactory.createCommissionedEmployee(
                UUID.randomUUID(),
                "Юля",
                "не здесь",
                362,
                10.0
        )

        employeeRepo.add(ulya)

        assertThat(employeeRepo.allEmployees()).hasSize(initialSize + 1)
        assertThat(employeeRepo.allEmployees()).contains(petya, vasya, ulya)
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

        employeeRepo.add(zhenya)
        assertThatThrownBy {
            employeeRepo.add(zhenya)
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

        employeeRepo.add(fedya)
        employeeRepo.remove(fedya.id)

        assertThat(employeeRepo.allEmployees())
                .doesNotContainSequence(fedya)
    }

    @Test
    fun `можно удалить работника с учтённым временем`() {
        val ulyana: Employee = EmployeeFactory.createCommissionedEmployee(
                UUID.randomUUID(),
                "Ульяна",
                "ччч",
                63,
                1.0
        )
        employeeRepo.add(ulyana)
        val card = TimeCard(ulyana.id, Instant.now(), 2)
        timeCardRepo.add(card)
        employeeRepo.remove(ulyana.id)
        assertThat(timeCardRepo.featuresFor(ulyana.id).isEmpty())
    }

    @Test
    fun `можно удалить работника с продажами`() {
        val efim: Employee = EmployeeFactory.createCommissionedEmployee(
                UUID.randomUUID(),
                "Ефим",
                "ччч",
                63,
                1.0
        )
        employeeRepo.add(efim)
        val receipt = SalesReceipt(efim.id, Instant.now(), 700)
        salesReceiptRepo.add(receipt)
        employeeRepo.remove(efim.id)
        assertThat(salesReceiptRepo.featuresFor(efim.id).isEmpty())
    }

    @Test
    fun `можно удалить работника с профсоюзным взносом`() {
        val igor: Employee = EmployeeFactory.createCommissionedEmployee(
                UUID.randomUUID(),
                "Игорь",
                "ччч",
                63,
                1.0
        )
        employeeRepo.add(igor)
        val charge = UnionCharge(igor.id, 1700)
        unionChargeRepo.add(charge)
        employeeRepo.remove(igor.id)
        assertThat(unionChargeRepo.getFeatureFor(igor.id) == null)
    }

    @Test
    fun `можно удалить работника с выплатами`() {
        val slavik: Employee = EmployeeFactory.createCommissionedEmployee(
                UUID.randomUUID(),
                "Вячеслав",
                "бгг",
                13,
                12.0
        )
        employeeRepo.add(slavik)
        val payMethod = PayMethodHold(slavik.id)
        payMethodRepo.add(payMethod)
        employeeRepo.remove(slavik.id)
        assertThat(payMethodRepo.getFeatureFor(slavik.id) == null)
    }

}