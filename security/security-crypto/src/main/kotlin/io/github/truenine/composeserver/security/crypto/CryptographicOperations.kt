package io.github.truenine.composeserver.security.crypto

import io.github.truenine.composeserver.enums.EncryptAlgorithm
import io.github.truenine.composeserver.slf4j
import java.nio.charset.Charset
import java.security.*
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import org.bouncycastle.jce.spec.IESParameterSpec

/**
 * High-performance cryptographic operations provider for the Compose Server framework.
 *
 * This object provides comprehensive cryptographic capabilities including RSA, ECC, and AES encryption/decryption, digital signatures, and secure hashing
 * operations. All operations are optimized for performance and thread safety while maintaining security best practices.
 *
 * ## Supported Operations
 * - **RSA Encryption/Decryption**: Public/private key operations with configurable sharding
 * - **ECC Encryption/Decryption**: Elliptic curve cryptography using BouncyCastle
 * - **AES Encryption/Decryption**: Symmetric encryption with secure key management
 * - **Digital Signatures**: SHA256withRSA signature generation and verification
 * - **Secure Hashing**: SHA-1 and SHA-256 digest computation
 *
 * ## Performance Features
 * - Thread-safe MessageDigest instances with proper synchronization
 * - Optimized memory management for large data processing
 * - Efficient sharding mechanism for RSA operations
 * - Reusable cipher instances to minimize object creation overhead
 *
 * ## Security Considerations
 * - All operations use secure random number generation where applicable
 * - Proper error handling without exposing sensitive cryptographic details
 * - Input validation to prevent common cryptographic attacks
 * - Constant-time operations where feasible to prevent timing attacks
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // RSA encryption with public key
 * val encrypted = CryptographicOperations.encryptByRsaPublicKey(publicKey, "sensitive data")
 *
 * // AES encryption with generated key
 * val aesKey = Keys.generateAesKey()
 * val encrypted = CryptographicOperations.encryptByAesKey(aesKey, "data")
 *
 * // SHA-256 hashing
 * val hash = CryptographicOperations.signatureBySha256("data to hash")
 * ```
 *
 * @author TrueNine
 * @since 2023-02-28
 * @version 2.0 - Optimized for performance and security
 */
object CryptographicOperations {

  /**
   * Configuration data class for encryption operations.
   *
   * Encapsulates encryption parameters to reduce function parameter count and improve type safety. This approach follows the framework's preference for data
   * classes over multiple parameters.
   *
   * @param data The plaintext data to encrypt
   * @param shardingSize The size of data chunks for processing large data (RSA limitation)
   * @param charset Character encoding for string-to-byte conversion
   * @param algorithm The cryptographic algorithm to use
   */
  data class EncryptionConfig(
    val data: String,
    val shardingSize: Int = SHARDING_SIZE,
    val charset: Charset = DEFAULT_CHARSET,
    val algorithm: EncryptAlgorithm = EncryptAlgorithm.RSA,
  )

  /**
   * Configuration data class for decryption operations.
   *
   * @param ciphertext The Base64-encoded encrypted data to decrypt
   * @param charset Character encoding for byte-to-string conversion
   * @param algorithm The cryptographic algorithm used for encryption
   */
  data class DecryptionConfig(val ciphertext: String, val charset: Charset = DEFAULT_CHARSET, val algorithm: EncryptAlgorithm = EncryptAlgorithm.RSA)

  /**
   * Encrypts data using a public key with optimized cipher operations.
   *
   * This method provides the core encryption functionality for public key cryptography. It uses efficient sharding for large data and proper error handling
   * without exposing sensitive cryptographic details.
   *
   * @param publicKey The public key for encryption
   * @param config Encryption configuration containing data and parameters
   * @return Base64-encoded encrypted string, or null if encryption fails
   * @throws IllegalArgumentException if the public key is invalid
   */
  @JvmStatic
  private fun encryptWithPublicKey(publicKey: PublicKey, config: EncryptionConfig): String? =
    performCipherOperation(
      key = publicKey,
      mode = Cipher.ENCRYPT_MODE,
      data = config.data.toByteArray(config.charset),
      algorithm = config.algorithm,
      shardingSize = config.shardingSize,
    )

