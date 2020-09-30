package ppp.payroll.repo.ram

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import ppp.payroll.Employee
import ppp.payroll.EmployeeRepo
import ppp.payroll.PayCheck
import ppp.payroll.PayCheckRepo
import java.time.LocalDate
import java.util.*

class PayCheckTest {

    private val employeeRepo: EmployeeRepo = EmployeeRepoImpl()

    private val payCheckRepo: PayCheckRepo = PayCheckRepoImpl(employeeRepo)

    private val employee = Employee()

    @BeforeAll
    fun setup() {
        employeeRepo.add(employee)
        val receipt = PayCheck(employee.id, LocalDate.now(), 230, "выплачено в банк J.P.Morgan")
        payCheckRepo.add(receipt)
    }

    @Test
    fun `добавим выплату зарплаты`() {
        val receipt = PayCheck(employee.id, LocalDate.now(), 100, "хранится у бухгалтера")
        payCheckRepo.add(receipt)
        assertThat(payCheckRepo.allFeatures()).contains(receipt)
        assertThat(payCheckRepo.featuresFor(employee.id)).contains(receipt)
    }

    @Test
    fun `конкретную выплату зарплаты нельзя учесть дважды`() {
        val receipt = PayCheck(employee.id, LocalDate.now().plusDays(3), 12, "отослано на домашний адрес")
        payCheckRepo.add(receipt)
        Assertions.assertThatThrownBy {
            payCheckRepo.add(receipt)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("twice")
    }

    @Test
    fun `нельзя учесть выплату зарплаты для несуществующего работника`() {
        val receipt = PayCheck(UUID.randomUUID(), LocalDate.now(), 1, "отдано в пользу бедных")
        Assertions.assertThatThrownBy {
            payCheckRepo.add(receipt)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("not found")

    }


}