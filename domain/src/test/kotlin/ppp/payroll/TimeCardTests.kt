package ppp.payroll

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import ppp.payroll.repo.employeeRepo
import ppp.payroll.repo.timeCardRepo
import java.time.Instant
import java.util.*

class TimeCardTests {
    private val zahar: Employee = EmployeeFactory.createHourlyRatedEmployee(
            UUID.randomUUID(),
            "Захар",
            "где-то",
            100
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