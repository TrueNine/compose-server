package com.example.entities

import net.yan100.compose.rds.jimmer.generators.JimmerSnowflakeLongIdGenerator
import org.babyfish.jimmer.jackson.JsonConverter
import org.babyfish.jimmer.jackson.LongToStringConverter
import org.babyfish.jimmer.sql.*

@Entity
interface TreeNode {
  @Id
  @GeneratedValue(generatorRef = JimmerSnowflakeLongIdGenerator.JIMMER_SNOWFLAKE_LONG_ID_GENERATOR_NAME)
  @JsonConverter(LongToStringConverter::class)
  @Column(name = "TREE_NODE")
  val id: Long

  val name: String

  @ManyToOne
  val parent: TreeNode?

  @OneToMany(mappedBy = "parent")
  val childNodes: List<TreeNode>
}
