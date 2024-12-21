package net.yan100.compose.client.jimmer

import org.babyfish.jimmer.sql.Embeddable

@Embeddable
interface FullName {
  val firstName: String
  val lastName: String
}
