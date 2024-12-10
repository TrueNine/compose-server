package net.yan100.compose.ksp.toolkit.kotlinpoet

import net.yan100.compose.ksp.toolkit.models.ClassDefine

object ClassNames {
  object Kotlin {
    object Jvm {
      val Transient = ClassDefine("kotlin.jvm", "Transient")
      val JvmStatic = ClassDefine("kotlin.jvm", "JvmStatic")
    }
  }

  object Net {
    object Yan100 {
      object Compose {
        object Meta {
          object Annotations {
            val MetaSkipGeneration = ClassDefine("net.yan100.compose.meta.annotations", "MetaSkipGeneration")
          }
        }

        object Rds {
          object Core {
            object Listeners {
              val BizCodeInsertListener = ClassDefine("net.yan100.compose.rds.core.listeners", "BizCodeInsertListener")
              val SnowflakeIdInsertListener = ClassDefine("net.yan100.compose.rds.core.listeners", "SnowflakeIdInsertListener")
            }

            object Entities {
              val IJpaPersistentEntity = ClassDefine("net.yan100.compose.rds.core.entities", "IJpaPersistentEntity")
            }
          }
        }
      }
    }
  }

  object Org {
    object Hibernate {
      val Hibernate = ClassDefine("org.hibernate", "Hibernate")

      object Annotations {
        val DynamicInsert = ClassDefine("org.hibernate.annotations", "DynamicInsert")
        val DynamicUpdate = ClassDefine("org.hibernate.annotations", "DynamicUpdate")
        val Immutable = ClassDefine("org.hibernate.annotations", "Immutable")
      }
    }
  }

  object Jakarta {
    object Persistence {
      val EntityListeners = ClassDefine("jakarta.persistence", "EntityListeners")
      val SecondaryTable = ClassDefine("jakarta.persistence", "SecondaryTable")
      val Access = ClassDefine("jakarta.persistence", "Access")
      val Basic = ClassDefine("jakarta.persistence", "Basic")
      val Table = ClassDefine("jakarta.persistence", "Table")
      val Entity = ClassDefine("jakarta.persistence", "Entity")
      val Column = ClassDefine("jakarta.persistence", "Column")
      val Transient = ClassDefine("jakarta.persistence", "Transient")
      val Id = ClassDefine("jakarta.persistence", "Id")
    }
  }

  object Com {
    object Fasterxml {
      object Jackson {
        object Annotation {
          val JsonIgnore = ClassDefine("com.fasterxml.jackson.annotation", "JsonIgnore")
        }
      }
    }
  }
}
