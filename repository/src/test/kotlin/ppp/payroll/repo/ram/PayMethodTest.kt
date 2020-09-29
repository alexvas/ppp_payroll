package ppp.payroll.repo.ram

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ppp.payroll.*
import java.util.*

class PayMethodTest {

    @Test
    fun `добавляем Hold`() {
        val zina: Employee = EmployeeFactory.createCommissionedEmployee(
                UUID.randomUUID(),
                "Зина",
                "юг",
                1010,
                5.0
        )
        employeeRepo.add(zina)
        val payMethod : PayMethod = PayMethodHold(zina.id)
        payMethodRepo.add(payMethod)
        assertThat(payMethodRepo.getFeatureFor(zina.id)).isEqualTo(payMethod)
    }

    @Test
    fun `добавляем Direct`() {
        val alena: Employee = EmployeeFactory.createCommissionedEmployee(
                UUID.randomUUID(),
                "Алёна",
                "восток",
                1011,
                5.5
        )
        employeeRepo.add(alena)
        val payMethod : PayMethod = PayMethodDirect(alena.id, "Western Union", 1122334455667788990L)
        payMethodRepo.add(payMethod)
    }

    @Test
    fun `добавляем Mail`() {
        val aksinya: Employee = EmployeeFactory.createCommissionedEmployee(
                UUID.randomUUID(),
                "Аксинья",
                "запад",
                1012,
                7.5
        )
        employeeRepo.add(aksinya)
        val payMethod : PayMethod = PayMethodMail(aksinya.id, "запад")
        payMethodRepo.add(payMethod)
    }

    @Test
    fun `работнику нельзя заплатить дважды`() {
        val semyon: Employee = EmployeeFactory.createFlatMonthlySalariedEmployee(
                UUID.randomUUID(),
                "Семён",
                "северо-восток",
                201
        )
        employeeRepo.add(semyon)

        val payMethod1 : PayMethod = PayMethodMail(semyon.id, "северо-восток")
        payMethodRepo.add(payMethod1)

        val payMethod2 : PayMethod = PayMethodHold(semyon.id)

        Assertions.assertThatThrownBy {
            payMethodRepo.add(payMethod2)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("twice")
    }

    @Test
    fun `нельзя зарегистрировать выплаты несуществующему работнику`() {
        val payMethod : PayMethod = PayMethodDirect(UUID.randomUUID(), "J.P. Morgan", 998877665544332211L)

        Assertions.assertThatThrownBy {
            payMethodRepo.add(payMethod)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("not found")
    }


}