package net.yan100.compose.core.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.typing.cert.DisTyping
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


open class AB {
    var typ: DisTyping? = null
}

@SpringBootTest
class AnyTypingDeserializerTest {
    @Autowired
    lateinit var mapper: ObjectMapper

    @Test
    fun `test deserializer`() {
        val d = DisTyping.EYE
        val dd = AB().apply {
            typ = d
        }
        val json = mapper.writeValueAsString(dd)
        val cc = mapper.readValue(json, AB::class.java)
        val ff = mapper.readValue("{\"typ\":1}", AB::class.java)
        val ee = mapper.readValue("{\"typ\":\"1\"}", AB::class.java)

        println(json)
    }
}
