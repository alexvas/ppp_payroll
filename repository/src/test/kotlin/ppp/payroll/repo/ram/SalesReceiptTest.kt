package ppp.payroll.repo.ram

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import ppp.payroll.Employee
import ppp.payroll.EmployeeRepo
import ppp.payroll.SalesReceipt
import ppp.payroll.SalesReceiptRepo
import java.util.*

class SalesReceiptTest {

    private val employeeRepo: EmployeeRepo = EmployeeRepoImpl()

    private val salesReceiptRepo: SalesReceiptRepo = SalesReceiptRepoImpl(employeeRepo)

    private val employee = Employee()

    @BeforeAll
    fun setup() {
        employeeRepo.add(employee)
        val receipt = SalesReceipt(employee.id, UUID.randomUUID(), 200)
        salesReceiptRepo.add(receipt)
    }

    @Test
    fun `добавим продажи`() {
        val receipt = SalesReceipt(employee.id, UUID.randomUUID(), 100)
        salesReceiptRepo.add(receipt)
        assertThat(salesReceiptRepo.allFeatures()).contains(receipt)
        assertThat(salesReceiptRepo.featuresFor(employee.id)).contains(receipt)
    }

    @Test
    fun `получим продажи с невыплаченной комиссией`() {
        // given
        val ann = Employee()
        employeeRepo.add(ann)
        val receipt = SalesReceipt(ann.id, UUID.randomUUID(), 200)
        salesReceiptRepo.add(receipt)

        // when
        val annResult = salesReceiptRepo.unpaidReceipts(ann.id)
        val employeeResult = salesReceiptRepo.unpaidReceipts(employee.id)

        // then
        assertThat(annResult).isNotEmpty.hasSize(1).contains(receipt)
        assertThat(employeeResult).isNotEmpty.doesNotContain(receipt)
    }

    @Test
    fun `_не_ получим продажи с выплаченной комиссией`() {
        // given
        val ann = Employee()
        employeeRepo.add(ann)
        val receipt = SalesReceipt(
                employeeId = ann.id,
                id = UUID.randomUUID(),
                amount = 200,
                commissionPayed = true,
        )
        salesReceiptRepo.add(receipt)

        // when
        val annResult = salesReceiptRepo.unpaidReceipts(ann.id)
        val employeeResult = salesReceiptRepo.unpaidReceipts(employee.id)

        // then
        assertThat(annResult).isEmpty()
        assertThat(employeeResult).isNotEmpty.doesNotContain(receipt)
    }

    @Test
    fun `можем пометить комиссию выплаченной`() {
        // given
        val ann = Employee()
        employeeRepo.add(ann)
        val receipt = SalesReceipt(
                employeeId = ann.id,
                id = UUID.randomUUID(),
                amount = 200,
                commissionPayed = false,
        )
        salesReceiptRepo.add(receipt)

        // when
        salesReceiptRepo.markReceiptsAsPaid(listOf(receipt))

        // then
        assertThat(receipt.commissionPayed).isTrue
    }

    @Test
    fun `конкретную продажу нельзя учесть дважды`() {
        val receipt = SalesReceipt(employee.id, UUID.randomUUID(), 12)
        salesReceiptRepo.add(receipt)
        Assertions.assertThatThrownBy {
            salesReceiptRepo.add(receipt)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("twice")
    }

    @Test
    fun `нельзя учесть продажу для несуществующего работника`() {
        val receipt = SalesReceipt(UUID.randomUUID(), UUID.randomUUID(), 1)
        Assertions.assertThatThrownBy {
            salesReceiptRepo.add(receipt)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("not found")

    }


}