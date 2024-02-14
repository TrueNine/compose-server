package net.yan100.compose.rds.service.impl

import net.yan100.compose.rds.entities.cert.Idcard2
import net.yan100.compose.rds.repositories.cert.IIdcard2Repo
import net.yan100.compose.rds.service.base.CrudService
import org.springframework.stereotype.Service

@Service
class Idcard2ServiceImpl(
    val repo: IIdcard2Repo
) : CrudService<Idcard2>(repo)
