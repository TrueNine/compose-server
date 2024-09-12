/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
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
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
*/
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
@Deprecated
public class FileArgs {
    private String dir;
    private String fileName;
    private String mimeType;
    private Long size;

    public static FileArgs useStreamMap(StreamsMap map) {
        return FileArgs.builder()
            .dir(map.getDirName())
            .fileName(map.getFName())
            .mimeType(map.getMediaType())
            .size(map.getSize())
            .build();
    }

    public String getSizeStr() {
        return Long.toString(this.size);
    }

    public void setSizeStr(String size) {
        this.setSize(Long.parseLong(size));
    }
}
