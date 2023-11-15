package net.yan100.compose.rds.service.impl

import net.yan100.compose.rds.entities.documents.DisabilityCertificate2
import net.yan100.compose.rds.repositories.documents.IDisabilityCertificate2Repo
import net.yan100.compose.rds.service.base.CrudService
import org.springframework.stereotype.Service

@Service
class DisabilityCertificate2ServiceImpl(
  val repo: IDisabilityCertificate2Repo
) : CrudService<DisabilityCertificate2>(repo)
