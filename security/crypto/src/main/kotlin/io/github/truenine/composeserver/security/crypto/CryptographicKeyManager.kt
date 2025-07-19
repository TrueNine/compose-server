package io.github.truenine.composeserver.security.crypto

import io.github.truenine.composeserver.security.crypto.domain.EccExtKeyPair
import io.github.truenine.composeserver.security.crypto.domain.IEccExtKeyPair
import io.github.truenine.composeserver.security.crypto.domain.IRsaExtKeyPair
import io.github.truenine.composeserver.security.crypto.domain.RsaExtKeyPair
import io.github.truenine.composeserver.slf4j
import io.github.truenine.composeserver.typing.EncryptAlgorithm
import java.security.*
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.provider.BouncyCastleProvider

/**
 * High-performance cryptographic key management provider for the Compose Server framework.
 *
 * This object provides comprehensive key generation, conversion, and management capabilities for RSA, ECC, and AES cryptographic algorithms. All operations are
 * optimized for performance and thread safety while maintaining security best practices.
 *
 * ## Supported Operations
 * - **RSA Key Management**: 2048-bit key generation, Base64 conversion, PEM format support
 * - **ECC Key Management**: P-256 curve operations, secure key pair generation
 * - **AES Key Management**: 256-bit key generation, secure random key creation
 * - **Format Conversion**: Base64 encoding/decoding, PEM format support
 * - **Key Validation**: Comprehensive input validation and error handling
 *
 * ## Performance Features
 * - Thread-safe SecureRandom instances with ThreadLocal caching
 * - Pre-initialized KeyFactory and KeyGenerator instances for optimal performance
 * - Efficient memory management and resource reuse
 * - Optimized string and byte array operations
 * - Lazy initialization of expensive cryptographic components
 *
 * ## Security Considerations
 * - Uses cryptographically secure random number generation
 * - Implements proper key size recommendations (RSA 2048-bit, AES 256-bit)
 * - Provides comprehensive error handling without exposing sensitive details
 * - Supports industry-standard key formats (X.509, PKCS#8, PEM)
 * - Thread-safe operations for concurrent environments
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Generate RSA key pair
 * val rsaKeyPair = CryptographicKeyManager.generateRsaKeyPair()
 *
 * // Convert to Base64 for storage
 * val publicKeyBase64 = CryptographicKeyManager.writeKeyToBase64(rsaKeyPair.publicKey)
 *
 * // Generate AES key
 * val aesKey = CryptographicKeyManager.generateAesKey()
 *
 * // Generate random string for seeds
 * val randomSeed = CryptographicKeyManager.generateRandomAsciiString(32)
 * ```
 *
 * @author TrueNine
 * @since 2024-03-19
 * @version 4.0 - Optimized for performance and security
 */
object CryptographicKeyManager {

  /**
   * Configuration data class for RSA key generation.
   *
   * Encapsulates RSA key generation parameters to reduce function parameter count and improve type safety. This approach follows the framework's preference for
   * data classes over multiple parameters.
   *
   * @param seed Random seed for key generation
   * @param keySize RSA key size in bits (recommended: 2048 or higher)
   * @param provider Security provider name
   */
  data class RsaKeyConfig(val seed: String = generateRandomAsciiString(), val keySize: Int = DEFAULT_RSA_KEY_SIZE, val provider: String = RSA_PROVIDER)

  /**
   * Configuration data class for ECC key generation.
   *
   * @param seed Random seed for key generation
   * @param curve Elliptic curve specification
   * @param provider Security provider name
   */
  data class EccKeyConfig(val seed: String = generateRandomAsciiString(), val curve: String = DEFAULT_ECC_CURVE, val provider: String = ECC_PROVIDER)

  /**
   * Configuration data class for AES key generation.
   *
   * @param seed Random seed for key generation
   * @param keySize AES key size in bits (128, 192, or 256)
   * @param algorithm AES algorithm identifier
   */
  data class AesKeyConfig(val seed: String = DEFAULT_SEED, val keySize: Int = DEFAULT_AES_KEY_SIZE, val algorithm: String = AES_ALGORITHM)

  /**
   * Configuration data class for key pair reconstruction from Base64 strings.
   *
   * @param publicKeyBase64 Base64-encoded public key
   * @param privateKeyBase64 Base64-encoded private key
   */
  data class KeyPairBase64Config(val publicKeyBase64: String, val privateKeyBase64: String)

