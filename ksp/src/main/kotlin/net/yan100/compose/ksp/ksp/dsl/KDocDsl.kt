package net.yan100.compose.ksp.ksp.dsl

import com.squareup.kotlinpoet.CodeBlock

class KDocDsl() : StandardBuilderAdaptor<CodeBlock.Builder, CodeBlock> {
  private val cb: CodeBlock.Builder = CodeBlock.builder()

  private fun addTitleFormat(title: String, count: Int = 1, vararg args: Any) {
    for (i in 1..count) cb.add("#")
    cb.add(" $title", args)
  }

  fun p(doc: String, vararg args: Any) {
    if (cb.isEmpty()) cb.add(doc, args)
    else cb.add("\n$doc", args)
  }

  fun h1(title: String, vararg args: Any) = addTitleFormat(title, 1, args)
  fun h2(title: String, vararg args: Any) = addTitleFormat(title, 2, args)
  fun h3(title: String, vararg args: Any) = addTitleFormat(title, 3, args)
  fun h4(title: String, vararg args: Any) = addTitleFormat(title, 4, args)
  fun h5(title: String, vararg args: Any) = addTitleFormat(title, 5, args)
  fun h6(title: String, vararg args: Any) = addTitleFormat(title, 6, args)

  override fun build(): CodeBlock = cb.build()
  override val builder: CodeBlock.Builder get() = cb
}
