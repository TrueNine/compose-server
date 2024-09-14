package net.yan100.compose.rds.service

import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.entities.DbTestMergeTable
import net.yan100.compose.rds.repositories.IDbTestMergeTableRepo


interface IDbTestMergeTableService : ICrud<DbTestMergeTable>, IDbTestMergeTableRepo {
}
