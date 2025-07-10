package io.github.truenine.composeserver.testtoolkit

import org.junit.jupiter.api.io.TempDir
import org.springframework.test.annotation.Rollback

typealias SysLogger = org.slf4j.Logger

typealias RDBRollback = Rollback

/** 临时目录映射 */
typealias TempDirMapping = TempDir
