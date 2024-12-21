export type Executor = (args: {
readonly uri: string
readonly method: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH' | 'HEAD'
readonly headers?: {readonly [key: string]: string}
readonly body?: unknown
}) => Promise${"<unknown>"}
