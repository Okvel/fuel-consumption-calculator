package by.liauko.siarhei.cl.util

import android.content.Context
import androidx.appcompat.app.AlertDialog
import by.liauko.siarhei.cl.R
import by.liauko.siarhei.cl.activity.dialog.ProgressDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Calendar

object ApplicationUtil {

    const val EMPTY_STRING = ""

    var type = DataType.LOG
    var dataPeriod = DataPeriod.MONTH
    var periodCalendar: Calendar = Calendar.getInstance()
    var profileId = -1L
    var profileName: String = EMPTY_STRING

    fun createProgressDialog(context: Context, messageId: Int)
            = ProgressDialog(context, context.getString(messageId))

    fun createAlertDialog(context: Context, titleId: Int, messageId: Int): AlertDialog
            = MaterialAlertDialogBuilder(context)
        .setTitle(titleId)
        .setMessage(messageId)
        .setPositiveButton(
            context.getString(R.string.dialog_backup_alert_ok_button)
        ) { dialog, _ -> dialog.dismiss() }
        .create()

    fun prepareDateRange(): Pair<Long, Long> {
        periodCalendar.set(Calendar.HOUR_OF_DAY, 0)
        periodCalendar.clear(Calendar.MINUTE)
        periodCalendar.clear(Calendar.SECOND)
        periodCalendar.clear(Calendar.MILLISECOND)

        val dayType = when (dataPeriod) {
            DataPeriod.MONTH -> Calendar.DAY_OF_MONTH
            DataPeriod.YEAR -> Calendar.DAY_OF_YEAR
            DataPeriod.ALL -> 0
        }

        periodCalendar.set(dayType, 1)
        val startTime = periodCalendar.timeInMillis

        periodCalendar.set(Calendar.HOUR_OF_DAY, periodCalendar.getActualMaximum(Calendar.HOUR_OF_DAY))
        periodCalendar.set(Calendar.MINUTE, periodCalendar.getActualMaximum(Calendar.MINUTE))
        periodCalendar.set(Calendar.SECOND, periodCalendar.getActualMaximum(Calendar.SECOND))
        periodCalendar.set(Calendar.MILLISECOND, periodCalendar.getActualMaximum(Calendar.MILLISECOND))
        periodCalendar.set(dayType, periodCalendar.getActualMaximum(dayType))
        val endTime = periodCalendar.timeInMillis

        return Pair(startTime, endTime)
    }
}

enum class DataType {
    LOG, FUEL
}

enum class DataPeriod {
    MONTH, YEAR, ALL
}

enum class CarBodyType {
    SEDAN, HATCHBACK, SUV, WAGON, COUPE, VAN, JEEP, CONVERTIBLE
}

enum class CarFuelType {
    GASOLINE, DIESEL, GAS, ETHANOL, HYBRID, ELECTRICITY
}

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}
