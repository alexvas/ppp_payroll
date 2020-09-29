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

    fun add(feature: T)

    fun getFeatureFor(employeeId: UUID): T?
}

interface EmployeeRepo {
    fun interface RemovalListener {
        fun removed(employeeId: UUID)
    }

    fun interface Updater {
        fun update(original: Employee): Employee
    }

    fun add(employee: Employee)
    fun hasEmployee(employeeId: UUID): Boolean
    fun get(employeeId: UUID): Employee?

    /**
     * Updates any employee field except his or her ID. The employee must be already saved in the repository.
     */
    fun update(employeeId: UUID, updater: Updater)

    fun remove(employeeId: UUID)
    fun allEmployees(): List<Employee>
    fun addRemovalListener(removalListener: RemovalListener)
}
