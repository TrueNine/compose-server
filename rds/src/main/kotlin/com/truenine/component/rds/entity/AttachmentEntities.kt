package com.truenine.component.rds.entity;

import com.truenine.component.rds.entity.supers.SuperAttachmentEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

/**
 * 文件
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@DynamicInsert
@DynamicUpdate
@Entity
@Schema(title = "附件")
@Table(name = AttachmentEntity.TABLE_NAME)
public class AttachmentEntity extends SuperAttachmentEntity implements Serializable {
}
