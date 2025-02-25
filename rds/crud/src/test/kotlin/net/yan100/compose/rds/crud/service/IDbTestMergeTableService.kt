package net.yan100.compose.rds.crud.service

import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.crud.entities.DbTestMergeTable
import net.yan100.compose.rds.crud.repositories.jpa.IDbTestMergeTableRepo

interface IDbTestMergeTableService :
  ICrud<DbTestMergeTable>, IDbTestMergeTableRepo
