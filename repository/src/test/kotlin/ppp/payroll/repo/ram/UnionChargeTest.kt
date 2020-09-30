package ppp.payroll.repo.ram

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import ppp.payroll.Employee
import ppp.payroll.EmployeeRepo
import ppp.payroll.UnionCharge
import ppp.payroll.UnionChargeRepo
import java.util.*

class UnionChargeTest {

    private val employeeRepo: EmployeeRepo = EmployeeRepoImpl()

    private val unionChargeRepo: UnionChargeRepo = UnionChargeRepoImpl(employeeRepo)

    private val employee = Employee()

    @BeforeAll
    fun setup() {
        employeeRepo.add(employee)
        val charge = UnionCharge(employee.id, 32)
        unionChargeRepo.add(charge)
    }

    @Test
    fun `добавим профсоюзный сбор`() {
        val charge = UnionCharge(employee.id, 15)
        unionChargeRepo.add(charge)
        assertThat(unionChargeRepo.allFeatures()).contains(charge)
        assertThat(unionChargeRepo.featuresFor(employee.id)).contains(charge)
    }

    @Test
    fun `конкретный профсоюзный сбор нельзя учесть дважды`() {
        val charge = UnionCharge(employee.id, 11)
        unionChargeRepo.add(charge)
        Assertions.assertThatThrownBy {
            unionChargeRepo.add(charge)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("twice")
    }

    @Test
    fun `нельзя учесть профсоюзный сбор для несуществующего работника`() {
        val charge = UnionCharge(UUID.randomUUID(), 3)
        Assertions.assertThatThrownBy {
            unionChargeRepo.add(charge)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("not found")

    }


}