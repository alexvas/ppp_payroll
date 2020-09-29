package ppp.payroll.repo.ram

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import ppp.payroll.*
import java.time.Instant
import java.util.*

class EmployeeTests {
    private val petya = Employee(
            UUID.randomUUID(),
            "Петя",
            "где-то",
    )

    private val vasya = Employee(
            UUID.randomUUID(),
            "Вася",
            "там-то",
    )

    @BeforeAll
    fun setup() {
        employeeRepo.add(petya)
        employeeRepo.add(vasya)
    }

    @Test
    fun `добавляем работников в репозиторий`() {

        val initialSize = employeeRepo.allEmployees().size

        val ulya = Employee(
                UUID.randomUUID(),
                "Юля",
                "не здесь",
        )

        employeeRepo.add(ulya)

        assertThat(employeeRepo.allEmployees()).hasSize(initialSize + 1)
        assertThat(employeeRepo.allEmployees()).contains(petya, vasya, ulya)
    }

    @Test
    fun `нельзя создать работника без имени`() {
        assertThatThrownBy {
            Employee(
                    UUID.randomUUID(),
                    "",
                    "где-то",
            )
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("name")
    }

    @Test
    fun `нельзя создать работника без адреса`() {
        assertThatThrownBy {
            Employee(
                    UUID.randomUUID(),
                    "выаывавы",
                    "",
            )
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("address")
    }

    @Test
    fun `нельзя дважды добавить работника в репозиторий`() {
        val zhenya = Employee(
                UUID.randomUUID(),
                "Женя",
                "опять не здесь",
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
        val fedya = Employee(
                UUID.randomUUID(),
                "Федя",
                "ччч",
        )

        employeeRepo.add(fedya)
        employeeRepo.remove(fedya.id)

        assertThat(employeeRepo.allEmployees())
                .doesNotContainSequence(fedya)
    }

    @Test
    fun `можно удалить работника с учтённым временем (удалится отовсюду)`() {
        val ulyana = Employee(
                UUID.randomUUID(),
                "Ульяна",
                "ччч",
        )
        employeeRepo.add(ulyana)
        val card = TimeCard(ulyana.id, Instant.now(), 2)
        timeCardRepo.add(card)
        employeeRepo.remove(ulyana.id)
        assertThat(timeCardRepo.featuresFor(ulyana.id).isEmpty())
    }

    @Test
    fun `можно удалить работника с продажами (удалится отовсюду)`() {
        val efim = Employee(
                UUID.randomUUID(),
                "Ефим",
                "ччч",
        )
        employeeRepo.add(efim)
        val receipt = SalesReceipt(efim.id, Instant.now(), 700)
        salesReceiptRepo.add(receipt)
        employeeRepo.remove(efim.id)
        assertThat(salesReceiptRepo.featuresFor(efim.id).isEmpty())
    }

    @Test
    fun `можно удалить работника с профсоюзным взносом (удалится отовсюду)`() {
        val igor = Employee(
                UUID.randomUUID(),
                "Игорь",
                "ччч",
        )
        employeeRepo.add(igor)
        val charge = UnionCharge(igor.id, 1700)
        unionChargeRepo.add(charge)
        employeeRepo.remove(igor.id)
        assertThat(unionChargeRepo.getFeatureFor(igor.id) == null)
    }

    @Test
    fun `можно удалить работника с выплатами (удалится отовсюду)`() {
        val slavik = Employee(
                UUID.randomUUID(),
                "Вячеслав",
                "бгг",
        )
        employeeRepo.add(slavik)
        val payMethod = PayMethodHold(slavik.id)
        payMethodRepo.add(payMethod)
        employeeRepo.remove(slavik.id)
        assertThat(payMethodRepo.getFeatureFor(slavik.id) == null)
    }

    @Test
    fun `можно удалить работника с зарплатой (удалится отовсюду)`() {
        val denis = Employee(
                UUID.randomUUID(),
                "Денис",
                "офф",
        )
        employeeRepo.add(denis)
        val wage = FlatMonthlySalary(denis.id, 15)
        wageRepo.add(wage)
        employeeRepo.remove(denis.id)
        assertThat(wageRepo.getFeatureFor(denis.id) == null)
    }

    @Test
    fun `успешно создаём работника`() {
        val aleftina = Employee(
                UUID.randomUUID(),
                "Алефтина",
                "налево-направо",

        )
        employeeRepo.add(aleftina)
        assertThat(employeeRepo.hasEmployee(aleftina.id)).isTrue
        val saved = employeeRepo.get(aleftina.id)
        assertThat(saved.name).isEqualTo("Алефтина")
        assertThat(saved.address).isEqualTo("налево-направо")

    }

}