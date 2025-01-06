plugins {
  `kotlin-convention`
}

version = libs.versions.composeKspPlugin.get()

dependencies {
  compileOnly(libs.com.google.devtools.ksp.symbolProcessingApi)
  testImplementation(libs.com.google.devtools.ksp.symbolProcessingApi)

  implementation(libs.com.squareup.kotlinpoetJvm)
  implementation(libs.com.squareup.kotlinpoetKsp)

  api(projects.meta)

  implementation(projects.core)
  implementation(projects.ksp.kspToolkit)

  testImplementation(libs.com.github.tschuchortdev.kotlinCompileTestingKsp)
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
