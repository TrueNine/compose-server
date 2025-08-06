plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
LangChain4J integration for advanced AI capabilities and language model interactions.
Provides comprehensive AI model integration, prompt engineering, and intelligent processing workflows.
"""
    .trimIndent()

dependencies {
  implementation(platform(libs.dev.langchain4j.langchain4j.bom))
  implementation(platform(libs.dev.langchain4j.langchain4j.community.bom))

  api(projects.ai.aiShared)
  implementation(projects.shared)
  testImplementation(projects.testtoolkit)
}
