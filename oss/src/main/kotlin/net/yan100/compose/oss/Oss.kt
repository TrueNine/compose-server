package net.yan100.compose.oss

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.function.Consumer

/**
 * 对文件系统得抽象，使得编程接口统一化
 *
 * @author TrueNine
 */
interface Oss {
  /**
   * 获取一个实现对象，以便于应急使用，调用具体细节。
   * 如：使用 minio 实现，则实例为 MinioClient
   * 除非明确知晓类型，否则会类型转换错误
   *
   * @param instanceType 实例类型
   * @param <T>          类型
   * @return 实现对象实例
   * @implSpec MinioClientWrapper
  </T> */
  fun <T> `as`(instanceType: Class<T>): T

  /**
   * 创建新的文件夹
   *
   * @param dirName dir名字
   */
  fun makeDirs(dirName: String)

  /**
   * 创建公共文件夹
   *
   * @param dir dir
   */
  fun publicDir(dir: String)

  /**
   * 上传
   *
   * @param stream   流
   * @param fileInfo 文件信息
   * @return [InMap]
   */
  fun upload(stream: InputStream, fileInfo: FileArgs): InMap

  /**
   * 上传
   *
   * @param stream    流
   * @param fileInfo  文件信息
   * @param afterExec 后执行
   * @return [InMap]
   */
  fun upload(stream: InputStream, fileInfo: FileArgs, afterExec: Runnable): InMap

  /**
   * 上传
   *
   * @param stream    流
   * @param fileArgs  文件参数
   * @param afterExec 后执行
   * @return [InMap]
   */
  fun upload(stream: InputStream, fileArgs: FileArgs, afterExec: Consumer<FileArgs>): InMap

  /**
   * 下载
   *
   * @param stream   流
   * @param fileInfo 文件信息
   * @return [OutMap]
   * @throws IOException ioexception
   */
  @Throws(IOException::class)
  fun download(stream: OutputStream, fileInfo: FileArgs): OutMap

  /**
   * 下载
   *
   * @param beforeExec 在执行之前
   * @param stream     流
   * @param fileInfo   文件信息
   * @return [OutMap]
   * @throws IOException ioexception
   */
  @Throws(IOException::class)
  fun download(beforeExec: Runnable, stream: OutputStream, fileInfo: FileArgs): OutMap

  /**
   * 下载
   *
   * @param beforeExec 在执行之前
   * @param stream     流
   * @param fileInfo   文件信息
   * @return [OutMap]
   * @throws IOException ioexception
   */
  @Throws(IOException::class)
  fun download(beforeExec: Consumer<FileArgs>, stream: OutputStream, fileInfo: FileArgs): OutMap

  /**
   * 列表文件
   *
   * @param dir dir
   * @return [List]<[String]>
   */
  fun listFiles(dir: String): List<String>

  /**
   * dir列表
   *
   * @return [List]<[String]>
   */
  fun listDir(): List<String>
}