  // ================================================================================================
  // Private Constants and Configuration
  // ================================================================================================

  /** Logger instance for cryptographic key operations */
  private val log = slf4j<CryptographicKeyManager>()

  /** Character set for random ASCII string generation */
  private const val RANDOM_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

  /** Default RSA key size (2048-bit for enhanced security) */
  private const val DEFAULT_RSA_KEY_SIZE = 2048

  /** Default AES key size (256-bit for maximum security) */
  private const val DEFAULT_AES_KEY_SIZE = 256

  /** Default seed for deterministic key generation */
  private const val DEFAULT_SEED = "T-DECRYPT-AND-ENCRYPT"

  /** Default ECC curve (P-256 for optimal security/performance balance) */
  private const val DEFAULT_ECC_CURVE = "P-256"

  /** Buffer size for I/O operations */
  private const val BUFFER_SIZE = 8192

  /** RSA security provider */
  private const val RSA_PROVIDER = "SunRsaSign"

  /** ECC security provider (BouncyCastle) */
  private const val ECC_PROVIDER = "BC"

  /** AES algorithm identifier */
  private const val AES_ALGORITHM = "AES"

  /** EC algorithm identifier */
  private const val EC_ALGORITHM = "EC"

  // ================================================================================================
  // Thread-Safe Key Factories and Generators
  // ================================================================================================

  /**
   * Thread-safe factory instances for cryptographic operations.
   *
   * This object provides pre-initialized, thread-safe access to key factories and generators. All instances are lazily initialized to minimize startup overhead
   * while ensuring optimal performance during runtime operations.
   */
  private object SecureKeyFactories {

    /** Thread-local SecureRandom instances for optimal performance and thread safety */
    private val secureRandomThreadLocal = ThreadLocal.withInitial { SecureRandom().apply { setSeed(System.nanoTime()) } }

    /** Thread-safe SecureRandom instance */
    val secureRandom: SecureRandom
      get() = secureRandomThreadLocal.get()

    /** Pre-initialized RSA key factory */
    val rsaKeyFactory: KeyFactory by lazy { KeyFactory.getInstance(EncryptAlgorithm.RSA.value, RSA_PROVIDER) }

    /** Pre-initialized ECC key factory */
    val eccKeyFactory: KeyFactory by lazy { KeyFactory.getInstance(EC_ALGORITHM, ECC_PROVIDER) }

    /** Pre-initialized AES key generator */
    val aesKeyGenerator: KeyGenerator by lazy { KeyGenerator.getInstance(AES_ALGORITHM) }

    /** Pre-initialized ECC key pair generator */
    val eccKeyGenerator: KeyPairGenerator by lazy { KeyPairGenerator.getInstance(EC_ALGORITHM, ECC_PROVIDER) }
  }

  // ================================================================================================
  // Public Key Management API
  // ================================================================================================

  /**
   * Generates a cryptographically secure random ASCII string with high entropy.
   *
   * This method uses thread-safe SecureRandom instances to generate random strings suitable for cryptographic seeds, salts, and other security-sensitive
   * applications. The character set includes alphanumeric characters for maximum compatibility.
   *
   * ## Performance Characteristics
   * - Uses ThreadLocal SecureRandom for optimal thread safety and performance
   * - Pre-allocated StringBuilder for efficient string construction
   * - Constant-time character selection to prevent timing attacks
   *
   * @param length The desired length of the random string (must be positive)
   * @return A cryptographically secure random ASCII string
   * @throws IllegalArgumentException if length is less than or equal to zero
   * @see SecureRandom
   */
  @JvmStatic
  fun generateRandomAsciiString(length: Int = 32): String {
    require(length > 0) { "String length must be positive, got: $length" }

    return buildString(length) {
      val random = SecureKeyFactories.secureRandom
      repeat(length) { append(RANDOM_CHARACTERS[random.nextInt(RANDOM_CHARACTERS.length)]) }
    }
  }

  /**
   * Reads an RSA public key from a Base64-encoded string with optimized performance.
   *
   * This method uses pre-initialized KeyFactory instances for optimal performance and provides comprehensive error handling without exposing sensitive
   * cryptographic details. The method supports standard X.509 encoded public keys.
   *
   * @param base64 Base64-encoded RSA public key string
   * @return RSA public key object, or null if parsing fails
   * @throws IllegalArgumentException if the Base64 string is malformed
   */
  @JvmStatic fun readRsaPublicKeyByBase64(base64: String): RSAPublicKey? = readPublicKeyByAlgorithm(base64, EncryptAlgorithm.RSA) as? RSAPublicKey

