package ppp.payroll

import java.util.*

enum class WageType {
    HOURLY_RATE, FLAT_MONTHLY_SALARY, COMMISSION
}

interface Wage: EmployeeFeature {
    val type: WageType
}

data class HourlyRate(
        override val employeeId: UUID,
        val hourlyRate: Int,
) : Wage {

    override val type: WageType
        get() = WageType.HOURLY_RATE

    init {
        requireHourlyRatePrecondition(hourlyRate)
    }

    private fun requireHourlyRatePrecondition(hourlyRate: Int) =
            require(hourlyRate > 0) { "hourly rate must be positive" }
}

data class FlatMonthlySalary(
        override val employeeId: UUID,
        val monthlySalary: Int,
) : Wage {

    override val type: WageType
        get() = WageType.FLAT_MONTHLY_SALARY

    init {
        requireMonthlySalaryPrecondition(monthlySalary)
    }
}

data class Commission(
        override val employeeId: UUID,
        val monthlySalary: Int,
        val commission: Double,
) : Wage {

    override val type: WageType
        get() = WageType.COMMISSION

    init {
        requireMonthlySalaryPrecondition(monthlySalary)
        requireCommissionPercentPrecondition(commission)
    }

    private fun requireCommissionPercentPrecondition(commissionPercent: Double) {
        require(commissionPercent > 0) { "commission percent must be positive" }
        require(commissionPercent < 100) { "commission percent must be lesser than 100%" }
    }
}

private fun requireMonthlySalaryPrecondition(monthlySalary: Int) =
        require(monthlySalary > 0) { "monthly salary must be positive" }
