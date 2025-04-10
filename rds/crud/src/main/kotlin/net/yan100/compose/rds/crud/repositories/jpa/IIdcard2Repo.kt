package net.yan100.compose.rds.crud.repositories.jpa

import net.yan100.compose.rds.IRepo
import net.yan100.compose.rds.crud.entities.jpa.Idcard2
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Primary
@Repository("IIdcard2Repository")
@Deprecated("弃用 JPA")
interface IIdcard2Repo : IRepo<Idcard2>