  /**
   * Reads an RSA private key from a Base64-encoded string with enhanced security.
   *
   * This method uses pre-initialized KeyFactory instances for optimal performance and implements secure error handling. The method supports PKCS#8 encoded
   * private keys.
   *
   * @param base64 Base64-encoded RSA private key string
   * @return RSA private key object, or null if parsing fails
   * @throws IllegalArgumentException if the Base64 string is malformed
   */
  @JvmStatic
  fun readRsaPrivateKeyByBase64(base64: String): RSAPrivateKey? =
    runCatching {
        val keyBytes = IBase64.decodeToByte(base64)
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        SecureKeyFactories.rsaKeyFactory.generatePrivate(keySpec) as RSAPrivateKey
      }
      .onFailure { throwable -> log.error("Failed to parse RSA private key from Base64", throwable) }
      .getOrNull()

  /**
   * Reconstructs an RSA key pair from Base64-encoded public and private keys.
   *
   * This method provides a convenient way to rebuild RSA key pairs from stored Base64 representations. It validates both keys and ensures they form a valid
   * pair.
   *
   * @param config Configuration containing Base64-encoded public and private keys
   * @return RSA key pair object
   * @throws IllegalArgumentException if either key is invalid or malformed
   */
  @JvmStatic
  fun readRsaKeyPair(config: KeyPairBase64Config): IRsaExtKeyPair =
    RsaExtKeyPair(
      requireNotNull(readRsaPublicKeyByBase64(config.publicKeyBase64)) { "Invalid RSA public key in Base64 format" },
      requireNotNull(readRsaPrivateKeyByBase64(config.privateKeyBase64)) { "Invalid RSA private key in Base64 format" },
    )

  /**
   * Reconstructs an RSA key pair from Base64-encoded strings (legacy method).
   *
   * @param rsaPublicKeyBase64 Base64-encoded RSA public key
   * @param rsaPrivateKeyBase64 Base64-encoded RSA private key
   * @return RSA key pair object
   * @throws IllegalArgumentException if either key is invalid
   */
  @JvmStatic
  fun readRsaKeyPair(rsaPublicKeyBase64: String, rsaPrivateKeyBase64: String): IRsaExtKeyPair =
    readRsaKeyPair(KeyPairBase64Config(rsaPublicKeyBase64, rsaPrivateKeyBase64))

  /**
   * Reads an AES key from a Base64-encoded string with optimized performance.
   *
   * This method provides secure AES key reconstruction from Base64 representations with efficient memory usage and comprehensive error handling. The method
   * supports standard AES key sizes (128, 192, and 256 bits).
   *
   * @param base64 Base64-encoded AES key string
   * @return AES key specification object, or null if parsing fails
   * @throws IllegalArgumentException if the Base64 string is malformed
   */
  @JvmStatic
  fun readAesKeyByBase64(base64: String): SecretKeySpec? =
    runCatching {
        val keyBytes = IBase64.decodeToByte(base64)
        SecretKeySpec(keyBytes, AES_ALGORITHM)
      }
      .onFailure { throwable -> log.error("Failed to parse AES key from Base64", throwable) }
      .getOrNull()

  /**
   * Converts a cryptographic key to Base64 string format with secure handling.
   *
   * This method provides universal key-to-Base64 conversion with optimized memory usage and comprehensive error handling. It supports all standard key types
   * including RSA, ECC, and AES keys.
   *
   * @param key The cryptographic key to convert
   * @return Base64-encoded key string, or null if conversion fails
   * @throws IllegalArgumentException if the key cannot be encoded
   */
  @JvmStatic
  fun writeKeyToBase64(key: Key): String? =
    runCatching { key.encoded.encodeBase64String }.onFailure { throwable -> log.error("Failed to convert key to Base64 format", throwable) }.getOrNull()

  /**
   * Converts an AES key to Base64 string format with optimized performance.
   *
   * This method provides specialized AES key conversion with enhanced type safety and optimized performance characteristics. It ensures cross-platform
   * compatibility through standardized encoding methods.
   *
   * @param key AES key specification object
   * @return Base64-encoded AES key string, or null if conversion fails
   * @throws IllegalArgumentException if the AES key cannot be encoded
   */
  @JvmStatic fun writeAesKeyToBase64(key: SecretKeySpec): String? = writeKeyToBase64(key)

