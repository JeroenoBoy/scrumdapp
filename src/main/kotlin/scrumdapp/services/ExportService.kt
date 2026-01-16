package scrumdapp.services

import com.jeroenvdg.scrumdapp.db.CheckinRepositoryImpl
import com.jeroenvdg.scrumdapp.db.GroupRepository
import com.jeroenvdg.scrumdapp.db.GroupUser
import com.jeroenvdg.scrumdapp.services.AppException
import com.jeroenvdg.scrumdapp.utils.weekOfYear
import io.ktor.http.ContentType
import io.ktor.server.response.respondTextWriter
import io.ktor.server.routing.RoutingCall
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.until

class ExportService(val checkinRepository: CheckinRepositoryImpl, val groupRepository: GroupRepository) {

    suspend fun writeUserExport(groupUser: GroupUser, call: RoutingCall) {
        val checkins = checkinRepository.getUserCheckins(groupUser.user.id, groupUser.groupId)

        if (checkins.size < 2) {
            throw AppException(400, "De gebruiker moet 2 of meer checkins hebben", "Onvoldoende checkins")
        }

        val startDate = checkins.first().date
        val endDate = checkins.last().date

        startDate.minus(startDate.dayOfWeek.ordinal, DateTimeUnit.DAY);

        call.respondTextWriter(ContentType.Text.CSV) {
            write("Presentie van")
            write(",")
            write(groupUser.user.name)
            appendLine()
            write("Week,Maandag,Dinsdag,Woensdag,Donderdag,Vrijdag")

            for (dateIndex in 0 .. startDate.until(endDate, DateTimeUnit.WEEK)) {
                val weekStartDay = startDate.plus(dateIndex, DateTimeUnit.WEEK)

                appendLine()
                write("W")
                write(weekStartDay.weekOfYear.toString())
                write(" ")
                write(weekStartDay.year.toString())

                for (i in 0 until 5) {
                    val date = weekStartDay.plus(i, DateTimeUnit.DAY);
                    val checkin = checkins.find { it.date == date }
                    write(",")
                    if (checkin?.date == date) {
                        write(checkin.presence?.key ?: "--")
                    } else {
                        write("--")
                    }
                }
            }
        }
    }

}