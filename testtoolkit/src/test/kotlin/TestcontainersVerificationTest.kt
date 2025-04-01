import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.junit.jupiter.api.Assertions.assertTrue
import org.slf4j.LoggerFactory

class TestcontainersVerificationTest {
    private val logger = LoggerFactory.getLogger(TestcontainersVerificationTest::class.java)
    
    @Test
    fun `verify testcontainers is working`() {
        logger.info("开始测试 Testcontainers")
        
        GenericContainer("alpine:3.19.1").apply {
            withCommand("sh", "-c", "echo 'Hello, Testcontainers!' && sleep 1")
        }.use { container ->
            logger.info("正在启动测试容器...")
            container.start()
            
            logger.info("容器状态: ${container.isRunning}")
            assertTrue(container.isRunning, "容器应该处于运行状态")
            
            val logs = container.logs
            logger.info("容器日志: $logs")
            
            logger.info("测试完成")
        }
    }
} 