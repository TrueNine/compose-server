package io.tn.oss.aliyun

import com.aliyun.oss.OSS
import io.tn.oss.FileArgs
import io.tn.oss.abstracts.InMap
import io.tn.oss.abstracts.Oss
import io.tn.oss.abstracts.OutMap
import java.io.InputStream
import java.io.OutputStream
import java.util.function.Consumer

/**
 * 阿里云 oss 实现
 *
 * @author TrueNine
 * @since 2023-02-21
 * @param oss 阿里云连接实例
 */
class AliCloudOss(
  val oss: OSS
) : Oss {

  @Suppress("UNCHECKED_CAST")
  override fun <T : Any?> `as`(instanceType: Class<T>?): T {
    return this.oss as T
  }

  override fun makeDirs(dirName: String?) {
    TODO("Not yet implemented")
  }

  override fun publicDir(dir: String?) {
    TODO("Not yet implemented")
  }

  override fun upload(stream: InputStream?, fileInfo: FileArgs?): InMap {
    TODO("Not yet implemented")
  }

  override fun upload(
    stream: InputStream?,
    fileInfo: FileArgs?,
    afterExec: Runnable?
  ): InMap {
    TODO("Not yet implemented")
  }

  override fun upload(
    stream: InputStream?,
    fileArgs: FileArgs?,
    afterExec: Consumer<FileArgs>?
  ): InMap {
    TODO("Not yet implemented")
  }

  override fun download(stream: OutputStream?, fileInfo: FileArgs?): OutMap {
    TODO("Not yet implemented")
  }

  override fun download(
    beforeExec: Runnable?,
    stream: OutputStream?,
    fileInfo: FileArgs?
  ): OutMap {
    TODO("Not yet implemented")
  }

  override fun download(
    beforeExec: Consumer<FileArgs>?,
    stream: OutputStream?,
    fileInfo: FileArgs?
  ): OutMap {
    TODO("Not yet implemented")
  }

  override fun listFiles(dir: String?): MutableList<String> {
    TODO("Not yet implemented")
  }

  override fun listDir(): MutableList<String> {
    TODO("Not yet implemented")
  }
}
