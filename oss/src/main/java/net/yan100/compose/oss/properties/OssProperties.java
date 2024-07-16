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
package net.yan100.compose.oss.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * oss属性
 *
 * @author TrueNine
 * @since 2022-10-28
 */
@Data
@ConfigurationProperties(prefix = "compose.oss")
public class OssProperties {
    private Type type = Type.FILE;

    /** ## 对外暴露的访问路径 */
    private String exposeBaseUrl = "http://localhost:9999/not_set_oss_expose_base_url";

    /** ## minio相关配置 */
    @NestedConfigurationProperty private MinioProperties minio;

    /** ## 阿里云相关配置 */
    @NestedConfigurationProperty private AliCloudOssProperties aliyun;

    /**
     * ## 类型
     *
     * @author TrueNine
     * @since 2022-10-28
     */
    public enum Type {
        /** 内置文件系统 */
        FILE,
        /** mysql 数据库 */
        MYSQL_DB,
        /** minio */
        MINIO,
        /** 阿里云 */
        ALI_CLOUD_OSS,
        /** 华为云 */
        HUAWEI_CLOUD
    }
}
