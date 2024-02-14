package net.yan100.compose.rds.base

import net.yan100.compose.rds.entities.relationship.RolePermissions
import net.yan100.compose.rds.repositories.relationship.IRolePermissionsRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest
class AnyEntityTest {
    @Autowired
    private lateinit var repo: IRolePermissionsRepo

    @Test
    fun `test save and update`() {
        val empty = RolePermissions().also {
            it.roleId = "33"
            it.permissionsId = "44"
        }
        assertTrue { empty.isNew }
        assertFalse {
            empty.let {
                it.id = "3344"
                it
            }.isNew
        }
        val b = repo.save(empty)
        assertFalse { b.isNew }
        assertTrue { b.id != null }
        val c = repo.save(b.let {
            it.roleId = "4455"
            it
        })
        repo.save(c)

    }
}
