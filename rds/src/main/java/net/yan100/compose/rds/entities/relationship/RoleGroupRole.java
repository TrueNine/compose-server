package net.yan100.compose.rds.entities.relationship;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.yan100.compose.rds.core.entities.IEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 角色组  角色
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Getter
@Setter
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "角色组  角色")
@Table(name = RoleGroupRole.TABLE_NAME)
public class RoleGroupRole extends IEntity {
    public static final String TABLE_NAME = "role_group_role";
    public static final String ROLE_GROUP_ID = "role_group_id";
    public static final String ROLE_ID = "role_id";
    /**
     * 用户组
     */
    @Nullable
    @Schema(title = "用户组")
    @Column(name = ROLE_GROUP_ID)
    private String roleGroupId;

    /**
     * 角色
     */
    @Nullable
    @Schema(title = "角色")
    @Column(name = ROLE_ID)
    private String roleId;
}
