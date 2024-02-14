package net.yan100.compose.rds.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.yan100.compose.core.consts.DataBaseBasicFieldNames;

import java.io.Serial;
import java.io.Serializable;


/**
 * 带外键的 预排序树
 *
 * @author TrueNine
 * @since 2022-12-15
 */
@Setter
@Getter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "预排序树和任意外键的结合体")
public class TreeAnyRefEntity extends TreeEntity implements Serializable {

    /**
     * 任意外键
     */
    public static final String ARI = DataBaseBasicFieldNames.ANY_REFERENCE_ID;

    /**
     * 任意类型
     */
    public static final String TYP = DataBaseBasicFieldNames.ANY_REFERENCE_TYPE;
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    @Column(name = DataBaseBasicFieldNames.ANY_REFERENCE_ID)
    @Schema(title = "任意外键id")
    protected String ari;

    @JsonIgnore
    @Column(name = DataBaseBasicFieldNames.ANY_REFERENCE_TYPE)
    @Schema(title = "外键类别")
    protected String typ;
}
