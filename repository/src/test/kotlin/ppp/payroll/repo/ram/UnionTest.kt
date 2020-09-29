package ppp.payroll.repo.ram

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ppp.payroll.Employee
import ppp.payroll.FlatMonthlySalariedEmployee
import ppp.payroll.UnionCharge
import java.util.*

class UnionTest {

    @Test
    fun `добавим членство в профсоюзе`() {
        val dima: Employee = FlatMonthlySalariedEmployee(
                UUID.randomUUID(),
                "Дмитрий",
                "там-то",
                11
        )
        employeeRepo.add(dima)

        val charge = UnionCharge(dima.id, 44)
        unionChargeRepo.add(charge)
        assertThat(unionChargeRepo.getFeatureFor(dima.id)).isEqualTo(charge)
    }

    @Test
    fun `у работника нельзя вычесть два профсоюзных взноса`() {
        val andrei: Employee = FlatMonthlySalariedEmployee(
                UUID.randomUUID(),
                "Андрей",
                "север",
                101
        )
        employeeRepo.add(andrei)

        val charge1 = UnionCharge(andrei.id, 24)
        unionChargeRepo.add(charge1)

        val charge2 = UnionCharge(andrei.id, 15)

        Assertions.assertThatThrownBy {
            unionChargeRepo.add(charge2)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("twice")
    }

    @Test
    fun `нельзя зарегистрировать вычет профсоюзного взноса для несуществующего работника`() {
        val charge = UnionCharge(UUID.randomUUID(), 3)

        Assertions.assertThatThrownBy {
            unionChargeRepo.add(charge)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("not found")

    }

}