package ppp.payroll

import java.util.*

interface PayMethod: EmployeeFeature {
    override val employeeId: UUID
    val type: PayMethodType
}

enum class PayMethodType {
    HOLD, DIRECT, MAIL
}

data class PayMethodHold(override val employeeId: UUID) : PayMethod {
    override val type: PayMethodType
        get() = PayMethodType.HOLD
}

data class PayMethodDirect(
        override val employeeId: UUID,
        val bank: String,
        val account: Long,
) : PayMethod {
    override val type: PayMethodType
        get() = PayMethodType.DIRECT
}

data class PayMethodMail(
        override val employeeId: UUID,
        val address: String,
) : PayMethod {
    override val type: PayMethodType
        get() = PayMethodType.MAIL
}