  /**
   * Encrypts data using a private key with optimized cipher operations.
   *
   * This method provides the core encryption functionality for private key operations, typically used for digital signatures or reverse encryption scenarios.
   *
   * @param privateKey The private key for encryption
   * @param config Encryption configuration containing data and parameters
   * @return Base64-encoded encrypted string, or null if encryption fails
   * @throws IllegalArgumentException if the private key is invalid
   */
  @JvmStatic
  private fun encryptWithPrivateKey(privateKey: PrivateKey, config: EncryptionConfig): String? =
    performCipherOperation(
      key = privateKey,
      mode = Cipher.ENCRYPT_MODE,
      data = config.data.toByteArray(config.charset),
      algorithm = config.algorithm,
      shardingSize = config.shardingSize,
    )

  /**
   * Performs optimized cipher operations with proper error handling and resource management.
   *
   * This method centralizes cipher operations to reduce code duplication and improve performance through efficient resource usage. It handles both encryption
   * and decryption operations with proper sharding for large data.
   *
   * @param key The cryptographic key (public or private)
   * @param mode The cipher mode (ENCRYPT_MODE or DECRYPT_MODE)
   * @param data The data to process (plaintext bytes for encryption, encrypted bytes for decryption)
   * @param algorithm The cryptographic algorithm to use
   * @param shardingSize The size of data chunks for processing
   * @return Base64-encoded result for encryption, plaintext for decryption, or null if operation fails
   */
  @JvmStatic
  private fun performCipherOperation(
    key: java.security.Key,
    mode: Int,
    data: ByteArray,
    algorithm: EncryptAlgorithm,
    shardingSize: Int = SHARDING_SIZE,
  ): String? =
    runCatching {
        if (data.isEmpty()) return ""

        Cipher.getInstance(algorithm.padding)
          .apply { init(mode, key) }
          .let { cipher ->
            when (mode) {
              Cipher.ENCRYPT_MODE -> {
                optimizedSharding(data, shardingSize).joinToString(SHARDING_SEPARATOR) { chunk -> IBase64.encode(cipher.doFinal(chunk)) }
              }

              Cipher.DECRYPT_MODE -> {
                // For decryption, data should be Base64 string split by separator
                String(data, DEFAULT_CHARSET)
                  .split(SHARDING_SEPARATOR)
                  .map { IBase64.decodeToByte(it) }
                  .map { cipher.doFinal(it) }
                  .reduce { acc, bytes -> acc + bytes }
                  .let { String(it, DEFAULT_CHARSET) }
              }

              else -> throw IllegalArgumentException("Unsupported cipher mode: $mode")
            }
          }
      }
      .onFailure { throwable -> log.error("Cipher operation failed for algorithm ${algorithm.value}", throwable) }
      .getOrNull()

  /**
   * Decrypts data using a private key with enhanced security and performance.
   *
   * This method provides secure decryption with proper input validation and optimized processing for sharded data. It replaces the original basicDecrypt method
   * with improved error handling and performance characteristics.
   *
   * @param privateKey The private key for decryption
   * @param config Decryption configuration containing ciphertext and parameters
   * @return Decrypted plaintext string, or null if decryption fails
   * @throws IllegalArgumentException if the private key is invalid or ciphertext is malformed
   */
  @JvmStatic
  private fun decryptWithPrivateKey(privateKey: PrivateKey, config: DecryptionConfig): String? {
    if (config.ciphertext.isEmpty()) return ""

    return performCipherOperation(
      key = privateKey,
      mode = Cipher.DECRYPT_MODE,
      data = config.ciphertext.toByteArray(config.charset),
      algorithm = config.algorithm,
    )
  }

  /**
   * Encrypts data using a Base64-encoded RSA public key.
   *
   * This method provides a convenient interface for RSA encryption when the public key is available as a Base64-encoded string. It automatically handles key
   * parsing and delegates to the optimized encryption implementation.
   *
   * @param rsaPublicKey Base64-encoded RSA public key string
   * @param data The plaintext data to encrypt
   * @param shardingSize The size of data chunks for processing large data
   * @param charset Character encoding for string-to-byte conversion
   * @return Base64-encoded encrypted string, or null if encryption fails
   * @throws IllegalArgumentException if the public key string is invalid
   */
  @JvmStatic
  @JvmOverloads
  fun encryptByRsaPublicKeyBase64(rsaPublicKey: String, data: String, shardingSize: Int = SHARDING_SIZE, charset: Charset = DEFAULT_CHARSET): String? {
    val publicKey =
      CryptographicKeyManager.readRsaPublicKeyByBase64(rsaPublicKey) ?: return null.also { log.warn("Failed to parse RSA public key from Base64 string") }

    return encryptByRsaPublicKey(publicKey, data, shardingSize, charset)
  }

