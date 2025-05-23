package net.yan100.compose.depend.servlet.log

import net.yan100.compose.datetime
import net.yan100.compose.hasText
import net.yan100.compose.string
import org.slf4j.event.Level
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern
import kotlin.test.Test

data class MDCLogData(
  var datetime: datetime,
  var threadName: string?,
  var traceId: string?,
  var ip: string?,
  var level: Level,
  var abbrClassName: string,
  var logContent: string,
  var append: string?,
)

class SplitText {

  @Test
  fun `read log file and split`() {
    val resource =
      SplitText::class
        .java
        .classLoader
        .getResourceAsStream("test-split-log.txt")!!
    val reader = BufferedReader(InputStreamReader(resource))
    val datetimeFormatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    val pattern =
      Pattern.compile(
        "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}) \\[(.*?)\\] (\\w+) (.*)"
      )
    val logs = mutableListOf<MDCLogData>()
    var lastData: MDCLogData? = null

    reader.use {
      it.forEachLine { line ->
        if (line.hasText()) {
          val matcher = pattern.matcher(line.trim())
          if (matcher.matches() && matcher.groupCount() == 4) {
            val datetime = matcher.group(1)
            val mdcAndThreadName =
              with(matcher.group(2)) {
                val sep = indexOf(" - ")
                val mdc = substring(0..sep)
                val tidAndIp = mdc.trim().split(',')
                val mdcPair = tidAndIp[0] to tidAndIp[1]
                val c = substring(sep + 3)
                mdcPair to c
              }
            val level = Level.valueOf(matcher.group(3).uppercase())
            val packageNameAndLogContent =
              with(matcher.group(4)) {
                val sep = indexOf(" - ")
                val b = substring(0..sep)
                val c = substring(sep + 3)
                b to c
              }
            lastData =
              MDCLogData(
                datetime = LocalDateTime.parse(datetime, datetimeFormatter),
                traceId = mdcAndThreadName.first.first,
                threadName = mdcAndThreadName.second,
                ip = mdcAndThreadName.first.second,
                level = level,
                abbrClassName = packageNameAndLogContent.first,
                logContent = packageNameAndLogContent.second,
                append = "",
              )
            logs += lastData!!
          } else {
            lastData?.append += "$line\n"
            println()
          }
        }
      }
    }
    logs.forEach { println("${it.datetime} ${it.level} - ${it.logContent}") }
  }
}
