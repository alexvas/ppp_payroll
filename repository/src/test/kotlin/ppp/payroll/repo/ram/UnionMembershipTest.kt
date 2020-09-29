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
        val employee = Employee()
        employeeRepo.add(employee)

        val membership = UnionMembership(employee.id, 44)
        unionMembershipRepo.add(membership)
        assertThat(unionMembershipRepo.getFeatureFor(employee.id)).isEqualTo(membership)
    }

    @Test
    fun `обновим профсоюзный взнос`() {
        val employee = Employee()
        employeeRepo.add(employee)

        val membership = UnionMembership(employee.id, 45)
        unionMembershipRepo.add(membership)
        unionMembershipRepo.updateDueRate(employee.id, 13)
        assertThat(unionMembershipRepo.getFeatureFor(employee.id)!!.dueRate).isEqualTo(13)
    }

    @Test
    fun `исключаем из профсоюза`() {
        val employee = Employee()
        employeeRepo.add(employee)

        val membership = UnionMembership(employee.id, 315)
        unionMembershipRepo.add(membership)
        unionMembershipRepo.noMember(employee.id)
        assertThat(unionMembershipRepo.getFeatureFor(employee.id)).isNull()
    }

    @Test
    fun `работнику нельзя выплачивать два профсоюзных взноса`() {
        val employee = Employee()
        employeeRepo.add(employee)

        val membership1 = UnionMembership(employee.id, 24)
        unionMembershipRepo.add(membership1)

        val membership2 = UnionMembership(employee.id, 15)

        Assertions.assertThatThrownBy {
            unionMembershipRepo.add(membership2)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("twice")
    }

    @Test
    fun `нельзя зарегистрировать профсоюзный взнос для несуществующего работника`() {
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
                .hasMessageContaining("not in the repo")
    }

}