package net.yan100.compose.client.templates

object UtilsTemplate {
  fun renderExecutor() = """
type HTTPMethod = 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH' | 'HEAD' | 'OPTIONS' | 'TRACE'
type BodyType = 'json' | 'form'
export type Executor = (requestOptions: {
  readonly uri: `/${'$'}{string}`
  readonly method: HTTPMethod
  readonly headers?: {readonly [key: string]: string}
  readonly body?: unknown
  readonly bodyType?: BodyType
}) => Promise<unknown>
    """.trimIndent().plus("\n")

}
