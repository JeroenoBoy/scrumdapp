package scrumdapp.services

import com.jeroenvdg.scrumdapp.db.CheckinRepositoryImpl
import com.jeroenvdg.scrumdapp.db.GroupRepository
import com.jeroenvdg.scrumdapp.db.GroupUser
import com.jeroenvdg.scrumdapp.models.Presence
import com.jeroenvdg.scrumdapp.services.AppException
import com.jeroenvdg.scrumdapp.utils.weekOfYear
import io.ktor.http.ContentType
import io.ktor.server.response.respondBytesWriter
import io.ktor.server.response.respondTextWriter
import io.ktor.server.routing.RoutingCall
import io.ktor.utils.io.jvm.javaio.toOutputStream
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.until
import kotlinx.html.dom.write
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Color
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.jetbrains.exposed.sql.Index
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor

class ExportService(val checkinRepository: CheckinRepositoryImpl, val groupRepository: GroupRepository) {

    suspend fun writeUserExport(groupUser: GroupUser, call: RoutingCall) {
        val checkins = checkinRepository.getUserCheckins(groupUser.user.id, groupUser.groupId)

        if (checkins.size < 2) {
            throw AppException(400, "De gebruiker moet 2 of meer checkins hebben", "Onvoldoende checkins")
        }

        val startDate = checkins.first().date
        val endDate = checkins.last().date

        startDate.minus(startDate.dayOfWeek.ordinal, DateTimeUnit.DAY);

            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet()

            sheet.setColumnWidth(1, 15*256)
            sheet.setColumnWidth(2, 15*256)
            sheet.setColumnWidth(3, 15*256)
            sheet.setColumnWidth(4, 15*256)
            sheet.setColumnWidth(5, 15*256)

            val presenceStyles: MutableMap<Presence, CellStyle> = mutableMapOf()
            val defaultCellStyle = createDefaultCellStyle(workbook)
            val weekStyle = createWeekStyle(workbook)
            val headerStyle = createHeaderStyle(workbook)

            presenceStyles[Presence.OnTime] = createPresenceCellStyle(workbook, Presence.OnTime)
            presenceStyles[Presence.VerifiedAbsent] = createPresenceCellStyle(workbook, Presence.VerifiedAbsent)
            presenceStyles[Presence.Late] = createPresenceCellStyle(workbook, Presence.Late)
            presenceStyles[Presence.Sick] = createPresenceCellStyle(workbook, Presence.Sick)
            presenceStyles[Presence.Absent] = createPresenceCellStyle(workbook, Presence.Absent)

            var rowNum = 0;
            val firstRow = sheet.createRow(rowNum++)
            
            firstRow.createCell(0).setCellValue("Presentie")
            firstRow.createCell(1).setCellValue(groupUser.user.name)

            val secondRow = sheet.createRow(rowNum++)
            var colNum = 0
            for (txt in listOf("Week", "Maandag", "Dinsdag", "Woensdag", "Donderdag", "Vrijdag")) {
                val col = secondRow.createCell(colNum++)
                col.setCellValue(txt)
                col.setCellStyle(headerStyle)
            }

            for (dateIndex in 0 .. startDate.until(endDate, DateTimeUnit.WEEK)) {
                val weekStartDay = startDate.plus(dateIndex, DateTimeUnit.WEEK)

                val row = sheet.createRow(rowNum++)
                val weekCol = row.createCell(0)
                weekCol.setCellValue("W${weekStartDay.weekOfYear} ${weekStartDay.year}")
                weekCol.setCellStyle(weekStyle)

                var colNum = 1
                for (i in 0 until 5) {
                    val date = weekStartDay.plus(i, DateTimeUnit.DAY);
                    val col = row.createCell(colNum++)

                    val checkin = checkins.find { it.date == date }
                    if (checkin?.date == date && checkin.presence?.key != null) {
                        col.setCellValue(checkin.presence!!.key)
                        col.setCellStyle(presenceStyles[checkin.presence])
                    } else {
                        col.setCellValue("--")
                        col.setCellStyle(defaultCellStyle)
                    }
                }
            }
        call.respondBytesWriter(ContentType.Application.Xlsx) {
            workbook.write(toOutputStream())
        }
    }

    private fun createPresenceCellStyle(book: XSSFWorkbook, presence: Presence): CellStyle {
        val style = book.createCellStyle()
        style.fillForegroundColor = when (presence) {
            Presence.OnTime -> IndexedColors.GREEN
            Presence.Late -> IndexedColors.YELLOW
            Presence.VerifiedAbsent -> IndexedColors.DARK_GREEN
            Presence.Absent -> IndexedColors.RED
            Presence.Sick -> IndexedColors.TEAL
        }.index
        style.fillPattern = FillPatternType.SOLID_FOREGROUND
        return style;
    }

    private fun createDefaultCellStyle(book: XSSFWorkbook): CellStyle {
        val style = book.createCellStyle()
        style.fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
        style.fillPattern = FillPatternType.SOLID_FOREGROUND
        return style;
    }

    private fun createWeekStyle(book: XSSFWorkbook): CellStyle {
        val style = book.createCellStyle()
        style.fillForegroundColor = IndexedColors.BROWN.index
        style.fillPattern = FillPatternType.SOLID_FOREGROUND
        return style
    }

    private fun createHeaderStyle(book: XSSFWorkbook): CellStyle {
        val style = book.createCellStyle()
        style.fillForegroundColor = IndexedColors.BLUE.index
        style.fillPattern = FillPatternType.SOLID_FOREGROUND
        return style
    }

}