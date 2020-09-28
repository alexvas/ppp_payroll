package ppp.payroll

import java.util.*

object EmployeeFactory {
    fun createHourlyRatedEmployee(
            id: UUID,
            name: String,
            address: String,
            hourlyRate: Int,
    ): Employee {
        checkName(name)
        checkAddress(address)
        checkHourlyRate(hourlyRate)
        println("creating Hourly Rated Employee")
        return HourlyRatedEmployee(
                id,
                name,
                address,
                hourlyRate,
        )
    }

    fun createFlatMonthlySalariedEmployee(
            id: UUID,
            name: String,
            address: String,
            monthlySalary: Int,
    ): Employee {
        checkName(name)
        checkAddress(address)
        checkMonthlySalary(monthlySalary)
        println("creating Salaried Employee")
        return FlatMonthlySalariedEmployee(
                id,
                name,
                address,
                monthlySalary,
        )
    }

    fun createCommissionedEmployee(
            id: UUID,
            name: String,
            address: String,
            monthlySalary: Int,
            commissionPercent: Double
    ): Employee {
        checkName(name)
        checkAddress(address)
        checkMonthlySalary(monthlySalary)
        checkComissionPercent(commissionPercent)
        println("creating Commissioned Employee")
        return CommissionedEmployee(
                id,
                name,
                address,
                monthlySalary,
                commissionPercent,
        )
    }

    private fun checkName(name: String) =
            check(name.isNotBlank()) { "no name set" }

    private fun checkAddress(address: String) =
            check(address.isNotBlank()) { "no address set" }

    private fun checkHourlyRate(hourlyRate: Int) =
            check(hourlyRate > 0) { "hourly rate must be positive" }

    private fun checkMonthlySalary(monthlySalary: Int) =
            check(monthlySalary > 0) { "monthly salary must be positive" }

    private fun checkComissionPercent(commissionPercent: Double) {
        check(commissionPercent > 0) { "commission percent must be positive" }
        check(commissionPercent < 100) { "commission percent must be lesser than 100%" }
    }
}
