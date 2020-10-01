package ppp.payroll.business

import io.mockk.*
import ppp.payroll.PayCheckRepo
import ppp.payroll.SalesReceipt
import ppp.payroll.SalesReceiptRepo
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
    confirmVerified(this)
}

fun noSalesReceipt(): SalesReceiptRepo {
    val salesReceiptRepo: SalesReceiptRepo = mockk()
    every {
        salesReceiptRepo.unpaidReceipts(any())
    } returns listOf()
    return salesReceiptRepo
}

fun givenThereWasAnUnpaidSalesReceiptFor(employeeId: UUID): SalesReceiptRepo {
    val salesReceiptRepo: SalesReceiptRepo = mockk()
    val salesReceipt = SalesReceipt(
            employeeId,
            UUID.randomUUID(),
            200,
            false
    )
    val answer = listOf(salesReceipt)
    every {
        salesReceiptRepo.unpaidReceipts(any())
    } returns answer
    every {
        salesReceiptRepo.markReceiptsAsPaid(answer)
    } just Runs
    return salesReceiptRepo
}

fun SalesReceiptRepo.unpaidReceiptsCalled(employeeId: UUID) {
    verify {
        unpaidReceipts(employeeId)
    }
    confirmVerified(this)
}

fun SalesReceiptRepo.unpaidReceiptsCalledThenMarkedAsPaid(employeeId: UUID) {
    verify {
        unpaidReceipts(employeeId)
        markReceiptsAsPaid(any())
    }
    confirmVerified(this)
}

