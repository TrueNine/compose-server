package io.tn.rds.dao;

import io.tn.rds.base.BaseDao;
import org.hibernate.Hibernate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 时间区间，只能存在一个
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Getter
@Setter
@ToString
@DynamicInsert
@DynamicUpdate
@Entity
@Schema(title = "时间区间，只能存在一个")
@Table(name = DurationsDao.$T_NAME)
public class DurationsDao extends BaseDao implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  public static final String $T_NAME = "durations";

  public static final String NAME = "name";

  public static final String YEARS = "years";

  public static final String MONTHS = "months";

  public static final String MONTH_DAYS = "month_days";

  public static final String DAYS = "days";

  public static final String WEEKS = "weeks";

  public static final String HOURS = "hours";

  public static final String MINUTES = "minutes";

  public static final String SECONDS = "seconds";

  public static final String MILLIS = "millis";

  /**
   * 名称
   */
  @Schema(
    name = NAME,
    description = "名称"
  )
  @Column(table = $T_NAME,
    name = NAME)
  @Nullable
  private String name;

  /**
   * 年
   */
  @Schema(
    name = YEARS,
    description = "年"
  )
  @Column(table = $T_NAME,
    name = YEARS)
  @Nullable
  private Integer years;

  /**
   * 月
   */
  @Schema(
    name = MONTHS,
    description = "月"
  )
  @Column(table = $T_NAME,
    name = MONTHS)
  @Nullable
  private Integer months;

  /**
   * 月计算天数
   */
  @Schema(
    name = MONTH_DAYS,
    description = "月计算天数"
  )
  @Column(table = $T_NAME,
    name = MONTH_DAYS)
  @Nullable
  private Integer monthDays;

  /**
   * 日
   */
  @Schema(
    name = DAYS,
    description = "日"
  )
  @Column(table = $T_NAME,
    name = DAYS)
  @Nullable
  private Integer days;

  /**
   * 星期
   */
  @Schema(
    name = WEEKS,
    description = "星期"
  )
  @Column(table = $T_NAME,
    name = WEEKS)
  @Nullable
  private Integer weeks;

  /**
   * 小时
   */
  @Schema(
    name = HOURS,
    description = "小时"
  )
  @Column(table = $T_NAME,
    name = HOURS)
  @Nullable
  private Integer hours;

  /**
   * 分钟
   */
  @Schema(
    name = MINUTES,
    description = "分钟"
  )
  @Column(table = $T_NAME,
    name = MINUTES)
  @Nullable
  private Long minutes;

  /**
   * 秒
   */
  @Schema(
    name = SECONDS,
    description = "秒"
  )
  @Column(table = $T_NAME,
    name = SECONDS)
  @Nullable
  private Long seconds;

  /**
   * 毫秒
   */
  @Schema(
    name = MILLIS,
    description = "毫秒"
  )
  @Column(table = $T_NAME,
    name = MILLIS)
  @Nullable
  private Long millis;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    var that = (DurationsDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
