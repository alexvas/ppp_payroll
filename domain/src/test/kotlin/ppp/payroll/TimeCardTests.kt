package ppp.payroll

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import ppp.payroll.repo.EmployeeRepo
import ppp.payroll.repo.TimeCardRepo
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
        EmployeeRepo.add(zahar)
        val card = TimeCard(zahar.id, Instant.now(), 12)
        TimeCardRepo.add(card)
    }

    @Test
    fun `добавим отработанное время`() {
        val card = TimeCard(zahar.id, Instant.now().plusMillis(1000), 12)
        TimeCardRepo.add(card)
        assertThat(TimeCardRepo.allCards()).contains(card)
    }

    @Test
    fun `отработанное время нельзя учесть дважды`() {
        val card = TimeCard(zahar.id, Instant.now().plusMillis(2000), 12)
        TimeCardRepo.add(card)
        Assertions.assertThatThrownBy {
            TimeCardRepo.add(card)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("twice")
    }

    @Test
    fun `нельзя учесть отработанное время для несуществующего работника`() {
        val card = TimeCard(UUID.randomUUID(), Instant.now(), 1)
        Assertions.assertThatThrownBy {
            TimeCardRepo.add(card)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("not found")

    }


}