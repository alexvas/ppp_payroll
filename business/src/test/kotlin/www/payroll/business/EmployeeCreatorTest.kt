package www.payroll.business

import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import ppp.payroll.*
import ppp.payroll.business.EmployeeCreation
import ppp.payroll.business.EmployeeCreatorFacade
import java.util.*

class EmployeeCreatorTest {

    private val employeeRepo: EmployeeRepo = mockk()
    private val employeeDetailRepo: EmployeeDetailRepo = mockk()
    private val wageRepo: WageRepo = mockk()
    private val payMethodRepo: PayMethodRepo = mockk()
    private val unionMembershipRepo: UnionMembershipRepo = mockk()

    private val employeeCreator = EmployeeCreatorFacade(
            employeeRepo,
            employeeDetailRepo,
            wageRepo,
            payMethodRepo,
            unionMembershipRepo,
    )

    @AfterEach
    fun clearRepoMocks() {
        clearMocks(
                employeeRepo,
                employeeDetailRepo,
                wageRepo,
                payMethodRepo,
                unionMembershipRepo,
        )
    }

    @Test
    fun createEmployee() {
        val employeeCreation = givenEmployeeCreation()

        // when
        employeeCreator.create(employeeCreation)

        thenVerifyEmployeeCreated(employeeCreation)
    }

    private fun givenEmployeeCreation(): EmployeeCreation {
        val employeeId: UUID = UUID.randomUUID()
        val employee = Employee(employeeId)
        val employeeDetail = EmployeeDetail(
                employeeId,
                "Вася",
                "где-то"
        )
        val wage: Wage = HourlyRate(employeeId, 12)
        val payMethod = PayMethodDirect(
                employeeId,
                "Deutsche Bank",
                779383
        )
        val employeeCreation = EmployeeCreation(
                employee,
                employeeDetail,
                wage,
                payMethod
        )
        employeeCreation.membership = UnionMembership(
                employeeId,
                3
        )

        every { employeeRepo.add(any()) } just Runs
        every { employeeDetailRepo.add(any()) } just Runs
        every { wageRepo.add(any()) } just Runs
        every { payMethodRepo.add(any()) } just Runs
        every { unionMembershipRepo.add(any()) } just Runs
        return employeeCreation
    }

    private fun thenVerifyEmployeeCreated(employeeCreation: EmployeeCreation) {
        verify {
            employeeRepo.add(employeeCreation.employee)
            employeeDetailRepo.add(employeeCreation.employeeDetail)
            wageRepo.add(employeeCreation.wage)
            payMethodRepo.add(employeeCreation.payMethod)
            unionMembershipRepo.add(employeeCreation.membership!!)
        }

        confirmVerified(
                employeeRepo,
                employeeDetailRepo,
                wageRepo,
                payMethodRepo,
                unionMembershipRepo,
        )
    }


}