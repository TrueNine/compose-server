package net.sf.sevenzipjbinding

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem
import net.yan100.compose.core.hasText
import net.yan100.compose.core.isFile
import net.yan100.compose.core.nonText
import net.yan100.compose.testtookit.log
import java.io.RandomAccessFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.createDirectories
import kotlin.test.BeforeTest
import kotlin.test.Test

data class Info(
  val fileName: String,
  val parentPath: String?,
  val deep: Int,
  val path: String,
  val handle: ISimpleInArchiveItem, val isFolder: Boolean
)

fun cleanFileName(path: String, sep: String = "$$"): String {
  return path.replace(":", sep)
    .replace("<", sep)
    .replace(">", sep)
    .replace("|", sep)
    .replace("?", sep)
    .replace("*", sep)
    .replace("\"", sep)
    .replace("/", sep)
    .replace("\\", sep)
    .replace(" ", sep)
    .replace("\r", sep)
    .replace("\n", sep)
    .replace("\t", sep)
}


class SevenZippingTest {
  private val rootFile = "C:/文件"
  private val destFile = "C:/文件Dest"
  private val rootPath = Paths.get(rootFile)
  private val destPath = Paths.get(destFile)

  private fun isCompressFile(path: String): Boolean {
    return path.matches(".*\\.(rar|zip|7z)$".toRegex())
  }

  private fun findCommonPrefix(strings: List<String>): String {
    if (strings.isEmpty()) return ""
    var prefix = strings[0]
    for (string in strings) {
      while (string.indexOf(prefix) != 0) {
        prefix = prefix.dropLast(1)
        if (prefix.isEmpty()) return ""
      }
    }
    return prefix
  }

  private suspend fun getArchiveItems(path: Path, resolver: suspend (items: List<Info>) -> List<Info>): List<Info> {
    val f = RandomAccessFile(path.toFile(), "r")
    val byteArrIn = RandomAccessFileInStream(f)
    val archive = SevenZip.openInArchive(
      null, byteArrIn
    )
    if (null == archive) return emptyList()

    return archive.use { a ->
      val result = a.simpleInterface.archiveItems.map {
        val name = it.path.split("[/\\\\]".toRegex()).filter { s -> s.hasText() }.let { ss ->
          if (ss.size == 1) ss
          else ss.drop(1)
        }.joinToString("/")
        val deep = name.split("/").filter { s -> s.hasText() }.map { s -> cleanFileName(s) }
        val handle = it
        val isFolder = it.isFolder
        Info(
          fileName = if (deep.isEmpty()) cleanFileName(handle.path) else deep.last(),
          path = name,
          parentPath = if (deep.isEmpty()) null else deep.dropLast(1).joinToString("/"),
          deep = deep.size,
          handle = handle,
          isFolder = isFolder
        )
      }.filterNot {
        it.deep == 0 && it.path.nonText() && it.isFolder
      }.filterNot {
        it.path.matches(".*\\.(mp3|mp4|xls|xlsx|txt|pptx|doc|docx)$".toRegex())
      }.filterNot { it.isFolder }
      val r = if (result.isEmpty()) emptyList() else resolver(result)
      byteArrIn.close()
      r
    }
  }

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