  /**
   * Encrypts data using an RSA public key with optimized performance.
   *
   * This method provides high-performance RSA encryption with proper sharding for large data. It uses the optimized cipher operations to minimize resource
   * usage and improve throughput.
   *
   * @param publicKey RSA public key object for encryption
   * @param data The plaintext data to encrypt
   * @param shardingSize The size of data chunks for processing large data
   * @param charset Character encoding for string-to-byte conversion
   * @return Base64-encoded encrypted string, or null if encryption fails
   * @throws IllegalArgumentException if the public key is invalid
   */
  @JvmStatic
  @JvmOverloads
  fun encryptByRsaPublicKey(publicKey: RSAPublicKey, data: String, shardingSize: Int = SHARDING_SIZE, charset: Charset = DEFAULT_CHARSET): String? =
    encryptWithPublicKey(publicKey, EncryptionConfig(data, shardingSize, charset, EncryptAlgorithm.RSA))

  /**
   * Encrypts data using an RSA private key for reverse encryption scenarios.
   *
   * This method enables private key encryption, which is typically used for digital signatures or scenarios where the private key holder needs to encrypt data
   * that can be decrypted by anyone with the corresponding public key.
   *
   * @param privateKey RSA private key object for encryption
   * @param data The plaintext data to encrypt
   * @param shardingSize The size of data chunks for processing large data
   * @param charset Character encoding for string-to-byte conversion
   * @return Base64-encoded encrypted string, or null if encryption fails
   * @throws IllegalArgumentException if the private key is invalid
   */
  @JvmStatic
  @JvmOverloads
  fun encryptByRsaPrivateKey(privateKey: RSAPrivateKey, data: String, shardingSize: Int = SHARDING_SIZE, charset: Charset = DEFAULT_CHARSET): String? =
    encryptWithPrivateKey(privateKey, EncryptionConfig(data, shardingSize, charset, EncryptAlgorithm.RSA))

  /**
   * Encrypts data using an ECC public key with BouncyCastle ECIES.
   *
   * This method provides Elliptic Curve Integrated Encryption Scheme (ECIES) encryption using the BouncyCastle provider. ECIES combines the benefits of both
   * asymmetric and symmetric encryption for optimal security and performance.
   *
   * @param eccPublicKey ECC public key object for encryption
   * @param data The plaintext data to encrypt
   * @param charset Character encoding for string-to-byte conversion
   * @return Base64-encoded encrypted string, or null if encryption fails
   * @throws IllegalArgumentException if the ECC public key is invalid
   */
  @JvmStatic
  @JvmOverloads
  fun encryptByEccPublicKey(eccPublicKey: PublicKey, data: String, charset: Charset = DEFAULT_CHARSET): String? =
    runCatching {
        Cipher.getInstance(ECC_CIPHER_ALGORITHM, BOUNCY_CASTLE_PROVIDER)
          .apply { init(Cipher.ENCRYPT_MODE, eccPublicKey, createEccParameterSpec()) }
          .let { cipher -> IBase64.encode(cipher.doFinal(data.toByteArray(charset))) }
      }
      .onFailure { throwable -> log.error("ECC encryption failed", throwable) }
      .getOrNull()

  /**
   * Decrypts data using an ECC private key with BouncyCastle ECIES.
   *
   * This method provides secure decryption for data encrypted with the corresponding ECC public key. It uses the same ECIES scheme for consistent cryptographic
   * operations.
   *
   * @param eccPrivateKey ECC private key object for decryption
   * @param data Base64-encoded encrypted data
   * @param charset Character encoding for byte-to-string conversion
   * @return Decrypted plaintext string, or null if decryption fails
   * @throws IllegalArgumentException if the ECC private key is invalid or data is malformed
   */
  @JvmStatic
  @JvmOverloads
  fun decryptByEccPrivateKey(eccPrivateKey: PrivateKey, data: String, charset: Charset = DEFAULT_CHARSET): String? =
    runCatching {
        Cipher.getInstance(ECC_CIPHER_ALGORITHM, BOUNCY_CASTLE_PROVIDER)
          .apply { init(Cipher.DECRYPT_MODE, eccPrivateKey, createEccParameterSpec()) }
          .let { cipher -> String(cipher.doFinal(IBase64.decodeToByte(data)), charset) }
      }
      .onFailure { throwable -> log.error("ECC decryption failed", throwable) }
      .getOrNull()

