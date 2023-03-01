package com.truenine.component.oss.amazon;

public class S3Policies {

  public static class Effect {
    public static final String ALLOW = "Allow";
  }

  public static class Bucket {
    public static final String GET_LOCATION = "GetBucketLocation";
    public static final String LIST = "ListBucket";
    public static final String LIST_MUL_UPLOADS = "ListBucketMultipartUploads";
  }

  public static class Obj {
    public static final String GET = "GetObject";
    public static final String PUT = "PutObject";
    public static final String DEL = "DeleteObject";
    public static final String LIST_MUL_UPLOAD_PARTS = "ListMultipartUploadParts";
    public static final String ABORT_MUL_UPLOAD = "AbortMultipartUpload";
  }
}
