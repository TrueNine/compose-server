plugins { `kotlinspring-convention` }

version = libs.versions.compose.ai.get()

dependencies {
  api(projects.mcp.mcpShared)
  implementation(projects.shared)
  testImplementation(projects.testtoolkit)
}
