package io.tn.oss.minio;

import io.minio.MinioClient;
import io.minio.messages.Bucket;
import io.tn.oss.FileArgs;
import io.tn.oss.abstracts.InMap;
import io.tn.oss.abstracts.Oss;
import io.tn.oss.abstracts.OutMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * oss 的 minio 实现
 *
 * @author TrueNine
 * @since 2023-02-20
 */
public non-sealed class MinioClientWrapper extends MinioClientCompartment implements Oss {
  public MinioClientWrapper(MinioClient client) {
    super(client);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T as(Class<T> instanceType) {
    return (T) super.getClient();
  }

  @Override
  public void makeDirs(String dirName) {
    createBucket(dirName);
  }

  @Override
  public void publicDir(String dir) {
    publicBucket(dir);
  }

  @Override
  public InMap upload(InputStream stream, FileArgs fileInfo) {
    var ins = putObject(fileInfo, stream);
    return ins(ins, stream);
  }

  @Override
  public InMap upload(InputStream stream, FileArgs fileInfo, Runnable afterExec) {
    var ins = upload(stream, fileInfo);
    afterExec.run();
    return ins;
  }

  @Override
  public InMap upload(InputStream stream, FileArgs fileArgs, Consumer<FileArgs> afterExec) {
    var ins = upload(stream, fileArgs);
    afterExec.accept(FileArgs.useStreamMap(ins));
    return ins;
  }

  @Override
  public OutMap download(OutputStream stream, FileArgs fileInfo) throws IOException {
    var outs = getObject(fileInfo, stream);
    outs.transferTo(stream);
    return outs(outs, stream);
  }

  @Override
  public OutMap download(Runnable beforeExec, OutputStream stream, FileArgs fileInfo) throws IOException {
    var outs = getObject(fileInfo, stream);
    beforeExec.run();
    outs.transferTo(stream);
    return download(stream, fileInfo);
  }

  @Override
  public OutMap download(Consumer<FileArgs> beforeExec, OutputStream stream, FileArgs fileInfo) throws IOException {
    var outs = getObject(fileInfo, stream);
    var wrapper = outs(outs, stream);
    beforeExec.accept(FileArgs.useStreamMap(wrapper));
    outs.transferTo(stream);
    return outs(outs, stream);
  }

  @Override
  public List<String> listFiles(String dir) {
    return super.listFiles(dir);
  }

  @Override
  public List<String> listDir() {
    return getBuckets().stream().map(Bucket::name)
      .collect(Collectors.toList());
  }
}
