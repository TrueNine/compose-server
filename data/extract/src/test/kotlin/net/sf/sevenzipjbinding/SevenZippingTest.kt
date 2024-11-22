package net.sf.sevenzipjbinding

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import net.yan100.compose.core.hasText
import net.yan100.compose.core.isFile
import net.yan100.compose.testtookit.log
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.createDirectories
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test


class SevenZippingTest {
  private val rootFile = "D:/文件"
  private val destFile = "D:/文件Dest"
  private val rootPath = Paths.get(rootFile)
  private val destPath = Paths.get(destFile)

  private val onDeletePaths: MutableList<Path> = mutableListOf()

  private suspend fun dispatch(path: Path) {
    val rootFileName = path.fileName.toString().substringBeforeLast(".")
    val allPath = cleanFileName(rootPath.relativize(path).toString()).substringBeforeLast(".")
    getArchiveItems(path) { items ->
      for (item in items) {
        val isCompress = isCompressFile(item.fileName)
        val destFolder = (if (isCompress) rootPath.resolve("__zipping__") else destPath).resolve(cleanFileName(allPath + rootFileName))
        val destDir = item.parentPath?.let {
          if (it.hasText()) destFolder.parent.resolve(cleanFileName(destFolder.fileName.toString() + "$$" + it))
          else null
        } ?: destFolder
        destDir.createDirectories()
        val destFile = destDir.resolve(item.fileName)
        if (item.isFolder) continue
        Files.newOutputStream(destFile).use {
          print("write=")
          item.handle.extractSlow({ data ->
            it.write(data)
            print("=")
            data.size
          }, null)
          println()
          log.info("extracted item: {}", item)
        }
      }
      onDeletePaths.add(path)
      items
    }
  }

  @Ignore
  @Test
  fun `get rar file`() {
    val requireProcessPaths = mutableListOf<Path>()
    var size = 0
    Files.walk(rootPath).forEach { path ->
      if (!path.isFile()) return@forEach
      size += 1
      if (isCompressFile(path.fileName.toString())) {
        requireProcessPaths.add(path)
      }
    }

    runBlocking {
      val tasks = mutableListOf<Deferred<*>>()
      for (path in requireProcessPaths) {
        log.info("add task path: {}", path)
        val task = async {
          log.info("launch task path: {}", path)
          dispatch(path)
        }
        tasks.add(task)
      }

      tasks.joinAll()

      /*onDeletePaths.forEach {
          println("delete ${it.absolutePathString()}")
          File(it.absolutePathString()).deleteOnExit()
        }*/
      println("files size $size")
    }
  }

  @BeforeTest
  fun `open archive`() {
    SevenZip.initSevenZipFromPlatformJAR()
  }
}
