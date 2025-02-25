package net.yan100.compose.rds.crud.service.impl

import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.core.jpa
import net.yan100.compose.rds.crud.entities.jpa.Idcard2
import net.yan100.compose.rds.crud.repositories.jpa.IIdcard2Repo
import org.springframework.stereotype.Service

@Service
class Idcard2ServiceImpl(val iRepo: IIdcard2Repo) :
  ICrud<Idcard2> by jpa(iRepo)
