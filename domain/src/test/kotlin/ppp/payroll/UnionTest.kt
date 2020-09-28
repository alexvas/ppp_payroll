package ppp.payroll

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import ppp.payroll.repo.EmployeeRepo
import ppp.payroll.repo.UnionRepo
import java.util.*

class UnionTest {

    @Test
    fun `добавим членство в профсоюзе`() {
        val dima: Employee = EmployeeFactory.createFlatMonthlySalariedEmployee(
                UUID.randomUUID(),
                "Дмитрий",
                "там-то",
                11
        )
        EmployeeRepo.add(dima)

        val charge = UnionCharge(dima.id, 44)
        UnionRepo.add(charge)
        Assertions.assertThat(UnionRepo.allItems()).contains(charge)
    }

    @Test
    fun `у работника нельзя вычесть два профсоюзных взноса`() {
        val andrei: Employee = EmployeeFactory.createFlatMonthlySalariedEmployee(
                UUID.randomUUID(),
                "Андрей",
                "север",
                101
        )
        EmployeeRepo.add(andrei)

        val charge1 = UnionCharge(andrei.id, 24)
        UnionRepo.add(charge1)

        val charge2 = UnionCharge(andrei.id, 15)

        Assertions.assertThatThrownBy {
            UnionRepo.add(charge2)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("twice")
    }

    @Test
    fun `нельзя зарегистрировать вычет профсоюзного взноса для несуществующего работника`() {
        val charge = UnionCharge(UUID.randomUUID(), 3)

        Assertions.assertThatThrownBy {
            UnionRepo.add(charge)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("not found")

    }

}