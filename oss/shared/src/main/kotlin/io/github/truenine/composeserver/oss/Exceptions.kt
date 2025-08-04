package io.github.truenine.composeserver.oss

/** Base exception for all object storage operations */
open class ObjectStorageException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

/** Exception thrown when a bucket operation fails */
open class BucketException(message: String, cause: Throwable? = null) : ObjectStorageException(message, cause)

/** Exception thrown when a bucket already exists */
class BucketAlreadyExistsException(bucketName: String, cause: Throwable? = null) : BucketException("Bucket '$bucketName' already exists", cause)

/** Exception thrown when a bucket is not found */
class BucketNotFoundException(bucketName: String, cause: Throwable? = null) : BucketException("Bucket '$bucketName' not found", cause)

/** Exception thrown when a bucket is not empty and cannot be deleted */
class BucketNotEmptyException(bucketName: String, cause: Throwable? = null) : BucketException("Bucket '$bucketName' is not empty", cause)

/** Exception thrown when an object operation fails */
open class ObjectException(message: String, cause: Throwable? = null) : ObjectStorageException(message, cause)

/** Exception thrown when an object is not found */
class ObjectNotFoundException(bucketName: String, objectName: String, cause: Throwable? = null) :
  ObjectException("Object '$objectName' not found in bucket '$bucketName'", cause)

/** Exception thrown when an object already exists and overwrite is not allowed */
class ObjectAlreadyExistsException(bucketName: String, objectName: String, cause: Throwable? = null) :
  ObjectException("Object '$objectName' already exists in bucket '$bucketName'", cause)

/** Exception thrown when multipart upload operations fail */
class MultipartUploadException(message: String, cause: Throwable? = null) : ObjectStorageException(message, cause)

/** Exception thrown when authentication fails */
class AuthenticationException(message: String, cause: Throwable? = null) : ObjectStorageException(message, cause)

/** Exception thrown when authorization fails */
class AuthorizationException(message: String, cause: Throwable? = null) : ObjectStorageException(message, cause)

/** Exception thrown when network operations fail */
class NetworkException(message: String, cause: Throwable? = null) : ObjectStorageException(message, cause)

/** Exception thrown when configuration is invalid */
class ConfigurationException(message: String, cause: Throwable? = null) : ObjectStorageException(message, cause)

/** Exception thrown when service is unavailable */
class ServiceUnavailableException(message: String, cause: Throwable? = null) : ObjectStorageException(message, cause)

/** Exception thrown when quota is exceeded */
class QuotaExceededException(message: String, cause: Throwable? = null) : ObjectStorageException(message, cause)

/** Exception thrown when request parameters are invalid */
class InvalidRequestException(message: String, cause: Throwable? = null) : ObjectStorageException(message, cause)
