package ppp.payroll.repo.ram

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ppp.payroll.Employee
import ppp.payroll.EmployeeRepo
import ppp.payroll.UnionMembership
import ppp.payroll.UnionMembershipRepo
import java.util.*

class UnionMembershipTest {

    private val employeeRepo: EmployeeRepo = EmployeeRepoImpl()

    private val unionMembershipRepo: UnionMembershipRepo = UnionMembershipRepoImpl(employeeRepo)

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
    fun `обновим профсоюзный взнос`() {
        val stas = Employee(
                UUID.randomUUID(),
                "Стас",
                "везде",
        )
        employeeRepo.add(stas)

        val charge = UnionMembership(stas.id, 45)
        unionMembershipRepo.add(charge)
        unionMembershipRepo.updateDueRate(stas.id, 13)
        assertThat(unionMembershipRepo.getFeatureFor(stas.id)!!.dueRate).isEqualTo(13)
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

    @Test
    fun `нельзя обновить профсоюзный взнос для несуществующего работника`() {
        Assertions.assertThatThrownBy {
            unionMembershipRepo.updateDueRate(UUID.randomUUID(), 8)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("no member")
    }

}