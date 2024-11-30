package net.yan100.compose.ksp.toolkit.kotlinpoet

import com.squareup.kotlinpoet.ClassName

object ClassNames {
  object Org {
    object Hibernate {
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
