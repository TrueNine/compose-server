package net.yan100.compose.oss.abstracts;

import net.yan100.compose.oss.FileArgs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Consumer;

/**
 * 对文件系统得抽象，使得编程接口统一化
 *
 * @author TrueNine
 */
public interface Oss {

  /**
   * 获取一个实现对象，以便于应急使用，调用具体细节。
   * 如：使用 minio 实现，则实例为 MinioClient
   * 除非明确知晓类型，否则会类型转换错误
   *
   * @param instanceType 实例类型
   * @param <T>          类型
   * @return 实现对象实例
   * @implSpec MinioClientWrapper
   */
  <T> T as(Class<T> instanceType);

  /**
   * 创建新的文件夹
   *
   * @param dirName dir名字
   */
  void makeDirs(String dirName);

  /**
   * 创建公共文件夹
   *
   * @param dir dir
   */
  void publicDir(String dir);

  /**
   * 上传
   *
   * @param stream   流
   * @param fileInfo 文件信息
   * @return {@link InMap}
   */
  InMap upload(InputStream stream, FileArgs fileInfo);


  /**
   * 上传
   *
   * @param stream    流
   * @param fileInfo  文件信息
   * @param afterExec 后执行
   * @return {@link InMap}
   */
  InMap upload(InputStream stream, FileArgs fileInfo, Runnable afterExec);


  /**
   * 上传
   *
   * @param stream    流
   * @param fileArgs  文件参数
   * @param afterExec 后执行
   * @return {@link InMap}
   */
  InMap upload(InputStream stream, FileArgs fileArgs, Consumer<FileArgs> afterExec);


  /**
   * 下载
   *
   * @param stream   流
   * @param fileInfo 文件信息
   * @return {@link OutMap}
   * @throws IOException ioexception
   */
  OutMap download(OutputStream stream, FileArgs fileInfo) throws IOException;


  /**
   * 下载
   *
   * @param beforeExec 在执行之前
   * @param stream     流
   * @param fileInfo   文件信息
   * @return {@link OutMap}
   * @throws IOException ioexception
   */
  OutMap download(Runnable beforeExec, OutputStream stream, FileArgs fileInfo) throws IOException;


  /**
   * 下载
   *
   * @param beforeExec 在执行之前
   * @param stream     流
   * @param fileInfo   文件信息
   * @return {@link OutMap}
   * @throws IOException ioexception
   */
  OutMap download(Consumer<FileArgs> beforeExec, OutputStream stream, FileArgs fileInfo) throws IOException;

  /**
   * 列表文件
   *
   * @param dir dir
   * @return {@link List}<{@link String}>
   */
  List<String> listFiles(String dir);

  /**
   * dir列表
   *
   * @return {@link List}<{@link String}>
   */
  List<String> listDir();
}
