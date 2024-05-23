package net.yan100.compose.depend.jvalid.repositories

import net.yan100.compose.depend.jvalid.entities.GetEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GetRepo : JpaRepository<GetEntity, String> {
}
