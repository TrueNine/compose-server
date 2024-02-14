package net.yan100.compose.rds.entities.cert

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.core.alias.ReferenceId
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.core.alias.datetime
import net.yan100.compose.core.models.IIdcard2Code
import net.yan100.compose.rds.Col
import net.yan100.compose.rds.converters.GenderTypingConverter
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.typing.GenderTyping
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

@MappedSuperclass
abstract class SuperIdcard2 : IIdcard2Code, IEntity() {
    companion object {
        const val TABLE_NAME = "idcard_2"
        const val NAME = "name"
        const val USER_ID = "user_id"
        const val USER_INFO_ID = "user_info_id"
        const val ADDRESS_DETAILS_ID = "address_details_id"
        const val GENDER = "gender"
        const val CODE = "code"
        const val BIRTHDAY = "birthday"
        const val EXPIRE_DATE = "expire_date"
        const val ISSUE_ORGAN = "issue_organ"
        const val ETHNIC_GROUP = "ethnic_group"
    }

    @Schema(title = "用户信息")
    @Col(name = USER_INFO_ID)
    var userInfoId: RefId? = null

    @Schema(title = "签发机构")
    @Column(name = ISSUE_ORGAN)
    var issueOrgan: String? = null

    @Schema(title = "身份证过期时间")
    @Column(name = EXPIRE_DATE)
    var expireDate: datetime? = null

    @Schema(title = "民族")
    @Column(name = ETHNIC_GROUP)
    var ethnicGroup: String? = null

    @Schema(title = "生日")
    @Column(name = BIRTHDAY)
    var birthday: datetime? = null

    @NotNull
    @Schema(title = "身份证号")
    @Column(name = CODE)
    lateinit var code: SerialCode

    @Schema(title = "性别")
    @Column(name = GENDER)
    @Convert(converter = GenderTypingConverter::class)
    var gender: GenderTyping? = null

    @Schema(title = "外联 地址详情id（出生地）")
    @Column(name = ADDRESS_DETAILS_ID)
    var addressDetailsId: ReferenceId? = null

    @NotNull
    @Schema(title = "名称")
    @Column(name = NAME)
    lateinit var name: String

    @Schema(title = "外联 用户（所属用户）")
    @Column(name = USER_ID)
    var userId: ReferenceId? = null

    @get:Transient
    @get:JsonIgnore
    override val idcard2Code: SerialCode get() = this.code
}

@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "第二代身份证")
@Table(name = SuperIdcard2.TABLE_NAME)
class Idcard2 : SuperIdcard2()
