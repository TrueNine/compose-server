package io.github.truenine.composeserver.consts

import java.util.*

/**
 * 数据库基础字段
 *
 * @author TrueNine
 * @since 2022-12-04
 */
interface IDbNames {
  interface Tenant {
    companion object {
      const val ROOT_TENANT: Long = Rbac.ROOT_ID
      const val ROOT_TENANT_STR: String = ROOT_TENANT.toString()
      const val DEFAULT_TENANT: Long = Rbac.ROOT_ID
      const val DEFAULT_TENANT_STR: String = DEFAULT_TENANT.toString()
    }
  }

  interface Rbac {
    companion object {
      const val ROOT_ID: Long = 0L
      const val ROOT_ID_STR: String = ROOT_ID.toString()
      const val USER_ID: Long = 1L
      const val USER_ID_STR: String = USER_ID.toString()
      const val ADMIN_ID: Long = 2L
      const val ADMIN_ID_STR: String = ADMIN_ID.toString()
      const val VIP_ID: Long = 3L
      const val VIP_ID_STR: String = VIP_ID.toString()
    }
  }

  companion object {
    val all: List<String>
      get() {
        val al =
          arrayOf(
            ID,
            ROW_LOCK_VERSION,
            CREATE_ROW_DATETIME,
            MODIFY_ROW_DATETIME,
            TREE_LEFT_NODE,
            TREE_NODE_LEVEL,
            TREE_GROUP_ID,
            TREE_RIGHT_NODE,
            ROW_PARENT_ID,
            LOGIC_DELETE_FLAG,
            ANY_REFERENCE_ID,
            ANY_REFERENCE_TYPE,
            TENANT_ID,
          )
        return Arrays.asList(*al)
      }

    const val ID: String = "id"
    const val CREATE_ROW_DATETIME: String = "crd"
    const val MODIFY_ROW_DATETIME: String = "mrd"
    const val LOGIC_DELETE_FLAG: String = "ldf"
    const val TREE_LEFT_NODE: String = "rln"
    const val TREE_RIGHT_NODE: String = "rrn"
    const val TREE_NODE_LEVEL: String = "nlv"
    const val TREE_GROUP_ID: String = "tgi"
    const val ROW_LOCK_VERSION: String = "rlv"
    const val ROW_PARENT_ID: String = "rpi"
    const val ANY_REFERENCE_ID: String = "ari"
    const val ANY_REFERENCE_TYPE: String = "typ"
    const val TENANT_ID: String = "rti"
  }
}
