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

        val membership = UnionMembership(dima.id, 44)
        unionMembershipRepo.add(membership)
        assertThat(unionMembershipRepo.getFeatureFor(dima.id)).isEqualTo(membership)
    }

    @Test
    fun `обновим профсоюзный взнос`() {
        val stas = Employee(
                UUID.randomUUID(),
                "Стас",
                "везде",
        )
        employeeRepo.add(stas)

        val membership = UnionMembership(stas.id, 45)
        unionMembershipRepo.add(membership)
        unionMembershipRepo.updateDueRate(stas.id, 13)
        assertThat(unionMembershipRepo.getFeatureFor(stas.id)!!.dueRate).isEqualTo(13)
    }

    @Test
    fun `исключаем из профсоюза`() {
        val anfisa = Employee(
                UUID.randomUUID(),
                "Анфиса",
                "там-то",
        )
        employeeRepo.add(anfisa)

        val membership = UnionMembership(anfisa.id, 315)
        unionMembershipRepo.add(membership)
        unionMembershipRepo.noMember(anfisa.id)
        assertThat(unionMembershipRepo.getFeatureFor(anfisa.id)).isNull()
    }

    @Test
    fun `у работника нельзя вычесть два профсоюзных взноса`() {
        val andrei = Employee(
                UUID.randomUUID(),
                "Андрей",
                "север",
        )
        employeeRepo.add(andrei)

        val membership1 = UnionMembership(andrei.id, 24)
        unionMembershipRepo.add(membership1)

        val membership2 = UnionMembership(andrei.id, 15)

        Assertions.assertThatThrownBy {
            unionMembershipRepo.add(membership2)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("twice")
    }

    @Test
    fun `нельзя зарегистрировать вычет профсоюзного взноса для несуществующего работника`() {
        val membership = UnionMembership(UUID.randomUUID(), 3)

        Assertions.assertThatThrownBy {
            unionMembershipRepo.add(membership)
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