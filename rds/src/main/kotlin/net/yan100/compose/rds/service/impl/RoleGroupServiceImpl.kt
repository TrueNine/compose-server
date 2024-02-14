package net.yan100.compose.rds.service.impl


import net.yan100.compose.core.consts.DataBaseBasicFieldNames
import net.yan100.compose.rds.entities.RoleGroup
import net.yan100.compose.rds.repositories.relationship.IUserRoleGroupRepo
import net.yan100.compose.rds.service.IRoleGroupService
import net.yan100.compose.rds.service.base.CrudService
import org.springframework.stereotype.Service

@Service
class RoleGroupServiceImpl(
    private val rgRepo: net.yan100.compose.rds.repositories.RoleGroupRepo,
    private val urRepo: IUserRoleGroupRepo
) : IRoleGroupService, CrudService<RoleGroup>(rgRepo) {
    override fun assignRootToUser(userId: String): net.yan100.compose.rds.entities.relationship.UserRoleGroup {
        return net.yan100.compose.rds.entities.relationship.UserRoleGroup().apply {
            this.roleGroupId = DataBaseBasicFieldNames.Rbac.ROOT_ID_STR
            this.userId = userId
        }.let { urRepo.save(it) }
    }

    override fun assignPlainToUser(userId: String): net.yan100.compose.rds.entities.relationship.UserRoleGroup {
        return net.yan100.compose.rds.entities.relationship.UserRoleGroup().apply {
            this.roleGroupId = DataBaseBasicFieldNames.Rbac.USER_ID_STR
            this.userId = userId
        }.let { urRepo.save(it) }
    }

    override fun assignAdminToUser(userId: String): net.yan100.compose.rds.entities.relationship.UserRoleGroup {
        return net.yan100.compose.rds.entities.relationship.UserRoleGroup().apply {
            this.roleGroupId = DataBaseBasicFieldNames.Rbac.ADMIN_ID_STR
            this.userId = userId
        }.let { urRepo.save(it) }
    }
}
