/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.oss

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.function.Consumer

/**
 * ## 对文件系统或对象存储系统的抽象，使得编程接口统一化
 *
 * @author TrueNine
 */
interface Oss {
  /**
   * 获取一个实现对象，以便于应急使用，调用具体细节。 如：使用 minio 实现，则实例为 MinioClient 除非明确知晓类型，否则会类型转换错误
   *
   * @param instanceType 实例类型
   * @param <T> 类型
   * @return 实现对象实例 @implSpec MinioClientWrapper
   */
  fun <T> nativeHandle(instanceType: Class<T>): T

  /**
   * ## 根据文件参数删除对象
   *
   * @param fileInfo 文件信息
   * @return 是否删除成功
   */
  fun removeObject(fileInfo: FileArgs): Boolean

  /** 对外暴露的 baseUrl */
  val exposedBaseUrl: String

  /**
   * 创建新的文件夹
   *
   * @param dirName dir名字
   */
  fun makeDirs(dirName: String)

  /** ## 判断文件夹是否存在 */
  fun existsDir(dirName: String): Boolean

  /** 删除文件 */
  fun removeFile(fileInfo: FileArgs): Boolean

  /**
   * 创建公共文件夹
   *
   * @param dir dir
   */
  fun publicDir(dir: String)

  /**
   * 上传
   *
   * @param stream 流
   * @param fileInfo 文件信息
   * @return [InMap]
   */
  fun upload(stream: InputStream, fileInfo: FileArgs): InMap

  /**
   * 上传
   *
   * @param stream 流
   * @param fileInfo 文件信息
   * @param afterExec 后执行
   * @return [InMap]
   */
  fun upload(stream: InputStream, fileInfo: FileArgs, afterExec: Runnable): InMap

  /**
   * 上传
   *
   * @param stream 流
   * @param fileArgs 文件参数
   * @param afterExec 后执行
   * @return [InMap]
   */
  fun upload(stream: InputStream, fileArgs: FileArgs, afterExec: Consumer<FileArgs>): InMap

  /**
   * 下载
   *
   * @param stream 流
   * @param fileInfo 文件信息
   * @return [OutMap]
   * @throws IOException ioexception
   */
  fun download(stream: OutputStream, fileInfo: FileArgs): OutMap

  /**
   * 下载
   *
   * @param beforeExec 在执行之前
   * @param stream 流
   * @param fileInfo 文件信息
   * @return [OutMap]
   * @throws IOException ioexception
   */
  fun download(beforeExec: Runnable, stream: OutputStream, fileInfo: FileArgs): OutMap

  /**
   * 下载
   *
   * @param beforeExec 在执行之前
   * @param stream 流
   * @param fileInfo 文件信息
   * @return [OutMap]
   * @throws IOException ioexception
   */
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