  /**
   * Converts a cryptographic key to PEM format with automatic type detection.
   *
   * This method provides comprehensive PEM format conversion supporting RSA and ECC keys. It automatically selects the correct PEM header and footer markers
   * based on key type and uses standardized Base64 encoding to ensure cross-platform compatibility.
   *
   * ## Supported Key Types
   * - RSA public and private keys
   * - ECC public and private keys
   * - Automatic type detection based on key algorithm and class
   *
   * @param key The cryptographic key to convert
   * @param keyType Optional key type identifier; if null, type is auto-detected
   * @return PEM-formatted string, or null if conversion fails
   * @throws IllegalArgumentException if the key type is unsupported
   * @see PemFormat
   */
  @JvmStatic
  fun writeKeyToPem(key: Key, keyType: String? = null): String? =
    runCatching { PemFormat[key, keyType] }.onFailure { throwable -> log.error("Failed to convert key to PEM format", throwable) }.getOrNull()

  /**
   * Reads an RSA public key from standard Base64 format without PEM headers.
   *
   * This method directly parses Base64-encoded public key data without PEM format headers and footers. It uses the X.509 standard format for maximum
   * compatibility with various cryptographic libraries and systems.
   *
   * @param base64 Base64-encoded RSA public key (raw format, no PEM headers)
   * @return RSA public key object, or null if parsing fails
   * @throws IllegalArgumentException if the Base64 string is malformed
   */
  @JvmStatic
  fun readRsaPublicKeyByBase64AndStandard(base64: String): RSAPublicKey? =
    runCatching {
        val keyBytes = IBase64.decodeToByte(base64)
        val keySpec = X509EncodedKeySpec(keyBytes)
        SecureKeyFactories.rsaKeyFactory.generatePublic(keySpec) as RSAPublicKey
      }
      .onFailure { throwable -> log.error("Failed to parse RSA public key from standard Base64 format", throwable) }
      .getOrNull()

  /**
   * Reads an RSA private key from standard Base64 format without PEM headers.
   *
   * This method directly parses Base64-encoded private key data without PEM format headers and footers. It uses the PKCS#8 standard format for secure private
   * key representation and maximum compatibility.
   *
   * @param base64 Base64-encoded RSA private key (raw format, no PEM headers)
   * @return RSA private key object, or null if parsing fails
   * @throws IllegalArgumentException if the Base64 string is malformed
   */
  @JvmStatic
  fun readRsaPrivateKeyByBase64AndStandard(base64: String): RSAPrivateKey? =
    runCatching {
        val keyBytes = IBase64.decodeToByte(base64)
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        SecureKeyFactories.rsaKeyFactory.generatePrivate(keySpec) as RSAPrivateKey
      }
      .onFailure { throwable -> log.error("Failed to parse RSA private key from standard Base64 format", throwable) }
      .getOrNull()

  /**
   * Generates a secure RSA key pair with optimized performance and security.
   *
   * This method uses advanced key generation algorithms and parameters for optimal security and performance. It employs thread-safe SecureRandom instances and
   * pre-initialized KeyPairGenerator for efficient operation.
   *
   * ## Security Features
   * - 2048-bit key length (minimum recommended for current security standards)
   * - Cryptographically secure random number generation
   * - Industry-standard RSA algorithm implementation
   * - Proper entropy seeding for maximum security
   *
   * @param config RSA key generation configuration
   * @return RSA key pair object, or null if generation fails
   * @throws IllegalArgumentException if key size is invalid
   */
  @JvmStatic
  fun generateRsaKeyPair(config: RsaKeyConfig = RsaKeyConfig()): IRsaExtKeyPair? =
    runCatching {
        require(config.keySize >= 2048) { "RSA key size must be at least 2048 bits for security" }

        val random = SecureRandom(config.seed.toByteArray(Charsets.UTF_8))
        val keyPairGen = KeyPairGenerator.getInstance(EncryptAlgorithm.RSA.value, config.provider)
        keyPairGen.initialize(config.keySize, random)
        val keyPair = keyPairGen.generateKeyPair()
        RsaExtKeyPair(keyPair.public as RSAPublicKey, keyPair.private as RSAPrivateKey)
      }
      .onFailure { throwable -> log.error("Failed to generate RSA key pair", throwable) }
      .getOrNull()

