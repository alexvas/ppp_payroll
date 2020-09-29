package ppp.payroll.repo.ram

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ppp.payroll.Employee
import ppp.payroll.EmployeeRepo
import ppp.payroll.MonoRepo
import ppp.payroll.UnionMembership
import java.util.*

class UnionMembershipTest {

    private val employeeRepo: EmployeeRepo = EmployeeRepoImpl()

    private val unionMembershipRepo: MonoRepo<UnionMembership> = MonoRepoBase(employeeRepo)

    @Test
    fun `добавим членство в профсоюзе`() {
        val dima = Employee(
                UUID.randomUUID(),
                "Дмитрий",
                "там-то",
        )
        employeeRepo.add(dima)

        val charge = UnionMembership(dima.id, 44)
        unionMembershipRepo.add(charge)
        assertThat(unionMembershipRepo.getFeatureFor(dima.id)).isEqualTo(charge)
    }

    @Test
    fun `у работника нельзя вычесть два профсоюзных взноса`() {
        val andrei = Employee(
                UUID.randomUUID(),
                "Андрей",
                "север",
        )
        employeeRepo.add(andrei)

        val charge1 = UnionMembership(andrei.id, 24)
        unionMembershipRepo.add(charge1)

        val charge2 = UnionMembership(andrei.id, 15)

        Assertions.assertThatThrownBy {
            unionMembershipRepo.add(charge2)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("twice")
    }

    @Test
    fun `нельзя зарегистрировать вычет профсоюзного взноса для несуществующего работника`() {
        val charge = UnionMembership(UUID.randomUUID(), 3)

        Assertions.assertThatThrownBy {
            unionMembershipRepo.add(charge)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("not found")

    }

}