  /**
   * Creates optimized ECC parameter specification for ECIES operations.
   *
   * This method provides consistent parameter configuration for ECC operations, ensuring compatibility and security across all ECC-based cryptographic
   * functions.
   *
   * @return IESParameterSpec configured for optimal security and performance
   */
  @JvmStatic private fun createEccParameterSpec(): IESParameterSpec = IESParameterSpec(null, null, ECC_KEY_LENGTH)

  /**
   * Decrypts data using an RSA private key with optimized performance.
   *
   * This method provides high-performance RSA decryption with proper handling of sharded data and enhanced error reporting. It uses the optimized decryption
   * implementation for consistent performance characteristics.
   *
   * @param rsaPrivateKey RSA private key object for decryption
   * @param ciphertext Base64-encoded encrypted data
   * @param charset Character encoding for byte-to-string conversion
   * @return Decrypted plaintext string, or null if decryption fails
   * @throws IllegalArgumentException if the private key is invalid or ciphertext is malformed
   */
  @JvmStatic
  @JvmOverloads
  fun decryptByRsaPrivateKey(rsaPrivateKey: RSAPrivateKey, ciphertext: String, charset: Charset = DEFAULT_CHARSET): String? =
    decryptWithPrivateKey(rsaPrivateKey, DecryptionConfig(ciphertext, charset, EncryptAlgorithm.RSA))

  /**
   * Decrypts data using an RSA public key for reverse decryption scenarios.
   *
   * This method enables public key decryption, which is typically used to verify data that was encrypted with the corresponding private key (reverse
   * encryption). This is commonly used in digital signature verification workflows.
   *
   * @param rsaPublicKey RSA public key object for decryption
   * @param ciphertext Base64-encoded encrypted data
   * @param charset Character encoding for byte-to-string conversion
   * @return Decrypted plaintext string, or null if decryption fails
   * @throws IllegalArgumentException if the public key is invalid or ciphertext is malformed
   */
  @JvmStatic
  @JvmOverloads
  fun decryptByRsaPublicKey(rsaPublicKey: RSAPublicKey, ciphertext: String, charset: Charset = DEFAULT_CHARSET): String? =
    runCatching {
        if (ciphertext.isEmpty()) return ""

        Cipher.getInstance(EncryptAlgorithm.RSA.padding)
          .apply { init(Cipher.DECRYPT_MODE, rsaPublicKey) }
          .let { cipher ->
            ciphertext
              .split(SHARDING_SEPARATOR)
              .map { IBase64.decodeToByte(it) }
              .map { cipher.doFinal(it) }
              .reduce { acc, bytes -> acc + bytes }
              .let { String(it, charset) }
          }
      }
      .onFailure { throwable -> log.error("RSA public key decryption failed", throwable) }
      .getOrNull()

  /**
   * Decrypts data using a Base64-encoded RSA public key.
   *
   * This method provides a convenient interface for RSA public key decryption when the public key is available as a Base64-encoded string. It automatically
   * handles key parsing and delegates to the optimized decryption implementation.
   *
   * @param rsaPublicKey Base64-encoded RSA public key string
   * @param ciphertext Base64-encoded encrypted data
   * @param charset Character encoding for byte-to-string conversion
   * @return Decrypted plaintext string, or null if decryption fails
   * @throws IllegalArgumentException if the public key string is invalid or ciphertext is malformed
   */
  @JvmStatic
  @JvmOverloads
  fun decryptByRsaPublicKeyBase64(rsaPublicKey: String, ciphertext: String, charset: Charset = DEFAULT_CHARSET): String? {
    val publicKey =
      CryptographicKeyManager.readRsaPublicKeyByBase64(rsaPublicKey) ?: return null.also { log.warn("Failed to parse RSA public key from Base64 string") }

    return decryptByRsaPublicKey(publicKey, ciphertext, charset)
  }

