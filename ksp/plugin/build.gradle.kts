version = libs.versions.composeKspPlugin.get()

dependencies {
  compileOnly(libs.com.google.devtools.ksp.symbolProcessingApi)
  testImplementation(libs.com.google.devtools.ksp.symbolProcessingApi)

  implementation(libs.com.squareup.kotlinpoetJvm)
  implementation(libs.com.squareup.kotlinpoetKsp)

  api(project(":meta"))

  implementation(project(":core"))
  implementation(project(":ksp:ksp-toolkit"))

  testImplementation(libs.com.github.tschuchortdev.kotlinCompileTestingKsp) {
    //exclude("com.google.devtools.ksp")
    //exclude("org.jetbrains.kotlin")
  }
}

/*if (JavaVersion.current() >= JavaVersion.VERSION_16) {
  tasks.withType<Test>().all {
    jvmArgs(
      "--add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
      "--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
      "--add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED",
      "--add-opens=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
      "--add-opens=jdk.compiler/com.sun.tools.javac.jvm=ALL-UNNAMED",
      "--add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
      "--add-opens=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
      "--add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
      "--add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
      "--add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
    )
  }
}*/

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = project.group.toString()
      artifactId = project.name
      version = project.version.toString()
      from(components["java"])
    }
  }
}

signing {
  useGpgCmd()
  sign(publishing.publications["maven"])
}
