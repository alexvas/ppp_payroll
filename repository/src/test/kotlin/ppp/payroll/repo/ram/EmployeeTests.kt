package ppp.payroll.repo.ram

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import ppp.payroll.*
import java.time.Instant

class EmployeeTests {

    private val employeeRepo: EmployeeRepo = EmployeeRepoImpl()

    private val timeCardRepo: MultiRepo<TimeCard> = MultiRepoBase(employeeRepo)

    private val salesReceiptRepo: MultiRepo<SalesReceipt> = MultiRepoBase(employeeRepo)

    private val unionChargeRepo: MonoRepo<UnionCharge> = MonoRepoBase(employeeRepo)

    private val payMethodRepo: MonoRepo<PayMethod> = MonoRepoBase(employeeRepo)

    private val wageRepo: MonoRepo<Wage> = MonoRepoBase(employeeRepo)

    private val payCheckRepo: MultiRepo<PayCheck> = MultiRepoBase(employeeRepo)

    private val detailRepo: MonoRepo<EmployeeDetail> = MonoRepoBase(employeeRepo)

    private val petya = Employee()

    private val vasya = Employee()

    @BeforeAll
    fun setup() {
        employeeRepo.add(petya)
        employeeRepo.add(vasya)
    }

    @Test
    fun `добавляем работников в репозиторий`() {

        val initialSize = employeeRepo.allEmployees().size

        val employee = Employee()

        employeeRepo.add(employee)

        assertThat(employeeRepo.allEmployees()).hasSize(initialSize + 1)
        assertThat(employeeRepo.allEmployees()).contains(petya, vasya, employee)
    }

    @Test
    fun `нельзя дважды добавить работника в репозиторий`() {
        val employee = Employee()

        employeeRepo.add(employee)
        assertThatThrownBy {
            employeeRepo.add(employee)
        }
                .isInstanceOf(RuntimeException::class.java)
                .hasMessageContaining("twice")
    }

    @Test
    fun `удаляем работника из репозитория`() {
        val employee = Employee()

        employeeRepo.add(employee)
        employeeRepo.remove(employee.id)

        assertThat(employeeRepo.allEmployees())
                .doesNotContainSequence(employee)
    }

    @Test
    fun `можно удалить работника с учтённым временем (удалится отовсюду)`() {
        val employee = Employee()
        employeeRepo.add(employee)
        val card = TimeCard(employee.id, Instant.now(), 2)
        timeCardRepo.add(card)
        employeeRepo.remove(employee.id)
        assertThat(timeCardRepo.featuresFor(employee.id).isEmpty())
    }

    @Test
    fun `можно удалить работника с продажами (удалится отовсюду)`() {
        val employee = Employee()
        employeeRepo.add(employee)
        val receipt = SalesReceipt(employee.id, Instant.now(), 700)
        salesReceiptRepo.add(receipt)
        employeeRepo.remove(employee.id)
        assertThat(salesReceiptRepo.featuresFor(employee.id).isEmpty())
    }

    @Test
    fun `можно удалить работника с профсоюзным взносом (удалится отовсюду)`() {
        val employee = Employee()
        employeeRepo.add(employee)
        val charge = UnionCharge(employee.id, 1700)
        unionChargeRepo.add(charge)
        employeeRepo.remove(employee.id)
        assertThat(unionChargeRepo.getFeatureFor(employee.id) == null)
    }

    @Test
    fun `можно удалить работника с выплатами (удалится отовсюду)`() {
        val employee = Employee()
        employeeRepo.add(employee)
        val payMethod = PayMethodHold(employee.id)
        payMethodRepo.add(payMethod)
        employeeRepo.remove(employee.id)
        assertThat(payMethodRepo.getFeatureFor(employee.id) == null)
    }

    @Test
    fun `можно удалить работника с зарплатой (удалится отовсюду)`() {
        val employee = Employee()
        employeeRepo.add(employee)
        val wage = FlatMonthlySalary(employee.id, 15)
        wageRepo.add(wage)
        employeeRepo.remove(employee.id)
        assertThat(wageRepo.getFeatureFor(employee.id) == null)
    }

    @Test
    fun `можно удалить работника с выплатами зарплаты (удалится отовсюду)`() {
        val employee = Employee()
        employeeRepo.add(employee)
        val check = PayCheck(employee.id, Instant.now(), 15, "выплачено в банк Голдман Сакс")
        payCheckRepo.add(check)
        employeeRepo.remove(employee.id)
        assertThat(payCheckRepo.featuresFor(employee.id).isEmpty())
    }

    @Test
    fun `можно удалить работника со сведениями о нём (удалится вместе со сведениями)`() {
        val employee = Employee()
        employeeRepo.add(employee)
        val detail = EmployeeDetail(employee.id, "Артур", "поблизости")
        detailRepo.add(detail)
        employeeRepo.remove(employee.id)
        assertThat(detailRepo.getFeatureFor(employee.id) == null)
    }

    @Test
    fun `успешно создаём работника`() {
        val employee = Employee()
        employeeRepo.add(employee)
        assertThat(employeeRepo.hasEmployee(employee.id)).isTrue
        val saved = employeeRepo.get(employee.id)!!
        assertThat(saved).isNotNull
    }

}