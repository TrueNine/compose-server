package io.github.truenine.composeserver

import java.nio.file.FileSystems

val systemSeparator: String = FileSystems.getDefault().separator
val userDir: String = System.getProperty("user.dir")
