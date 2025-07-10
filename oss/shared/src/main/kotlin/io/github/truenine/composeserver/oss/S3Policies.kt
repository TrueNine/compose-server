package io.github.truenine.composeserver.oss

interface S3Policies {
  interface Effect {
    companion object {
      const val ALLOW: String = "Allow"
    }
  }

  interface Bucket {
    companion object {
      const val GET_LOCATION: String = "GetBucketLocation"
      const val LIST: String = "ListBucket"
      const val LIST_MUL_UPLOADS: String = "ListBucketMultipartUploads"
    }
  }

  interface Object {
    companion object {
      const val GET: String = "GetObject"
      const val PUT: String = "PutObject"
      const val DEL: String = "DeleteObject"
      const val LIST_MUL_UPLOAD_PARTS: String = "ListMultipartUploadParts"
      const val ABORT_MUL_UPLOAD: String = "AbortMultipartUpload"
    }
  }
}
