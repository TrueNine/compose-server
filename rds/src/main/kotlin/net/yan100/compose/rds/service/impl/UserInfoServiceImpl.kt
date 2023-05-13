package net.yan100.compose.rds.service.impl

import net.yan100.compose.rds.base.BaseServiceImpl
import net.yan100.compose.rds.entity.UserInfoEntity
import net.yan100.compose.rds.repository.UserInfoRepository
import net.yan100.compose.rds.service.UserInfoService
import org.springframework.stereotype.Service

@Service
class UserInfoServiceImpl(repo: UserInfoRepository) : UserInfoService, BaseServiceImpl<UserInfoEntity>(repo)