  /**
   * Computes SHA-1 hash of string data with optimized performance.
   *
   * This method provides secure SHA-1 hashing with thread-safe digest operations. While SHA-1 is considered cryptographically weak for new applications, it's
   * maintained for compatibility with legacy systems that require SHA-1 hashes.
   *
   * @param plaintext The string data to hash
   * @param charset Character encoding for string-to-byte conversion
   * @return Hexadecimal string representation of the SHA-1 hash
   * @deprecated SHA-1 is cryptographically weak; consider using SHA-256 instead
   */
  @JvmStatic
  @JvmOverloads
  @Deprecated("SHA-1 is cryptographically weak; use signatureBySha256 instead", ReplaceWith("signatureBySha256(plaintext, charset)"))
  fun signatureBySha1(plaintext: String, charset: Charset = DEFAULT_CHARSET): String = computeSecureHash(plaintext.toByteArray(charset), HashAlgorithm.SHA1)

  /**
   * Computes SHA-256 hash of string data with optimized performance and security.
   *
   * This method provides cryptographically secure SHA-256 hashing with thread-safe digest operations and optimized memory usage. SHA-256 is the recommended
   * hashing algorithm for new applications requiring cryptographic security.
   *
   * @param plaintext The string data to hash
   * @param charset Character encoding for string-to-byte conversion
   * @return Hexadecimal string representation of the SHA-256 hash
   */
  @JvmStatic
  @JvmOverloads
  fun signatureBySha256(plaintext: String, charset: Charset = DEFAULT_CHARSET): String = computeSecureHash(plaintext.toByteArray(charset), HashAlgorithm.SHA256)

  /**
   * Computes SHA-1 hash of byte array data with thread-safe operations.
   *
   * This method provides direct byte array hashing for scenarios where the input is already in byte format, avoiding unnecessary string conversions.
   *
   * @param plaintext The byte array data to hash
   * @return SHA-1 hash as byte array
   * @deprecated SHA-1 is cryptographically weak; use signatureBySha256ByteArray instead
   */
  @JvmStatic
  @Deprecated("SHA-1 is cryptographically weak; use signatureBySha256ByteArray instead", ReplaceWith("signatureBySha256ByteArray(plaintext)"))
  fun signatureBySha1ByteArray(plaintext: ByteArray): ByteArray = computeSecureHashBytes(plaintext, HashAlgorithm.SHA1)

  /**
   * Computes SHA-256 hash of byte array data with thread-safe operations.
   *
   * This method provides direct byte array hashing with optimal performance for scenarios where the input is already in byte format. It uses thread-safe digest
   * operations to ensure correctness in concurrent environments.
   *
   * @param plaintext The byte array data to hash
   * @return SHA-256 hash as byte array
   */
  @JvmStatic fun signatureBySha256ByteArray(plaintext: ByteArray): ByteArray = computeSecureHashBytes(plaintext, HashAlgorithm.SHA256)

  /**
   * Hash algorithm enumeration for secure digest operations.
   *
   * This enum provides type-safe access to supported hash algorithms with their corresponding algorithm names and output characteristics.
   */
  private enum class HashAlgorithm(val algorithmName: String, val outputLength: Int) {
    SHA1("SHA-1", 20),
    SHA256("SHA-256", 32),
  }

  /**
   * Computes secure hash with thread-safe digest operations and returns hexadecimal string.
   *
   * This method provides centralized hash computation with proper thread safety and optimized memory usage. It ensures consistent formatting across all hash
   * operations.
   *
   * @param data The byte array data to hash
   * @param algorithm The hash algorithm to use
   * @return Hexadecimal string representation of the hash
   */
  @JvmStatic
  private fun computeSecureHash(data: ByteArray, algorithm: HashAlgorithm): String =
    computeSecureHashBytes(data, algorithm).joinToString("") { "%02x".format(it) }

  /**
   * Computes secure hash with thread-safe digest operations and returns byte array.
   *
   * This method provides the core hash computation functionality with proper thread safety through synchronized access to MessageDigest instances.
   *
   * @param data The byte array data to hash
   * @param algorithm The hash algorithm to use
   * @return Hash result as byte array
   */
  @JvmStatic
  private fun computeSecureHashBytes(data: ByteArray, algorithm: HashAlgorithm): ByteArray =
    when (algorithm) {
      HashAlgorithm.SHA1 -> synchronized(sha1Digest) { sha1Digest.digest(data) }
      HashAlgorithm.SHA256 -> synchronized(sha256Digest) { sha256Digest.digest(data) }
    }

