package ppp.payroll.repo.ram

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ppp.payroll.*
import java.util.*

class PayMethodTest {

    @Test
    fun `добавляем Hold`() {
        val zina = Employee(
                UUID.randomUUID(),
                "Зина",
                "юг",
        )
        employeeRepo.add(zina)
        val payMethod : PayMethod = PayMethodHold(zina.id)
        payMethodRepo.add(payMethod)
        assertThat(payMethodRepo.getFeatureFor(zina.id)).isEqualTo(payMethod)
    }

    @Test
    fun `добавляем Direct`() {
        val alena = Employee(
                UUID.randomUUID(),
                "Алёна",
                "восток",
        )
        employeeRepo.add(alena)
        val payMethod : PayMethod = PayMethodDirect(alena.id, "Western Union", 1122334455667788990L)
        payMethodRepo.add(payMethod)
    }

    @Test
    fun `добавляем Mail`() {
        val aksinya = Employee(
                UUID.randomUUID(),
                "Аксинья",
                "запад",
        )
        employeeRepo.add(aksinya)
        val payMethod : PayMethod = PayMethodMail(aksinya.id, "запад")
        payMethodRepo.add(payMethod)
    }

    @Test
    fun `работнику нельзя заплатить дважды`() {
        val semyon = Employee(
                UUID.randomUUID(),
                "Семён",
                "северо-восток",
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