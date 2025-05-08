plugins { `kotlinspring-convention` }

version = libs.versions.compose.ai.get()

dependencies {
  implementation(platform(libs.dev.langchain4j.langchain4j.bom))

  api(projects.mcp.mcpShared)
  implementation(projects.shared)
  testImplementation(projects.testtoolkit)
}
