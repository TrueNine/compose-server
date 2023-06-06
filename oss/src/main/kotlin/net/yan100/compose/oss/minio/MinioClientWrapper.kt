package net.yan100.compose.oss.minio

import io.minio.MinioClient
import net.yan100.compose.oss.FileArgs
import net.yan100.compose.oss.InMap
import net.yan100.compose.oss.Oss
import net.yan100.compose.oss.OutMap
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.function.Consumer

/**
 * oss 的 minio 实现
 *
 * @author TrueNine
 * @since 2023-02-20
 */
class MinioClientWrapper(
  private val minioClient: MinioClient
) : Oss, MinioClientAdaptor(minioClient) {

  @Suppress("UNCHECKED_CAST")
  override fun <T> `as`(instanceType: Class<T>): T {
    return super.client as T
  }

  override fun makeDirs(dirName: String) {
    createBucket(dirName)
  }

  override fun publicDir(dir: String) {
    publicBucket(dir)
  }

  override fun upload(stream: InputStream, fileInfo: FileArgs): InMap {
    val ins = putObject(fileInfo, stream)
    return ins(ins, stream)
  }

  override fun upload(stream: InputStream, fileInfo: FileArgs, afterExec: Runnable): InMap {
    val ins = upload(stream, fileInfo)
    afterExec.run()
    return ins
  }

  override fun upload(stream: InputStream, fileArgs: FileArgs, afterExec: Consumer<FileArgs>): InMap {
    val ins = upload(stream, fileArgs)
    afterExec.accept(FileArgs.useStreamMap(ins))
    return ins
  }

  @Throws(IOException::class)
  override fun download(stream: OutputStream, fileInfo: FileArgs): OutMap {
    val outs = getObject(fileInfo, stream)
    outs.transferTo(stream)
    return outs(outs, stream)
  }

  @Throws(IOException::class)
  override fun download(beforeExec: Runnable, stream: OutputStream, fileInfo: FileArgs): OutMap {
    val outs = getObject(fileInfo, stream)
    beforeExec.run()
    outs.transferTo(stream)
    return download(stream, fileInfo)
  }

  @Throws(IOException::class)
  override fun download(beforeExec: Consumer<FileArgs>, stream: OutputStream, fileInfo: FileArgs): OutMap {
    val outs = getObject(fileInfo, stream)
    val wrapper = outs(outs, stream)
    beforeExec.accept(FileArgs.useStreamMap(wrapper))
    outs.transferTo(stream)
    return outs(outs, stream)
  }


}
