package com.truenine.component.rds.service.impl

import com.truenine.component.rds.base.BaseServiceImpl
import com.truenine.component.rds.entity.UserInfoEntity
import com.truenine.component.rds.repository.UserInfoRepository
import com.truenine.component.rds.service.UserInfoService
import org.springframework.stereotype.Service

@Service
class UserInfoServiceImpl(repo: UserInfoRepository) : UserInfoService, BaseServiceImpl<UserInfoEntity>(repo) {
}
