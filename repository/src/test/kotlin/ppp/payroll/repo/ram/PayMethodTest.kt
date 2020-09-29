package ppp.payroll.repo.ram

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ppp.payroll.*
import java.util.*

class PayMethodTest {

    private val employeeRepo: EmployeeRepo = EmployeeRepoImpl()

    private val payMethodRepo: MonoRepo<PayMethod> = MonoRepoBase(employeeRepo)

    @Test
    fun `добавляем Hold`() {
        val employee = Employee()
        employeeRepo.add(employee)
        val payMethod : PayMethod = PayMethodHold(employee.id)
        payMethodRepo.add(payMethod)
        assertThat(payMethodRepo.getFeatureFor(employee.id)).isEqualTo(payMethod)
    }

    @Test
    fun `добавляем Direct`() {
        val employee = Employee()
        employeeRepo.add(employee)
        val payMethod : PayMethod = PayMethodDirect(employee.id, "Western Union", 1122334455667788990L)
        payMethodRepo.add(payMethod)
        assertThat(payMethodRepo.getFeatureFor(employee.id)).isEqualTo(payMethod)
    }

    @Test
    fun `добавляем Mail`() {
        val employee = Employee()
        employeeRepo.add(employee)
        val payMethod : PayMethod = PayMethodMail(employee.id, "запад")
        payMethodRepo.add(payMethod)
        assertThat(payMethodRepo.getFeatureFor(employee.id)).isEqualTo(payMethod)
    }

    @Test
    fun `работнику нельзя заплатить дважды`() {
        val employee = Employee()
        employeeRepo.add(employee)
        val payMethod1 : PayMethod = PayMethodMail(employee.id, "северо-восток")
        payMethodRepo.add(payMethod1)

        val payMethod2 : PayMethod = PayMethodHold(employee.id)

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

    @Test
    fun `можем поменять выплаты на Direct`() {
        val employee = Employee()
        employeeRepo.add(employee)
        val hold : PayMethod = PayMethodHold(employee.id)
        payMethodRepo.add(hold)
        val direct: PayMethod = PayMethodDirect(
                employee.id,
                "ЦБ РФ",
                332211
        )
        payMethodRepo.update(employee.id) {
            direct
        }
        assertThat(payMethodRepo.getFeatureFor(employee.id)).isEqualTo(direct)
    }

    @Test
    fun `можем поменять выплаты на Mail`() {
        val employee = Employee()
        employeeRepo.add(employee)
        val hold : PayMethod = PayMethodHold(employee.id)
        payMethodRepo.add(hold)
        val mail: PayMethod = PayMethodMail(
                employee.id,
                "туда-туда",
        )
        payMethodRepo.update(employee.id) {
            mail
        }
        assertThat(payMethodRepo.getFeatureFor(employee.id)).isEqualTo(mail)
    }

    @Test
    fun `можем поменять выплаты на Hold`() {
        val employee = Employee()
        employeeRepo.add(employee)
        val mail: PayMethod = PayMethodMail(
                employee.id,
                "туда-туда",
        )
        payMethodRepo.add(mail)
        val hold : PayMethod = PayMethodHold(employee.id)
        payMethodRepo.update(employee.id) {
            hold
        }
        assertThat(payMethodRepo.getFeatureFor(employee.id)).isEqualTo(hold)
    }


}