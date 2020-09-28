package ppp.payroll

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import ppp.payroll.repo.EmployeeRepo
import ppp.payroll.repo.PaymethodRepo
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
        EmployeeRepo.add(zina)
        val payMethod : PayMethod = PayMethodHold(zina.id)
        PaymethodRepo.add(payMethod)
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
        EmployeeRepo.add(alena)
        val payMethod : PayMethod = PayMethodDirect(alena.id, "Western Union", 1122334455667788990L)
        PaymethodRepo.add(payMethod)
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
        EmployeeRepo.add(aksinya)
        val payMethod : PayMethod = PayMethodMail(aksinya.id, "запад")
        PaymethodRepo.add(payMethod)
    }

    @Test
    fun `работнику нельзя заплатить дважды`() {
        val semyon: Employee = EmployeeFactory.createFlatMonthlySalariedEmployee(
                UUID.randomUUID(),
                "Семён",
                "северо-восток",
                201
        )
        EmployeeRepo.add(semyon)

        val payMethod1 : PayMethod = PayMethodMail(semyon.id, "северо-восток")
        PaymethodRepo.add(payMethod1)

        val payMethod2 : PayMethod = PayMethodHold(semyon.id)

        Assertions.assertThatThrownBy {
            PaymethodRepo.add(payMethod2)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("twice")
    }

    @Test
    fun `нельзя зарегистрировать выплаты несуществующему работнику`() {
        val payMethod : PayMethod = PayMethodDirect(UUID.randomUUID(), "J.P. Morgan", 998877665544332211L)

        Assertions.assertThatThrownBy {
            PaymethodRepo.add(payMethod)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("not found")
    }


}