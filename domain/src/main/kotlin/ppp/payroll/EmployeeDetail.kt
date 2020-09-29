package ppp.payroll

import java.time.Instant
import java.util.*

data class EmployeeDetail(
        override val employeeId: UUID,
        val name: String,
        val address: String,
        val recruitment: Instant = Instant.now(),
): EmployeeFeature {

    init {
        requireNamePrecondition(name)
        requireAddressPrecondition(address)
    }

    private fun requireNamePrecondition(name: String) =
            require(name.isNotBlank()) { "no name set" }

    private fun requireAddressPrecondition(address: String) =
            require(address.isNotBlank()) { "no address set" }

}