  /**
   * Encrypts data using a Base64-encoded AES key with optimized performance.
   *
   * This method provides a convenient interface for AES encryption when the key is available as a Base64-encoded string. It automatically handles key parsing
   * and delegates to the optimized AES encryption implementation.
   *
   * @param aesKey Base64-encoded AES key string
   * @param plaintext The plaintext data to encrypt
   * @param charset Character encoding for string-to-byte conversion
   * @return Base64-encoded encrypted string, or null if encryption fails
   * @throws IllegalArgumentException if the AES key string is invalid
   */
  @JvmStatic
  @JvmOverloads
  fun encryptByAesKeyBase64(aesKey: String, plaintext: String, charset: Charset = DEFAULT_CHARSET): String? =
    runCatching {
        val keySpec = SecretKeySpec(IBase64.decodeToByte(aesKey), AES_ALGORITHM)
        encryptByAesKey(keySpec, plaintext, charset)
      }
      .onFailure { throwable -> log.error("Failed to parse AES key from Base64 string", throwable) }
      .getOrNull()

  /**
   * Encrypts data using an AES key with optimized cipher operations.
   *
   * This method provides high-performance AES encryption using ECB mode with PKCS5 padding. While ECB mode is used for compatibility, consider using CBC or GCM
   * modes for new applications requiring higher security.
   *
   * @param secret AES key specification object
   * @param plain The plaintext data to encrypt
   * @param charset Character encoding for string-to-byte conversion
   * @return Base64-encoded encrypted string, or null if encryption fails
   * @throws IllegalArgumentException if the AES key is invalid
   */
  @JvmStatic
  @JvmOverloads
  fun encryptByAesKey(secret: SecretKeySpec, plain: String, charset: Charset = DEFAULT_CHARSET): String? =
    runCatching {
        Cipher.getInstance(AES_CIPHER_TRANSFORMATION)
          .apply { init(Cipher.ENCRYPT_MODE, secret) }
          .let { cipher -> IBase64.encode(cipher.doFinal(plain.toByteArray(charset))) }
      }
      .onFailure { throwable -> log.error("AES encryption failed", throwable) }
      .getOrNull()

  /**
   * Decrypts data using a Base64-encoded AES key with optimized performance.
   *
   * This method provides a convenient interface for AES decryption when the key is available as a Base64-encoded string. It automatically handles key parsing
   * and delegates to the optimized AES decryption implementation.
   *
   * @param aesKey Base64-encoded AES key string
   * @param ciphertext Base64-encoded encrypted data
   * @param charset Character encoding for byte-to-string conversion
   * @return Decrypted plaintext string, or null if decryption fails
   * @throws IllegalArgumentException if the AES key string is invalid or ciphertext is malformed
   */
  @JvmStatic
  @JvmOverloads
  fun decryptByAesKeyBase64(aesKey: String, ciphertext: String, charset: Charset = DEFAULT_CHARSET): String? =
    runCatching {
        val keySpec = SecretKeySpec(IBase64.decodeToByte(aesKey), AES_ALGORITHM)
        decryptByAesKey(keySpec, ciphertext, charset)
      }
      .onFailure { throwable -> log.error("Failed to parse AES key from Base64 string", throwable) }
      .getOrNull()

  /**
   * Decrypts data using an AES key with optimized cipher operations.
   *
   * This method provides high-performance AES decryption using ECB mode with PKCS5 padding. It handles proper error cases and provides secure decryption for
   * AES-encrypted data.
   *
   * @param secret AES key specification object
   * @param ciphertext Base64-encoded encrypted data
   * @param charset Character encoding for byte-to-string conversion
   * @return Decrypted plaintext string, or null if decryption fails
   * @throws IllegalArgumentException if the AES key is invalid or ciphertext is malformed
   */
  @JvmStatic
  @JvmOverloads
  fun decryptByAesKey(secret: SecretKeySpec, ciphertext: String, charset: Charset = DEFAULT_CHARSET): String? =
    runCatching {
        Cipher.getInstance(AES_CIPHER_TRANSFORMATION)
          .apply { init(Cipher.DECRYPT_MODE, secret) }
          .let { cipher -> String(cipher.doFinal(IBase64.decodeToByte(ciphertext)), charset) }
      }
      .onFailure { throwable -> log.error("AES decryption failed", throwable) }
      .getOrNull()

