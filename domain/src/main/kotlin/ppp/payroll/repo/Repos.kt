package ppp.payroll.repo

import ppp.payroll.PayMethod
import ppp.payroll.SalesReceipt
import ppp.payroll.TimeCard
import ppp.payroll.UnionCharge
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.collections.LinkedHashSet

interface EmployeeFeature {
    val employeeId: UUID
}

/**
 * The repository allows multiple features per single employee
 * e.g. TimeCards or SalesReceipts. The repo reflects
 * One-to-many (Employee to particular Feature Set) relationship.
 * Yet duplicate features are not allowed here.
 */
interface MultiRepo<T : EmployeeFeature> {

    fun add(feature: T)

    fun allFeatures(): List<T>

    fun featuresFor(employeeId: UUID): List<T>
}

/**
 * The repository allows single feature per single employee
 * e.g. PayMethod, UnionCharge or PayDeliveryMethod. The repo reflects
 * One-to-one (Employee to Feature) relationship.
 * Obviously, duplicate features are not allowed here.
 */
interface MonoRepo<T : EmployeeFeature> {

    fun add(feature: T)

    fun getFeatureFor(employeeId: UUID): T?
}

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

class MonoRepoBase<T : EmployeeFeature>(private val employeeRepo: EmployeeRepo) : MonoRepo<T> {
    private val modificationLock: Any = Any()
    private val features: MutableMap<UUID, T> = LinkedHashMap()

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
        require(
                employeeRepo.hasEmployee(item.employeeId)
        ) {
            "Employee for item $item not found"
        }
        features[item.employeeId] = item
    }

    override fun getFeatureFor(employeeId: UUID): T? = features[employeeId]
}

val timeCardRepo: MultiRepo<TimeCard> = MultiRepoBase(EmployeeRepo)

val salesReceiptRepo: MultiRepo<SalesReceipt> = MultiRepoBase(EmployeeRepo)

val unionChargeRepo: MonoRepo<UnionCharge> = MonoRepoBase(EmployeeRepo)

val payMethodRepo: MonoRepo<PayMethod> = MonoRepoBase(EmployeeRepo)