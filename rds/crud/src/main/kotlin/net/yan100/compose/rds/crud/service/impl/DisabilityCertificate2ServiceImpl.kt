package net.yan100.compose.rds.crud.service.impl

import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.core.jpa
import net.yan100.compose.rds.crud.entities.jpa.DisCert2
import net.yan100.compose.rds.crud.repositories.jpa.IDisCert2Repo
import org.springframework.stereotype.Service

@Service
class DisabilityCertificate2ServiceImpl(val dRepo: IDisCert2Repo) :
  ICrud<DisCert2> by jpa(dRepo)
