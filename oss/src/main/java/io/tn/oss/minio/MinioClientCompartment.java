package io.tn.oss.minio;

import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.Result;
import io.minio.messages.Item;
import io.tn.oss.abstracts.InMap;
import io.tn.oss.abstracts.OutMap;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * OSS 抽象与 minio 具体实现的隔离继承层
 *
 * @author TrueNine
 * @since 2023-02-20
 */
public sealed class MinioClientCompartment extends MinioClientBaseOperator permits MinioClientWrapper {
  protected MinioClientCompartment(MinioClient client) {
    super(client);
  }

  protected InMap ins(ObjectWriteResponse resp, InputStream stream) {
    return new InMap() {
      @Override
      public InputStream usedStream() {
        return stream;
      }

      @Override
      public String mimeType() {
        return headerContentType(resp.headers());
      }

      @Override
      public String fileName() {
        return resp.object();
      }

      @Override
      public String directoryName() {
        return resp.bucket();
      }

      @Override
      public long size() {
        return headerSize(resp.headers());
      }
    };
  }

  protected OutMap outs(GetObjectResponse resp, OutputStream stream) {
    return new OutMap() {
      @Override
      public OutputStream usedStream() {
        return stream;
      }

      @Override
      public String mimeType() {
        return headerContentType(resp.headers());
      }

      @Override
      public String fileName() {
        return resp.object();
      }

      @Override
      public String directoryName() {
        return resp.bucket();
      }

      @Override
      public long size() {
        return headerSize(resp.headers());
      }
    };
  }


  @SneakyThrows
  public List<String> listFiles(String dir) {
    var result = new ArrayList<String>();
    for (Result<Item> f : getObjects(dir)) {
      result.add(f.get().objectName());
    }
    return result;
  }
}
