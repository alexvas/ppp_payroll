package ppp.payroll.repo.ram

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ppp.payroll.Employee
import ppp.payroll.EmployeeDetail
import ppp.payroll.EmployeeRepo
import ppp.payroll.MonoRepo
import java.time.Instant
import java.util.*

class UserDetailTest {

    private val employeeRepo: EmployeeRepo = EmployeeRepoImpl()

    private val detailRepo: MonoRepo<EmployeeDetail> = MonoRepoBase(employeeRepo)

    @Test
    fun `добавим сведения о работнике`() {
        val employee = Employee()
        employeeRepo.add(employee)

        val detail = EmployeeDetail(
                employee.id,
                name ="Наташа Ростова",
                address = "дом Ростовых",
                recruitment = Instant.now().plusMillis(1300)
        )
        detailRepo.add(detail)
        assertThat(detailRepo.getFeatureFor(employee.id)).isEqualTo(detail)
    }

    @Test
    fun `обновим имя`() {
        val employee = Employee()
        employeeRepo.add(employee)

        val detail = EmployeeDetail(
                employee.id,
                name ="Наташа Ростова",
                address = "дом Ростовых",
                recruitment = Instant.now().plusMillis(1300)
        )
        detailRepo.add(detail)
        detailRepo.update(employee.id) {
            it.copy(name = "Наташа Безухова")
        }

        assertThat(detailRepo.getFeatureFor(employee.id)!!.name).isEqualTo("Наташа Безухова")
    }

    @Test
    fun `обновим адрес`() {
        val employee = Employee()
        employeeRepo.add(employee)

        val detail = EmployeeDetail(
                employee.id,
                name ="Наташа Ростова",
                address = "дом Ростовых",
                recruitment = Instant.now().plusMillis(1300)
        )
        detailRepo.add(detail)
        detailRepo.update(employee.id) {
            it.copy(address = "дом Безуховых")
        }

        assertThat(detailRepo.getFeatureFor(employee.id)!!.address).isEqualTo("дом Безуховых")
    }

    @Test
    fun `обновим дату принятия на работу`() {
        val employee = Employee()
        employeeRepo.add(employee)

        val detail = EmployeeDetail(
                employee.id,
                name ="Наташа Ростова",
                address = "дом Ростовых",
        )
        val warAndPeacePublication = GregorianCalendar.getInstance()
        warAndPeacePublication.set(1865, 1, 1)
        val publication = warAndPeacePublication.time.toInstant()

        detailRepo.add(detail)
        detailRepo.update(employee.id) {
            it.copy(recruitment = publication)
        }

        assertThat(detailRepo.getFeatureFor(employee.id)!!.recruitment).isEqualTo(publication)
    }

    @Test
    fun `работнику нельзя одновременно указать два набора сведений о нём`() {
        val employee = Employee()
        employeeRepo.add(employee)

        val detail1 = EmployeeDetail(
                employee.id,
                name ="Наташа Ростова",
                address = "дом Ростовых",
        )
        detailRepo.add(detail1)

        val detail2 = EmployeeDetail(
                employee.id,
                name ="Пьер Безухов",
                address = "дом Безуховых",
        )

        Assertions.assertThatThrownBy {
            detailRepo.add(detail2)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("twice")
    }

    @Test
    fun `нельзя указать сведения для несуществующего работника`() {
        val detail = EmployeeDetail(
                UUID.randomUUID(),
                name ="Гуинплен",
                address = "большая дорога",
        )

        Assertions.assertThatThrownBy {
            detailRepo.add(detail)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("not found")

    }

    @Test
    fun `нельзя обновить сведения о несуществующем работнике`() {
        Assertions.assertThatThrownBy {
            detailRepo.update(UUID.randomUUID()) {
                it.copy(name = "Аркадий Голиков")
            }
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("not in the repo")
    }

    @Test
    fun `нельзя создать работника без имени`() {
        Assertions.assertThatThrownBy {
            EmployeeDetail(
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
        Assertions.assertThatThrownBy {
            EmployeeDetail(
                    UUID.randomUUID(),
                    "выаывавы",
                    "",
            )
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("address")
    }

}