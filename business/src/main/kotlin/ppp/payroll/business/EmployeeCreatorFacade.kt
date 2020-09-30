package ppp.payroll.business

import ppp.payroll.*

class EmployeeCreatorFacade(
        private val employeeRepo: EmployeeRepo,
        private val employeeDetailRepo: EmployeeDetailRepo,
        private val wageRepo: WageRepo,
        private val payMethodRepo: PayMethodRepo,
        private val unionMembershipRepo: UnionMembershipRepo,
) {
    fun create(employeeCreation: EmployeeCreation) {
        employeeRepo.add(employeeCreation.employee)
        employeeDetailRepo.add(employeeCreation.employeeDetail)
        wageRepo.add(employeeCreation.wage)
        payMethodRepo.add(employeeCreation.payMethod)
        employeeCreation.membership?.let { unionMembershipRepo.add(it) }
    }

}
