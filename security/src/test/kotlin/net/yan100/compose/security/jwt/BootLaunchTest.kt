package net.yan100.compose.security.jwt

import net.yan100.compose.security.SecurityEntrance
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext

@SpringBootTest(classes = [SecurityEntrance::class])
class BootLaunchTest {
    @Autowired
    lateinit var ctx: ApplicationContext

    @Test
    fun `test launch`() {
        println(ctx)
    }
}
