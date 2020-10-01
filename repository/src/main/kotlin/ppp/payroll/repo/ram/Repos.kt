package ppp.payroll.repo.ram

import ppp.payroll.*
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.collections.LinkedHashSet

abstract class MultiRepoBase<T : EmployeeFeature>(private val employeeRepo: EmployeeRepo) : MultiRepo<T> {
    private val modificationLock: Any = Any()
    private val features: MutableMap<UUID, MutableSet<T>> = LinkedHashMap()

    init {
        employeeRepo.addRemovalListener {
            synchronized(modificationLock) {
                features.remove(it)
            }
        }
    }

    override fun add(feature: T) {
        synchronized(modificationLock) {
            doAdd(feature)
        }
    }

    private fun doAdd(feature: T) {
        require(!(features[feature.employeeId]?.contains(feature) ?: false)) {
            "It is not allowed to add any item to the repo twice"
        }
        require(
                employeeRepo.hasEmployee(feature.employeeId)
        ) {
            "Employee for item $feature not found"
        }
        features.computeIfAbsent(feature.employeeId) { LinkedHashSet() }
                .add(feature)
    }

    override fun allFeatures() = features.asSequence().flatMap { it.value }.toList()

    override fun featuresFor(employeeId: UUID) = features[employeeId]?.toList() ?: emptyList()
}

abstract class MonoRepoBase<T : EmployeeFeature>(private val employeeRepo: EmployeeRepo) : MonoRepo<T> {
    internal val modificationLock: Any = Any()
    internal val features: MutableMap<UUID, T> = LinkedHashMap()

    init {
        employeeRepo.addRemovalListener {
            synchronized(modificationLock) {
                features.remove(it)
            }
        }
    }

    override fun add(feature: T) {
        synchronized(modificationLock) {
            doAdd(feature)
        }
    }

    private fun doAdd(item: T) {
        require(!features.containsKey(item.employeeId)) {
            "It is not allowed to add item $item to any employee twice"
        }
        require(employeeRepo.hasEmployee(item.employeeId)) {
            "Employee for item $item not found"
        }
        features[item.employeeId] = item
    }

    override fun getFeatureFor(employeeId: UUID): T? = features[employeeId]

    override fun update(employeeId: UUID, updater: MonoRepo.Updater<T>) {
        synchronized(modificationLock) {
            doUpdate(employeeId, updater)
        }
    }

    private fun doUpdate(employeeId: UUID, updater: MonoRepo.Updater<T>) {
        val original = features[employeeId]
        require(original != null) {
            "It is not allowed to update feature not in the repo"
        }
        val updated = updater.update(original)
        check(updated.employeeId == original.employeeId) {
            "It is not allowed to change employee id while updating"
        }
        features[employeeId] = updated
    }
}

class EmployeeDetailRepoImpl(employeeRepo: EmployeeRepo) : EmployeeDetailRepo, MonoRepoBase<EmployeeDetail>(employeeRepo)
class TimeCardRepoImpl(employeeRepo: EmployeeRepo) : TimeCardRepo, MultiRepoBase<TimeCard>(employeeRepo)
class UnionChargeRepoImpl(employeeRepo: EmployeeRepo) : UnionChargeRepo, MultiRepoBase<UnionCharge>(employeeRepo)
class PayMethodRepoImpl(employeeRepo: EmployeeRepo) : PayMethodRepo, MonoRepoBase<PayMethod>(employeeRepo)
class WageRepoImpl(employeeRepo: EmployeeRepo) : WageRepo, MonoRepoBase<Wage>(employeeRepo)

class SalesReceiptRepoImpl(employeeRepo: EmployeeRepo) : SalesReceiptRepo, MultiRepoBase<SalesReceipt>(employeeRepo) {
    override fun unpaidReceipts(employeeId: UUID): List<SalesReceipt> =
            featuresFor(employeeId).asSequence()
                    .filter { !it.commissionPayed }
                    .toList()

    override fun markReceiptsAsPaid(receipts: List<SalesReceipt>) =
            receipts.forEach { it.commissionPayed = true }
}

class PayCheckRepoImpl(employeeRepo: EmployeeRepo) : PayCheckRepo, MultiRepoBase<PayCheck>(employeeRepo) {

    override fun lastPayDay(employeeId: UUID) =
            featuresFor(employeeId).asSequence()
                    .map { it.date }
                    .maxOrNull()
}

class UnionMembershipRepoImpl(employeeRepo: EmployeeRepo) : UnionMembershipRepo, MonoRepoBase<UnionMembership>(employeeRepo) {

    override fun updateDueRate(employeeId: UUID, dueRate: Int) {
        update(employeeId) {
            it.copy(dueRate = dueRate)
        }
    }

    override fun noMember(employeeId: UUID) {
        synchronized(modificationLock) {
            features.remove(employeeId)
        }
    }
}