package io.github.truenine.composeserver.oss

@Deprecated("不建议使用 builder API")
class FileArgsBuilder(private var internalFileArgs: FileArgs = FileArgs()) {
  fun dir(dir: String): FileArgsBuilder {
    internalFileArgs.dir = dir
    return this
  }

  fun fileName(fileName: String): FileArgsBuilder {
    internalFileArgs.fileName = fileName
    return this
  }

  fun mimeType(mimeType: String): FileArgsBuilder {
    internalFileArgs.mimeType = mimeType
    return this
  }

  fun size(size: Long): FileArgsBuilder {
    internalFileArgs.size = size
    return this
  }

  fun build(): FileArgs {
    return internalFileArgs
  }
}
