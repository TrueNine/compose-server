package net.yan100.compose.client.ts

import net.yan100.compose.meta.annotations.client.Api
import org.springframework.web.bind.annotation.*

private typealias Erv = List<String>
private typealias MList<E> = List<E>

@Api
@RequestMapping("v1/ts/tset")
@RestController
class TsParameterTestController {
  @Api
  @PostMapping("inputNullParameter")
  fun inputNullParameter(@RequestBody data: Map<String, Map<String?, List<Boolean?>>>): Map<String, List<String>?>? {
    return null
  }

  @Api
  @PostMapping("outputGenericList")
  fun outputGenericList(): MList<Erv>? {
    return null
  }

  @Api
  @PutMapping("outputAlias")
  fun outputAlias(): Erv? {
    return null
  }

  @Api
  @GetMapping("outputVoidOrUnit")
  fun outputVoidOrUnit() {

  }
}
