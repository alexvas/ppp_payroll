package ppp.payroll.business

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ppp.payroll.PayCheckRepo
import java.time.LocalDate
import java.util.*


fun noPayment(): PayCheckRepo {
    val payCheckRepo: PayCheckRepo = mockk()
    every {
        payCheckRepo.lastPayDay(any())
    } returns null
    return payCheckRepo
}

fun givenThereWasAPaymentOn(lastPayDay: LocalDate): PayCheckRepo {
    val payCheckRepo: PayCheckRepo = mockk()
    every {
        payCheckRepo.lastPayDay(any())
    } returns lastPayDay
    return payCheckRepo
}

fun PayCheckRepo.lastPayDayWasCalledWith(employeeId: UUID) {
    verify {
        lastPayDay(employeeId)
    }
    confirmVerified(
            this,
    )
}