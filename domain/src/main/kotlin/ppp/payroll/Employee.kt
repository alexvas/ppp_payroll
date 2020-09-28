package ppp.payroll

import java.util.*

interface Employee {
    val id: UUID
    val name: String
    val address: String
    val type: EmployeeType
}

data class HourlyRatedEmployee(
        override val id: UUID,
        override val name: String,
        override val address: String,
        val hourlyRate: Int,
) : Employee {

    override val type: EmployeeType
        get() = EmployeeType.HOURLY_RATED

    init {
        requireNamePrecondition(name)
        requireAddressPrecondition(address)
        requireHourlyRatePrecondition(hourlyRate)
    }

    private fun requireHourlyRatePrecondition(hourlyRate: Int) =
            require(hourlyRate > 0) { "hourly rate must be positive" }
}

data class FlatMonthlySalariedEmployee(
        override val id: UUID,
        override val name: String,
        override val address: String,
        val monthlySalary: Int,
) : Employee {

    override val type: EmployeeType
        get() = EmployeeType.FLAT_MONTHLY_SALARIED

    init {
        requireNamePrecondition(name)
        requireAddressPrecondition(address)
        requireMonthlySalaryPrecondition(monthlySalary)
    }
}

data class CommissionedEmployee(
        override val id: UUID,
        override val name: String,
        override val address: String,
        val monthlySalary: Int,
        val commission: Double,
) : Employee {

    override val type: EmployeeType
        get() = EmployeeType.COMMISSIONED

    init {
        requireNamePrecondition(name)
        requireAddressPrecondition(address)
        requireMonthlySalaryPrecondition(monthlySalary)
        requireCommissionPercentPrecondition(commission)
    }

    private fun requireCommissionPercentPrecondition(commissionPercent: Double) {
        require(commissionPercent > 0) { "commission percent must be positive" }
        require(commissionPercent < 100) { "commission percent must be lesser than 100%" }
    }
}

private fun requireNamePrecondition(name: String) =
        require(name.isNotBlank()) { "no name set" }

private fun requireAddressPrecondition(address: String) =
        require(address.isNotBlank()) { "no address set" }

private fun requireMonthlySalaryPrecondition(monthlySalary: Int) =
        require(monthlySalary > 0) { "monthly salary must be positive" }
