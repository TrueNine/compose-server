import net.yan100.compose.core.encrypt.PemFormat
import org.junit.jupiter.api.Test
import kotlin.test.DefaultAsserter.assertEquals

class PemFormatTest {

    @Test
    fun `test PemFormat`() {
        val key = "-----BEGIN RSA PRIVATE KEY-----\nMIICXQIBAAKBgQDo56Mh0tFwuxa8ZSP1L1M0eWtU+1G+1tWp2rJ4p8k8R83a ...\n-----END RSA PRIVATE KEY-----"
        val pemFormat = PemFormat(key)

        assertEquals("未识别出参数", "RSA PRIVATE KEY", pemFormat.schema)
    }

    @Test
    fun `test PemFormat with invalid key`() {
        val key = "-----BEGIN CERTIFICATE-----\nMIICXQIBAAKBgQDo56Mh0tFwuxa8ZSP1L1M0eWtU+1G+1tWp2rJ4p8k8R83a ...\n-----END CERTIFICATE-----"
        PemFormat(key)
    }

    @Test
    fun testParseBase64() {
        val base64 = "base64string1241251251252362374575668679679679vvwsetgwetweywy262362737fsdfsehrejertjrtyj"
        val keyType = "rsa private key"
        val result = PemFormat.base64ToPem(base64, keyType)
        val pem = PemFormat(result)
        // Call the function to be tested and check the result
        println(result)
    }
}
