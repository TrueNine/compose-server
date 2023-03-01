package io.tn.rds.repo

import io.tn.rds.vo.FileVo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FileVoRepo : JpaRepository<io.tn.rds.vo.FileVo, String> {
}