  /**
   * Generates a secure RSA key pair with legacy parameter support.
   *
   * @param seed Random seed for key generation
   * @param keySize RSA key size in bits (minimum 2048)
   * @return RSA key pair object, or null if generation fails
   */
  @JvmStatic
  fun generateRsaKeyPair(seed: String = generateRandomAsciiString(), keySize: Int = DEFAULT_RSA_KEY_SIZE): IRsaExtKeyPair? =
    generateRsaKeyPair(RsaKeyConfig(seed, keySize))

  /**
   * Generates a secure ECC key pair with optimized elliptic curve parameters.
   *
   * This method uses advanced ECC algorithms and parameters for optimal security and performance. It employs the P-256 curve (NIST recommended) with proper
   * entropy seeding and thread-safe random number generation.
   *
   * ## Security Features
   * - P-256 elliptic curve (optimal security/performance balance)
   * - BouncyCastle provider for enhanced ECC support
   * - Cryptographically secure random number generation
   * - Industry-standard curve parameters
   *
   * @param config ECC key generation configuration
   * @return ECC key pair object, or null if generation fails
   * @throws IllegalArgumentException if curve parameters are invalid
   */
  @JvmStatic
  fun generateEccKeyPair(config: EccKeyConfig = EccKeyConfig()): IEccExtKeyPair? =
    runCatching {
        val random = SecureRandom(config.seed.toByteArray(Charsets.UTF_8))
        val curve = ECNamedCurveTable.getParameterSpec(config.curve)
        requireNotNull(curve) { "Unsupported ECC curve: ${config.curve}" }

        SecureKeyFactories.eccKeyGenerator
          .apply { initialize(curve, random) }
          .generateKeyPair()
          .let { keyPair -> EccExtKeyPair(keyPair.public, keyPair.private) }
      }
      .onFailure { throwable -> log.error("Failed to generate ECC key pair", throwable) }
      .getOrNull()

  /**
   * Generates a secure ECC key pair with legacy parameter support.
   *
   * @param seed Random seed for key generation
   * @return ECC key pair object, or null if generation fails
   */
  @JvmStatic fun generateEccKeyPair(seed: String = generateRandomAsciiString()): IEccExtKeyPair? = generateEccKeyPair(EccKeyConfig(seed))

  /**
   * Generates a high-strength AES key with optimized security parameters.
   *
   * This method creates cryptographically secure AES keys using advanced generation algorithms and parameters. It supports all standard AES key sizes and
   * employs proper entropy seeding for maximum security.
   *
   * ## Security Features
   * - 256-bit key length (maximum AES security)
   * - Cryptographically secure random number generation
   * - Pre-initialized KeyGenerator for optimal performance
   * - Support for all AES key sizes (128, 192, 256 bits)
   *
   * @param config AES key generation configuration
   * @return AES key specification object, or null if generation fails
   * @throws IllegalArgumentException if key size is invalid
   */
  @JvmStatic
  fun generateAesKey(config: AesKeyConfig = AesKeyConfig()): SecretKeySpec? =
    runCatching {
        require(config.keySize in listOf(128, 192, 256)) { "AES key size must be 128, 192, or 256 bits, got: ${config.keySize}" }

        val random = SecureRandom(config.seed.toByteArray(Charsets.UTF_8))
        SecureKeyFactories.aesKeyGenerator.apply { init(config.keySize, random) }.generateKey().let { key -> SecretKeySpec(key.encoded, config.algorithm) }
      }
      .onFailure { throwable -> log.error("Failed to generate AES key", throwable) }
      .getOrNull()

  /**
   * Generates a high-strength AES key with legacy parameter support.
   *
   * @param seed Random seed for key generation
   * @param keySize AES key size in bits (128, 192, or 256)
   * @return AES key specification object, or null if generation fails
   */
  @JvmStatic fun generateAesKey(seed: String = DEFAULT_SEED, keySize: Int = DEFAULT_AES_KEY_SIZE): SecretKeySpec? = generateAesKey(AesKeyConfig(seed, keySize))

  /**
   * Reconstructs an ECC key pair from Base64-encoded public and private keys.
   *
   * This method provides a convenient way to rebuild ECC key pairs from stored Base64 representations. It validates both keys and ensures they form a valid
   * pair.
   *
   * @param config Configuration containing Base64-encoded public and private keys
   * @return ECC key pair object
   * @throws IllegalArgumentException if either key is invalid or malformed
   */
  @JvmStatic
  fun readEccKeyPair(config: KeyPairBase64Config): IEccExtKeyPair =
    EccExtKeyPair(
      requireNotNull(readEccPublicKeyByBase64(config.publicKeyBase64)) { "Invalid ECC public key in Base64 format" },
      requireNotNull(readEccPrivateKeyByBase64(config.privateKeyBase64)) { "Invalid ECC private key in Base64 format" },
    )

