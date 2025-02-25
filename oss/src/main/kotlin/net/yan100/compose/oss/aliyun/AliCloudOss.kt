package net.yan100.compose.oss.aliyun

import com.aliyun.oss.OSS
import java.io.InputStream
import java.io.OutputStream
import java.util.function.Consumer
import net.yan100.compose.oss.FileArgs
import net.yan100.compose.oss.InMap
import net.yan100.compose.oss.Oss
import net.yan100.compose.oss.OutMap

/**
 * 阿里云 oss 实现
 *
 * @param oss 阿里云连接实例
 * @author TrueNine
 * @since 2023-02-21
 */
class AliCloudOss(val oss: OSS) : Oss {

  override fun getNative(): Any {
    return this.oss
  }

  override fun removeObject(fileInfo: FileArgs): Boolean {
    TODO("Not yet implemented")
  }

  override val exposedBaseUrl: String
    get() = TODO("Not yet implemented")

  override fun makeDirs(dirName: String) {
    TODO("Not yet implemented")
  }

  override fun existsDir(dirName: String): Boolean {
    TODO("Not yet implemented")
  }

  override fun removeFile(fileInfo: FileArgs): Boolean {
    TODO("Not yet implemented")
  }

  override fun publicDir(dir: String) {
    TODO("Not yet implemented")
  }

  override fun upload(stream: InputStream, fileInfo: FileArgs): InMap {
    TODO("Not yet implemented")
  }

  override fun upload(
    stream: InputStream,
    fileInfo: FileArgs,
    afterExec: Runnable,
  ): InMap {
    TODO("Not yet implemented")
  }

  override fun upload(
    stream: InputStream,
    fileArgs: FileArgs,
    afterExec: Consumer<FileArgs>,
  ): InMap {
    TODO("Not yet implemented")
  }

  override fun download(stream: OutputStream, fileInfo: FileArgs): OutMap {
    TODO("Not yet implemented")
  }

  override fun download(
    beforeExec: Runnable,
    stream: OutputStream,
    fileInfo: FileArgs,
  ): OutMap {
    TODO("Not yet implemented")
  }

  override fun download(
    beforeExec: Consumer<FileArgs>,
    stream: OutputStream,
    fileInfo: FileArgs,
  ): OutMap {
    TODO("Not yet implemented")
  }

  override fun listFiles(dir: String): List<String> {
    TODO("Not yet implemented")
  }

  override fun listDir(): List<String> {
    TODO("Not yet implemented")
  }
}
