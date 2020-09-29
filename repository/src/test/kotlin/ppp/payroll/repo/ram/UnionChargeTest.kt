package ppp.payroll.repo.ram

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import ppp.payroll.Employee
import ppp.payroll.EmployeeRepo
import ppp.payroll.MultiRepo
import ppp.payroll.UnionCharge
import java.util.*

class UnionChargeTest {

    private val employeeRepo: EmployeeRepo = EmployeeRepoImpl()

    private val unionChargeRepo: MultiRepo<UnionCharge> = MultiRepoBase(employeeRepo)

    private val roman = Employee(
            UUID.randomUUID(),
            "Рома",
            "в доме",
    )

    @BeforeAll
    fun setup() {
        employeeRepo.add(roman)
        val charge = UnionCharge(roman.id, 32)
        unionChargeRepo.add(charge)
    }

    @Test
    fun `добавим профсоюзный сбор`() {
        val charge = UnionCharge(roman.id, 15)
        unionChargeRepo.add(charge)
        assertThat(unionChargeRepo.allFeatures()).contains(charge)
        assertThat(unionChargeRepo.featuresFor(roman.id)).contains(charge)
    }

    @Test
    fun `конкретный профсоюзный сбор нельзя учесть дважды`() {
        val charge = UnionCharge(roman.id, 11)
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