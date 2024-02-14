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
