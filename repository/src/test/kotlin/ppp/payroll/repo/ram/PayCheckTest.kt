package ppp.payroll.repo.ram

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import ppp.payroll.Employee
import ppp.payroll.EmployeeRepo
import ppp.payroll.MultiRepo
import ppp.payroll.PayCheck
import java.time.Instant
import java.util.*

class PayCheckTest {

    private val employeeRepo: EmployeeRepo = EmployeeRepoImpl()

    private val payCheckRepo: MultiRepo<PayCheck> = MultiRepoBase(employeeRepo)

    private val employee = Employee()

    @BeforeAll
    fun setup() {
        employeeRepo.add(employee)
        val receipt = PayCheck(employee.id, Instant.now(), 230, "выплачено в банк J.P.Morgan")
        payCheckRepo.add(receipt)
    }

    @Test
    fun `добавим выплату зарплаты`() {
        val receipt = PayCheck(employee.id, Instant.now(), 100, "хранится у бухгалтера")
        payCheckRepo.add(receipt)
        assertThat(payCheckRepo.allFeatures()).contains(receipt)
        assertThat(payCheckRepo.featuresFor(employee.id)).contains(receipt)
    }

    @Test
    fun `конкретную выплату зарплаты нельзя учесть дважды`() {
        val receipt = PayCheck(employee.id, Instant.now().plusMillis(150), 12, "отослано на домашний адрес")
        payCheckRepo.add(receipt)
        Assertions.assertThatThrownBy {
            payCheckRepo.add(receipt)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("twice")
    }

    @Test
    fun `нельзя учесть выплату зарплаты для несуществующего работника`() {
        val receipt = PayCheck(UUID.randomUUID(), Instant.now(), 1, "отдано в пользу бедных")
        Assertions.assertThatThrownBy {
            payCheckRepo.add(receipt)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("not found")

    }


}