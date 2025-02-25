package net.yan100.compose.oss.minio

import io.minio.MinioClient
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.function.Consumer
import net.yan100.compose.oss.FileArgs
import net.yan100.compose.oss.InMap
import net.yan100.compose.oss.Oss
import net.yan100.compose.oss.OutMap

/**
 * oss 的 minio 实现
 *
 * @author TrueNine
 * @since 2023-02-20
 */
class MinioClientWrapper(
  private val minioClient: MinioClient,
  private val exposeUrl: String = "http://localhost:9000",
) : Oss, MinioClientAdaptor(minioClient, exposeUrl) {

  override fun getNative(): Any {
    return super.client
  }

  override val exposedBaseUrl: String
    get() = this.exposeUrl

  override fun makeDirs(dirName: String) {
    createBucket(dirName)
  }

  override fun existsDir(dirName: String): Boolean {
    return bucketExists(dirName)
  }

  override fun removeFile(fileInfo: FileArgs): Boolean {
    return removeObject(fileInfo)
  }

  override fun publicDir(dir: String) {
    publicBucket(dir)
  }

  override fun upload(stream: InputStream, fileInfo: FileArgs): InMap {
    val ins = putObject(fileInfo, stream)
    return ins(ins!!, stream)
  }

  override fun upload(
    stream: InputStream,
    fileInfo: FileArgs,
    afterExec: Runnable,
  ): InMap {
    val ins = upload(stream, fileInfo)
    afterExec.run()
    return ins
  }

  override fun upload(
    stream: InputStream,
    fileArgs: FileArgs,
    afterExec: Consumer<FileArgs>,
  ): InMap {
    val ins = upload(stream, fileArgs)
    afterExec.accept(FileArgs.useStreamMap(ins))
    return ins
  }

  @Throws(IOException::class)
  override fun download(stream: OutputStream, fileInfo: FileArgs): OutMap {
    val outs = getObject(fileInfo, stream)
    outs?.transferTo(stream)
    return outs(outs!!, stream)
  }

  @Throws(IOException::class)
  override fun download(
    beforeExec: Runnable,
    stream: OutputStream,
    fileInfo: FileArgs,
  ): OutMap {
    val outs = getObject(fileInfo, stream)
    beforeExec.run()
    outs?.transferTo(stream)
    return download(stream, fileInfo)
  }

  @Throws(IOException::class)
  override fun download(
    beforeExec: Consumer<FileArgs>,
    stream: OutputStream,
    fileInfo: FileArgs,
  ): OutMap {
    val outs = getObject(fileInfo, stream)
    val wrapper = outs(outs!!, stream)
    beforeExec.accept(FileArgs.useStreamMap(wrapper))
    outs.transferTo(stream)
    return outs(outs, stream)
  }
}