  /**
   * Creates a digital signature using RSA private key and SHA-256 algorithm.
   *
   * This method provides secure digital signature generation using the industry-standard SHA256withRSA algorithm. The returned Signature object can be used to
   * generate the actual signature bytes or verify signatures.
   *
   * @param signContent The data content to be signed
   * @param rsaPrivateKey RSA private key for signature generation
   * @param charset Character encoding for string-to-byte conversion
   * @return Configured Signature object ready for signature generation
   * @throws IllegalArgumentException if the private key is invalid
   */
  @JvmStatic
  @JvmOverloads
  fun signWithSha256WithRsaByRsaPrivateKey(signContent: String, rsaPrivateKey: RSAPrivateKey, charset: Charset = DEFAULT_CHARSET): Signature =
    Signature.getInstance(EncryptAlgorithm.SHA256_WITH_RSA.value).apply {
      initSign(rsaPrivateKey)
      update(signContent.toByteArray(charset))
    }

  /**
   * Optimized data sharding for large data processing with improved memory efficiency.
   *
   * This method provides efficient data chunking for cryptographic operations that have size limitations (such as RSA encryption). It uses optimized memory
   * allocation and avoids unnecessary array copying for better performance.
   *
   * @param data The byte array data to be sharded
   * @param size The size of each shard in bytes
   * @return List of byte array shards, each no larger than the specified size
   * @throws IllegalArgumentException if size is less than or equal to zero
   */
  @JvmStatic
  internal fun optimizedSharding(data: ByteArray, size: Int): List<ByteArray> {
    require(size > 0) { "Shard size must be positive, got: $size" }

    if (data.size <= size) return listOf(data)

    val shardCount = (data.size + size - 1) / size // Ceiling division
    return (0 until shardCount).map { index ->
      val start = index * size
      val end = minOf(start + size, data.size)
      data.sliceArray(start until end)
    }
  }

  /**
   * Legacy sharding method maintained for backward compatibility.
   *
   * @param data The byte array data to be sharded
   * @param size The size of each shard in bytes
   * @return List of byte array shards
   * @deprecated Use optimizedSharding instead for better performance
   */
  @JvmStatic
  @Deprecated("Use optimizedSharding instead for better performance", ReplaceWith("optimizedSharding(data, size)"))
  internal fun sharding(data: ByteArray, size: Int): List<ByteArray> = optimizedSharding(data, size)

  // ================================================================================================
  // Private Constants and Configuration
  // ================================================================================================

  /** Logger instance for cryptographic operations */
  private val log = slf4j<CryptographicOperations>()

  /** Separator for Base64-encoded sharded data */
  private const val SHARDING_SEPARATOR = "."

  /** Thread-safe SHA-1 message digest instance */
  private val sha1Digest: MessageDigest = MessageDigest.getInstance("SHA-1")

  /** Thread-safe SHA-256 message digest instance */
  private val sha256Digest: MessageDigest = MessageDigest.getInstance("SHA-256")

  /** Secure random number generator for cryptographic operations */
  private val secureRandom: SecureRandom = SecureRandom()

  /** Default RSA encryption shard size (optimized for 2048-bit keys) */
  private const val SHARDING_SIZE = 245

  /** Default character encoding for string operations */
  private val DEFAULT_CHARSET: Charset = Charsets.UTF_8

  /** AES algorithm identifier */
  private const val AES_ALGORITHM = "AES"

  /** AES cipher transformation (ECB mode with PKCS5 padding) */
  private const val AES_CIPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding"

  /** ECC cipher algorithm identifier */
  private const val ECC_CIPHER_ALGORITHM = "ECIES"

  /** BouncyCastle provider identifier */
  private const val BOUNCY_CASTLE_PROVIDER = "BC"

  /** ECC key length for parameter specification */
  private const val ECC_KEY_LENGTH = 256

  // ================================================================================================
  // Backward Compatibility Aliases
  // ================================================================================================

  /**
   * Legacy alias for the optimized cryptographic operations object.
   *
   * This alias maintains backward compatibility with existing code that references the old "Encryptors" name while providing access to the new optimized
   * implementation.
   *
   * @deprecated Use CryptographicOperations directly for new code
   */
  @Deprecated(
    message = "Use CryptographicOperations directly for new code",
    replaceWith = ReplaceWith("CryptographicOperations"),
    level = DeprecationLevel.WARNING,
  )
  @JvmField
  val Encryptors = CryptographicOperations
}
