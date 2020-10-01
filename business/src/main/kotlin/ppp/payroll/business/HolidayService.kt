package ppp.payroll.business

import java.time.DayOfWeek
import java.time.LocalDate

/**
 * In production there would be a full fledged service able to take in account public holidays or work day transitions,
 * etc. For the sake of simplicity here we will consider as holidays Saturday and Sunday only.
 */
fun isHoliday(day: LocalDate): Boolean {
    //
    val dayOfWeek = day.dayOfWeek
    return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY
}
