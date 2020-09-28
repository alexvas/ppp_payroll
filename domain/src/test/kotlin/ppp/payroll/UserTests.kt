package ppp.payroll

import org.assertj.core.api.Assertions.assertThat
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


}