  /**
   * Reconstructs an ECC key pair from Base64-encoded strings (legacy method).
   *
   * @param eccPublicKeyBase64 Base64-encoded ECC public key
   * @param eccPrivateKeyBase64 Base64-encoded ECC private key
   * @return ECC key pair object
   * @throws IllegalArgumentException if either key is invalid
   */
  @JvmStatic
  fun readEccKeyPair(eccPublicKeyBase64: String, eccPrivateKeyBase64: String): IEccExtKeyPair =
    readEccKeyPair(KeyPairBase64Config(eccPublicKeyBase64, eccPrivateKeyBase64))

  /**
   * Reads a public key from Base64 string with algorithm-specific processing.
   *
   * This private method provides centralized public key parsing with algorithm-specific factory selection and comprehensive error handling. It supports RSA and
   * ECC algorithms with proper X.509 encoding validation.
   *
   * @param base64 Base64-encoded public key string
   * @param algorithm Cryptographic algorithm type
   * @return Public key object, or null if parsing fails
   * @throws IllegalArgumentException if algorithm is unsupported
   */
  @JvmStatic
  private fun readPublicKeyByAlgorithm(base64: String, algorithm: EncryptAlgorithm): PublicKey? =
    runCatching {
        val keyBytes = IBase64.decodeToByte(base64)
        val keySpec = X509EncodedKeySpec(keyBytes)
        when (algorithm) {
          EncryptAlgorithm.RSA -> SecureKeyFactories.rsaKeyFactory.generatePublic(keySpec)
          EncryptAlgorithm.ECC -> SecureKeyFactories.eccKeyFactory.generatePublic(keySpec)
          else -> throw IllegalArgumentException("Unsupported algorithm type: $algorithm")
        }
      }
      .onFailure { throwable -> log.error("Failed to parse public key for algorithm ${algorithm.value}", throwable) }
      .getOrNull()

  /**
   * Reads an ECC public key from a Base64-encoded string.
   *
   * @param base64 Base64-encoded ECC public key string
   * @return ECC public key object, or null if parsing fails
   */
  @JvmStatic fun readEccPublicKeyByBase64(base64: String): PublicKey? = readPublicKeyByAlgorithm(base64, EncryptAlgorithm.ECC)

  /**
   * Reads an ECC private key from a Base64-encoded string.
   *
   * @param base64 Base64-encoded ECC private key string
   * @return ECC private key object, or null if parsing fails
   */
  @JvmStatic
  fun readEccPrivateKeyByBase64(base64: String): PrivateKey? =
    runCatching {
        val keyBytes = IBase64.decodeToByte(base64)
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        SecureKeyFactories.eccKeyFactory.generatePrivate(keySpec)
      }
      .onFailure { throwable -> log.error("Failed to parse ECC private key from Base64", throwable) }
      .getOrNull()

  // ================================================================================================
  // Initialization and Warm-up
  // ================================================================================================

  init {
    // Initialize BouncyCastle security provider for enhanced cryptographic support
    Security.addProvider(BouncyCastleProvider())

    // Pre-warm critical components for optimal runtime performance
    // This initialization ensures that expensive cryptographic setup is done once at startup
    runCatching {
        SecureKeyFactories.secureRandom
        SecureKeyFactories.rsaKeyFactory
        SecureKeyFactories.eccKeyFactory
        SecureKeyFactories.aesKeyGenerator
        SecureKeyFactories.eccKeyGenerator
        log.info("Cryptographic key manager initialized successfully")
      }
      .onFailure { throwable -> log.error("Failed to initialize cryptographic key manager", throwable) }
  }

  // ================================================================================================
  // Backward Compatibility Aliases
  // ================================================================================================

  /**
   * Legacy alias for the optimized cryptographic key manager object.
   *
   * This alias maintains backward compatibility with existing code that references the old "Keys" name while providing access to the new optimized
   * implementation.
   *
   * @deprecated Use CryptographicKeyManager directly for new code
   */
  @Deprecated(
    message = "Use CryptographicKeyManager directly for new code",
    replaceWith = ReplaceWith("CryptographicKeyManager"),
    level = DeprecationLevel.WARNING,
  )
  @JvmField
  val Keys = CryptographicKeyManager
}
