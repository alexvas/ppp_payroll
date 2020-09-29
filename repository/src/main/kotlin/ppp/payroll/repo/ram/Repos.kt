package ppp.payroll.repo.ram

import ppp.payroll.*
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.collections.LinkedHashSet

class MultiRepoBase<T : EmployeeFeature>(private val employeeRepo: EmployeeRepo) : MultiRepo<T> {
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

open class MonoRepoBase<T : EmployeeFeature>(private val employeeRepo: EmployeeRepo) : MonoRepo<T> {
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
}

class UnionMembershipRepoImpl(employeeRepo: EmployeeRepo) : UnionMembershipRepo, MonoRepoBase<UnionMembership>(employeeRepo) {
    override fun updateDueRate(employeeId: UUID, dueRate: Int) {
        synchronized(modificationLock) {
            doUpdateDueRate(employeeId, dueRate)
        }
    }

    private fun doUpdateDueRate(employeeId: UUID, dueRate: Int) {
        val membership = getFeatureFor(employeeId)
        require(membership != null) {
            "Employee of ID $employeeId is no member of any union"
        }
        features[employeeId] = membership.copy(dueRate = dueRate)
    }

    override fun noMember(employeeId: UUID) {
        synchronized(modificationLock) {
            features.remove(employeeId)
        }
    }

}