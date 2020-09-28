package ppp.payroll.repo

import ppp.payroll.TimeCard
import java.util.*
import kotlin.collections.LinkedHashSet

object TimeCardRepo {

    private val cards: MutableSet<TimeCard> = LinkedHashSet()

    private val modificationLock: Any = Any()

    fun add(card: TimeCard) {
        synchronized(modificationLock) {
            doAdd(card)
        }
    }

    private fun doAdd(card: TimeCard) {
        require(!cards.contains(card)) {
            "It is not allowed to add any card to the repo twice"
        }
        require(
                EmployeeRepo.hasEmployee(card.employeeId)
        ) {
            "Employee for card $card not found"
        }

        cards.add(card)
    }

    fun allCards() = cards.toList()

    fun cardsFor(employeeId: UUID) = cards.asSequence()
            .filter { it.employeeId == employeeId }
            .toList()
}
