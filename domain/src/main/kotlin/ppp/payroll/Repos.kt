package ppp.payroll

import java.util.*

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

    fun interface Updater<F> {
        fun update(original: F): F
    }

    fun add(feature: T)

    fun getFeatureFor(employeeId: UUID): T?

    /**
     * Updates any feature field except it's ID. The feature must be already saved in the repository.
     */
    fun update(employeeId: UUID, updater: Updater<T>)
}

interface EmployeeRepo {
    fun interface RemovalListener {
        fun removed(employeeId: UUID)
    }

    fun add(employee: Employee)
    fun hasEmployee(employeeId: UUID): Boolean
    fun get(employeeId: UUID): Employee?

    fun remove(employeeId: UUID)
    fun allEmployees(): List<Employee>
    fun addRemovalListener(removalListener: RemovalListener)
}

interface EmployeeDetailRepo : MonoRepo<EmployeeDetail>
interface TimeCardRepo : MultiRepo<TimeCard>
interface SalesReceiptRepo : MultiRepo<SalesReceipt>
interface UnionChargeRepo : MultiRepo<UnionCharge>
interface PayMethodRepo : MonoRepo<PayMethod>
interface WageRepo : MonoRepo<Wage>
interface PayCheckRepo : MultiRepo<PayCheck>

interface UnionMembershipRepo : MonoRepo<UnionMembership> {
    fun updateDueRate(employeeId: UUID, dueRate: Int)
    fun noMember(employeeId: UUID)
}