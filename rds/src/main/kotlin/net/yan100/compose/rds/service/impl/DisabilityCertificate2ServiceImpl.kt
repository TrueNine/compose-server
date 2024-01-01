package net.yan100.compose.rds.service.impl

import net.yan100.compose.rds.entities.cert.DisCert2
import net.yan100.compose.rds.repositories.cert.IDisCert2Repo
import net.yan100.compose.rds.service.base.CrudService
import org.springframework.stereotype.Service

@Service
class DisabilityCertificate2ServiceImpl(
  val repo: IDisCert2Repo
) : CrudService<DisCert2>(repo)
