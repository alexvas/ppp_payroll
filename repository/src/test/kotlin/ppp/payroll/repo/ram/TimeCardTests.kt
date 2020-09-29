package ppp.payroll.repo.ram

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import ppp.payroll.Employee
import ppp.payroll.EmployeeRepo
import ppp.payroll.MultiRepo
import ppp.payroll.TimeCard
import java.time.Instant
import java.util.*

class TimeCardTests {

    private val employeeRepo: EmployeeRepo = EmployeeRepoImpl()

    private val timeCardRepo: MultiRepo<TimeCard> = MultiRepoBase(employeeRepo)

    private val zahar = Employee(
            UUID.randomUUID(),
            "Захар",
            "где-то"
    )

    @BeforeAll
    fun setup() {
        employeeRepo.add(zahar)
        val card = TimeCard(zahar.id, Instant.now(), 12)
        timeCardRepo.add(card)
    }

    @Test
    fun `добавим отработанное время`() {
        val card = TimeCard(zahar.id, Instant.now().plusMillis(1000), 12)
        timeCardRepo.add(card)
        assertThat(timeCardRepo.allFeatures()).contains(card)
        assertThat(timeCardRepo.featuresFor(zahar.id)).contains(card)
    }

    @Test
    fun `отработанное время нельзя учесть дважды`() {
        val card = TimeCard(zahar.id, Instant.now().plusMillis(2000), 12)
        timeCardRepo.add(card)
        Assertions.assertThatThrownBy {
            timeCardRepo.add(card)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("twice")
    }

    @Test
    fun `нельзя учесть отработанное время для несуществующего работника`() {
        val card = TimeCard(UUID.randomUUID(), Instant.now(), 1)
        Assertions.assertThatThrownBy {
            timeCardRepo.add(card)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("not found")

    }


}