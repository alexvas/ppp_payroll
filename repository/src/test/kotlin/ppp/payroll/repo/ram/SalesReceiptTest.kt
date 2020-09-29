package ppp.payroll.repo.ram

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import ppp.payroll.Employee
import ppp.payroll.EmployeeRepo
import ppp.payroll.MultiRepo
import ppp.payroll.SalesReceipt
import java.time.Instant
import java.util.*

class SalesReceiptTest {

    private val employeeRepo: EmployeeRepo = EmployeeRepoImpl()

    private val salesReceiptRepo: MultiRepo<SalesReceipt> = MultiRepoBase(employeeRepo)

    private val employee = Employee()

    @BeforeAll
    fun setup() {
        employeeRepo.add(employee)
        val receipt = SalesReceipt(employee.id, Instant.now(), 200)
        salesReceiptRepo.add(receipt)
    }

    @Test
    fun `добавим продажи`() {
        val receipt = SalesReceipt(employee.id, Instant.now(), 100)
        salesReceiptRepo.add(receipt)
        assertThat(salesReceiptRepo.allFeatures()).contains(receipt)
        assertThat(salesReceiptRepo.featuresFor(employee.id)).contains(receipt)
    }

    @Test
    fun `конкретную продажу нельзя учесть дважды`() {
        val receipt = SalesReceipt(employee.id, Instant.now().plusMillis(150), 12)
        salesReceiptRepo.add(receipt)
        Assertions.assertThatThrownBy {
            salesReceiptRepo.add(receipt)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("twice")
    }

    @Test
    fun `нельзя учесть продажу для несуществующего работника`() {
        val receipt = SalesReceipt(UUID.randomUUID(), Instant.now(), 1)
        Assertions.assertThatThrownBy {
            salesReceiptRepo.add(receipt)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("not found")

    }


}