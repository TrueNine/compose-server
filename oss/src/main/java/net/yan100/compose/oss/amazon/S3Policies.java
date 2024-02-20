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
package net.yan100.compose.oss.amazon;

public interface S3Policies {
    interface Effect {
        String ALLOW = "Allow";
    }

    interface Bucket {
        String GET_LOCATION = "GetBucketLocation";
        String LIST = "ListBucket";
        String LIST_MUL_UPLOADS = "ListBucketMultipartUploads";
    }

    interface Object {
        String GET = "GetObject";
        String PUT = "PutObject";
        String DEL = "DeleteObject";
        String LIST_MUL_UPLOAD_PARTS = "ListMultipartUploadParts";
        String ABORT_MUL_UPLOAD = "AbortMultipartUpload";
    }
}
