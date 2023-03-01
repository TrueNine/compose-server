package io.tn.core.lang;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 时间工具类
 *
 * @author TrueNine
 * @since 2022-12-16
 */
public class DTimer {
  public static final String MILLIS = "SSS";
  public static final String DATE = "yyyy-MM-dd";
  public static final String TIME = "HH:mm:ss";
  public static final String DATETIME = DATE + " " + TIME;
  public static final String TIME_M = TIME + " " + MILLIS;
  public static final String DATETIME_M = DATETIME + " " + MILLIS;

  public static final String DATE_S = "yyyy/MM/dd";
  public static final String DATETIME_S = DATE_S + " " + TIME;
  public static final String DATETIME_M_S = DATETIME_S + " " + MILLIS;


  public static void sleepMillis(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }


  public static void sleep(long second) {
    sleepMillis(second * 1000);
  }

  public static void sleepOne() {
    sleep(1);
  }

  public static Duration ofSecondDuration(long second) {
    return Duration.of(second, ChronoUnit.SECONDS);
  }

  public static Duration ofMillisDuration(long millis) {
    return Duration.of(millis, ChronoUnit.MILLIS);
  }

  public static Date plusDate(long plusMillis) {
    return plusMillis(System.currentTimeMillis(), plusMillis);
  }

  public static Date plusMillis(long current, long plusMillis) {
    return new Date(current + plusMillis);
  }

  public static Date localTimeToDate(LocalTime lt) {
    var meta = LocalDate.of(1970, 1, 1);
    var ldt = lt.atDate(meta);
    return Date.from(
        ldt.atZone(ZoneId.of("GMT"))
            .toInstant()
    );
  }

  public static Date localDateToDate(LocalDate ld) {
    return Date.from(
        ld.atStartOfDay().atZone(ZoneId.systemDefault())
            .toInstant()
    );
  }

  public static Date localDatetimeToDate(LocalDateTime ldt) {
    return Date.from(
        ldt.atZone(ZoneId.systemDefault())
            .toInstant()
    );
  }
}
