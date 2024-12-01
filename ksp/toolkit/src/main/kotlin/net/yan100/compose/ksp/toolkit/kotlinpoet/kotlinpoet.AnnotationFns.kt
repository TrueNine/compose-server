package net.yan100.compose.ksp.toolkit.kotlinpoet

import com.squareup.kotlinpoet.ClassName

object ClassNames {
  object Kotlin {
    object Jvm {
      val Transient = ClassName("kotlin.jvm", "Transient")
      val JvmStatic = ClassName("kotlin.jvm", "JvmStatic")
    }
  }

  object Net {
    object Yan100 {
      object Compose {
        object Meta {
          object Annotations {
            val MetaSkipGeneration = ClassName("net.yan100.compose.meta.annotations", "MetaSkipGeneration")
          }
        }

        object Rds {
          object Core {
            object Entities {
              val IAnyEntity = ClassName("net.yan100.compose.rds.core.entities", "IAnyEntity")
            }
          }
        }
      }
    }
  }

  object Org {
    object Hibernate {
      val Hibernate = ClassName("org.hibernate", "Hibernate")

      object Annotations {
        val DynamicInsert = ClassName("org.hibernate.annotations", "DynamicInsert")
        val DynamicUpdate = ClassName("org.hibernate.annotations", "DynamicUpdate")
        val Immutable = ClassName("org.hibernate.annotations", "Immutable")
      }
    }
  }

  object Jakarta {
    object Persistence {
      val SecondaryTable = ClassName("jakarta.persistence", "SecondaryTable")
      val Access = ClassName("jakarta.persistence", "Access")
      val Table = ClassName("jakarta.persistence", "Table")
      val Entity = ClassName("jakarta.persistence", "Entity")
      val Column = ClassName("jakarta.persistence", "Column")
      val Transient = ClassName("jakarta.persistence", "Transient")
      val Id = ClassName("jakarta.persistence", "Id")
    }
  }

  object Com {
    object Fasterxml {
      object Jackson {
        object Annotation {
          val JsonIgnore = ClassName("com.fasterxml.jackson.annotation", "JsonIgnore")
        }
      }
    }
  }
}
