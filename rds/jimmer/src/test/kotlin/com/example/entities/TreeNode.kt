package com.example.entities

import org.babyfish.jimmer.jackson.JsonConverter
import org.babyfish.jimmer.jackson.LongToStringConverter
import org.babyfish.jimmer.sql.*

@Entity
interface TreeNode {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonConverter(LongToStringConverter::class)
  @Column(name = "TREE_NODE")
  val id: Long

  val name: String

  @ManyToOne
  val parent: TreeNode?

  @OneToMany(mappedBy = "parent")
  val childNodes: List<TreeNode>
}
