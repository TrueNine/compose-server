plugins {
  id("buildlogic.kotlin-conventions")
}

description = """
Kotlin Symbol Processing (KSP) plugin for compile-time code generation and analysis.
Provides annotation processing capabilities with KotlinPoet integration for generating type-safe code.
""".trimIndent()

dependencies {
  compileOnly(libs.com.google.devtools.ksp.symbol.processing.api)
  testImplementation(libs.com.google.devtools.ksp.symbol.processing.api)

  implementation(libs.com.squareup.kotlinpoet.jvm)
  implementation(libs.com.squareup.kotlinpoet.ksp)

  api(projects.meta)

  implementation(projects.ksp.kspShared)

  testImplementation(libs.com.github.tschuchortdev.kotlin.compile.testing.ksp)
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
