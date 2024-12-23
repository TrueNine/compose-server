package net.yan100.compose.client.templates

object InterfaceTemplate {
  fun renderInterface() {
    buildString {
      append("import ")
      appendLine("from 'xxx'")
      appendLine()
      append("export ")
      append("interface ")
      append("name {")
      append("}")
    }
  }
}
