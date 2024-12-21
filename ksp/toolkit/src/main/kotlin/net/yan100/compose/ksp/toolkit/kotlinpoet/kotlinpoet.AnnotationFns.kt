package net.yan100.compose.ksp.toolkit.kotlinpoet

import net.yan100.compose.ksp.toolkit.models.ClassDefine

object Libs {
  object kotlin {
    object jvm {
      val JvmField = ClassDefine("kotlin.jvm", "JvmField")
      val JvmName = ClassDefine("kotlin.jvm", "JvmName")
      val JvmSynthetic = ClassDefine("kotlin.jvm", "JvmSynthetic")
      val JvmOverloads = ClassDefine("kotlin.jvm", "JvmOverloads")
      val Transient = ClassDefine("kotlin.jvm", "Transient")
      val JvmStatic = ClassDefine("kotlin.jvm", "JvmStatic")
    }
  }

  object net {
    object yan100 {
      object compose {
        object meta {
          object annotations {
            object client {
              val Api = ClassDefine("net.yan100.compose.meta.annotations.client", "Api")
              val ApiIgnore = ClassDefine("net.yan100.compose.meta.annotations.client", "ApiIgnore")
              val ApiGeneratingAll = ClassDefine("net.yan100.compose.meta.annotations.client", "ApiGeneratingAll")
            }

            val MetaSkipGeneration = ClassDefine("net.yan100.compose.meta.annotations", "MetaSkipGeneration")
          }
        }

        object rds {
          object core {
            object listeners {
              val BizCodeInsertListener = ClassDefine("net.yan100.compose.rds.core.listeners", "BizCodeInsertListener")
              val SnowflakeIdInsertListener = ClassDefine("net.yan100.compose.rds.core.listeners", "SnowflakeIdInsertListener")
            }

            object entities {
              val IJpaPersistentEntity = ClassDefine("net.yan100.compose.rds.core.entities", "IJpaPersistentEntity")
            }
          }
        }
      }
    }
  }

  object org {
    object babyfish {
      object jimmer {
        object sql {
          val Entity = ClassDefine("org.babyfish.jimmer.sql", "Entity")
          val Embeddable = ClassDefine("org.babyfish.jimmer.sql", "Embeddable")
        }
      }
    }

    object springframework {
      object web {
        object bind {
          object annotations {
            val RequestMapping = ClassDefine("org.springframework.web.bind.annotation", "RequestMapping")
            val Controller = ClassDefine("org.springframework.web.bind.annotation", "Controller")
            val RestController = ClassDefine("org.springframework.web.bind.annotation", "RestController")
            val GetMapping = ClassDefine("org.springframework.web.bind.annotation", "GetMapping")
            val PostMapping = ClassDefine("org.springframework.web.bind.annotation", "PostMapping")
            val PutMapping = ClassDefine("org.springframework.web.bind.annotation", "PutMapping")
            val DeleteMapping = ClassDefine("org.springframework.web.bind.annotation", "DeleteMapping")
            val PatchMapping = ClassDefine("org.springframework.web.bind.annotation", "PatchMapping")
            val RequestBody = ClassDefine("org.springframework.web.bind.annotation", "RequestBody")
            val RequestParam = ClassDefine("org.springframework.web.bind.annotation", "RequestParam")
            val PathVariable = ClassDefine("org.springframework.web.bind.annotation", "PathVariable")
            val RequestHeader = ClassDefine("org.springframework.web.bind.annotation", "RequestHeader")
            val RequestPart = ClassDefine("org.springframework.web.bind.annotation", "RequestPart")
          }
        }
      }
    }

    object hibernate {
      val Hibernate = ClassDefine("org.hibernate", "Hibernate")

      object annotations {
        val DynamicInsert = ClassDefine("org.hibernate.annotations", "DynamicInsert")
        val DynamicUpdate = ClassDefine("org.hibernate.annotations", "DynamicUpdate")
        val Immutable = ClassDefine("org.hibernate.annotations", "Immutable")
      }
    }
  }

  object jakarta {
    object persistence {
      val EntityListeners = ClassDefine("jakarta.persistence", "EntityListeners")
      val SecondaryTable = ClassDefine("jakarta.persistence", "SecondaryTable")
      val Access = ClassDefine("jakarta.persistence", "Access")
      val Basic = ClassDefine("jakarta.persistence", "Basic")
      val Table = ClassDefine("jakarta.persistence", "Table")
      val Entity = ClassDefine("jakarta.persistence", "Entity")
      val Column = ClassDefine("jakarta.persistence", "Column")
      val JoinColumn = ClassDefine("jakarta.persistence", "JoinColumn")
      val JoinTable = ClassDefine("jakarta.persistence", "JoinTable")
      val ManyToOne = ClassDefine("jakarta.persistence", "ManyToOne")
      val OneToMany = ClassDefine("jakarta.persistence", "OneToMany")
      val ManyToMany = ClassDefine("jakarta.persistence", "ManyToMany")
      val OneToOne = ClassDefine("jakarta.persistence", "OneToOne")
      val ElementCollection = ClassDefine("jakarta.persistence", "ElementCollection")
      val Transient = ClassDefine("jakarta.persistence", "Transient")
      val Id = ClassDefine("jakarta.persistence", "Id")
    }
  }

  object java {
    object lang {
      val Long = ClassDefine("java.lang", "Long")
    }
  }

  object com {
    object fasterxml {
      object jackson {
        object annotation {
          val JsonIgnore = ClassDefine("com.fasterxml.jackson.annotation", "JsonIgnore")
        }
      }
    }
  }
}
