package net.yan100.compose.oss;

import lombok.Builder;
import lombok.Data;

/**
 * 文件参数
 *
 * @author TrueNine
 * @since 2022-10-28
 */
@Data
@Builder
public class FileArgs {
  private String dir;
  private String fileName;
  private String mimeType;
  private Long size;

  public static FileArgs useStreamMap(StreamsMap map) {
    return FileArgs
      .builder()
      .dir(map.directoryName())
      .fileName(map.fileName())
      .mimeType(map.mimeType())
      .size(map.size())
      .build();
  }

  public String getSizeStr() {
    return Long.toString(this.size);
  }

  public void setSizeStr(String size) {
    this.setSize(Long.parseLong(size));
  }
}
