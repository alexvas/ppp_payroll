package ppp.payroll

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import ppp.payroll.repo.EmployeeRepo
import ppp.payroll.repo.salesReceiptRepo
import java.time.Instant
import java.util.*

class SalesReceiptTest {
    private val sonya: Employee = EmployeeFactory.createCommissionedEmployee(
            UUID.randomUUID(),
            "Соня",
            "везде",
            10,
            5.0
    )

    @BeforeAll
    fun setup() {
        EmployeeRepo.add(sonya)
        val receipt = SalesReceipt(sonya.id, Instant.now(), 200)
        salesReceiptRepo.add(receipt)
    }

    @Test
    fun `добавим продажи`() {
        val receipt = SalesReceipt(sonya.id, Instant.now(), 100)
        salesReceiptRepo.add(receipt)
        assertThat(salesReceiptRepo.allFeatures()).contains(receipt)
    }

    @Test
    fun `конкретную продажу нельзя учесть дважды`() {
        val receipt = SalesReceipt(sonya.id, Instant.now().plusMillis(150), 12)
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