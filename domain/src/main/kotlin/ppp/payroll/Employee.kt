package ppp.payroll

import java.util.*

data class Employee(
        val id: UUID,
        val name: String,
        val address: String,
) {

    init {
        requireNamePrecondition(name)
        requireAddressPrecondition(address)
    }

    private fun requireNamePrecondition(name: String) =
            require(name.isNotBlank()) { "no name set" }

    private fun requireAddressPrecondition(address: String) =
            require(address.isNotBlank()) { "no address set" }
}
