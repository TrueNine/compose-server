package net.yan100.compose.ksp.dsl

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import java.time.LocalDate

class KDocSpecScope(override val fileBuilder: FileSpec.Builder) :
  StandardBuilderAdaptor<CodeBlock.Builder, CodeBlock> {
  private val cb: CodeBlock.Builder = CodeBlock.builder()

  private fun addTitleFormat(title: String, count: Int = 1, vararg args: Any) {
    for (i in 1..count) cb.add("#")
    cb.add(" $title", args)
  }

  fun desc(doc: String, vararg args: Any) {
    if (cb.isEmpty()) cb.add(doc, args) else cb.add("\n$doc", args)
  }

  fun h1(title: String, vararg args: Any) = addTitleFormat(title, 1, args)

  fun h2(title: String, vararg args: Any) = addTitleFormat(title, 2, args)

  fun h3(title: String, vararg args: Any) = addTitleFormat(title, 3, args)

  fun h4(title: String, vararg args: Any) = addTitleFormat(title, 4, args)

  fun h5(title: String, vararg args: Any) = addTitleFormat(title, 5, args)

  fun h6(title: String, vararg args: Any) = addTitleFormat(title, 6, args)

  fun param(name: String, desc: String = name) = cb.add("@param $name $desc\n")

  fun returnType(desc: String = "") = cb.add("@return $desc\n")

  fun see(desc: String = "") = cb.add("@see $desc\n")

  fun since(version: String = LocalDate.now().toString()) =
    cb.add("@since $version\n")

  fun throws(type: String, desc: String = "") = cb.add("@throws $type $desc\n")

  fun throws(type: Throwable, desc: String = "") =
    cb.add("@throws ${type::class.qualifiedName} $desc\n")

  override fun build(): CodeBlock = cb.build()

  override val builder: CodeBlock.Builder
    get() = cb
}
