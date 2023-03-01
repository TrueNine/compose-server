package io.tn.oss.minio;

import io.minio.*;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import io.tn.core.api.http.Headers;
import io.tn.oss.FileArgs;
import io.tn.oss.amazon.S3PolicyCreator;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * minio 基础层
 *
 * @author TrueNine
 * @since 2022-12-29
 */
public sealed class MinioClientBaseOperator permits MinioClientCompartment {
  protected final MinioClient minioClient;

  protected MinioClientBaseOperator(MinioClient client) {
    this.minioClient = client;
  }

  protected String headerContentType(okhttp3.Headers headers) {
    return headers.get(Headers.CONTENT_TYPE);
  }

  protected String headerSizeStr(okhttp3.Headers headers) {
    return headers.get(Headers.CONTENT_LENGTH);
  }

  protected long headerSize(okhttp3.Headers headers) {
    return Long.parseLong(headerSizeStr(headers));
  }


  @SneakyThrows
  protected GetObjectResponse getObject(FileArgs fileInfo, OutputStream stream) {
    return minioClient.getObject(GetObjectArgs.builder().bucket(fileInfo.getDir()).object(fileInfo.getFileName()).build());
  }

  @SneakyThrows
  protected void publicBucket(String bucketName) {
    minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
      .bucket(bucketName)
      .config(S3PolicyCreator.publicBucketAndReadOnly(bucketName).json())
      .build());
  }

  @SneakyThrows
  protected ObjectWriteResponse putObject(FileArgs fileInfo, InputStream stream) {
    return minioClient.putObject(PutObjectArgs.builder().bucket(fileInfo.getDir()).object(fileInfo.getFileName()).contentType(fileInfo.getMimeType()).stream(stream, fileInfo.getSize(), -1).build());
  }

  @SneakyThrows
  protected List<String> listFiles(String dir) {
    var items = minioClient.listObjects(ListObjectsArgs.builder()
      .bucket(dir)
      .build());

    List<String> results = new ArrayList<>();
    for (Result<Item> item : items) {
      results.add(item.get().objectName());
    }
    return results;
  }

  @SneakyThrows
  protected List<String> listDir() {
    return minioClient.listBuckets().stream().map(Bucket::name)
      .collect(Collectors.toList());
  }

  @SneakyThrows
  protected void createBucket(String dirName) {
    minioClient.makeBucket(MakeBucketArgs.builder()
      .bucket(dirName)
      .build());
  }

  @SneakyThrows
  protected List<Bucket> getBuckets() {
    return minioClient.listBuckets();
  }

  protected Iterable<Result<Item>> getObjects(String dir) {
    return minioClient.listObjects(ListObjectsArgs.builder()
      .bucket(dir)
      .build());
  }

  protected MinioClient getClient() {
    return this.minioClient;
  }
}
