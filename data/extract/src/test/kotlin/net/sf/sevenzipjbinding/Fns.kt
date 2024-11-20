package net.sf.sevenzipjbinding

import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream
import net.yan100.compose.core.hasText
import net.yan100.compose.core.nonText
import java.io.RandomAccessFile
import java.nio.file.Path


fun cleanFileName(path: String, sep: String = "$$"): String {
  return path.replace(":", sep)
    .replace("<", sep)
    .replace(">", sep)
    .replace("|", sep)
    .replace("?", sep)
    .replace("*", sep)
    .replace("/", sep)
    .replace(" ", sep)
    .replace("\"", sep)
    .replace("\\", sep)
    .replace("\r", sep)
    .replace("\n", sep)
    .replace("\t", sep)
}

fun isCompressFile(path: String): Boolean {
  return path.matches(".*\\.(rar|zip|7z)$".toRegex())
}

fun findCommonPrefix(strings: List<String>): String {
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

suspend fun getArchiveItems(
  path: Path,
  resolver: suspend (items: List<ArchivePackageFileInfo>) -> List<ArchivePackageFileInfo>
): List<ArchivePackageFileInfo> {
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
      ArchivePackageFileInfo(
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
