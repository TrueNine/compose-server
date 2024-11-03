package net.yan100.compose.core

import java.nio.file.FileSystems

val systemSeparator: String = FileSystems.getDefault().separator
val userDir: String = System.getProperty("user.dir")
