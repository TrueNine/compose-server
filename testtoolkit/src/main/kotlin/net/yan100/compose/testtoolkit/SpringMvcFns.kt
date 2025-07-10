package net.yan100.compose.testtoolkit

import org.springframework.mock.web.MockMultipartFile

fun MockMultipartFile.copy(
  name: String = this.name,
  originalFilename: String = this.originalFilename,
  contentType: String? = this.contentType,
  content: ByteArray = this.inputStream.readAllBytes(),
): MockMultipartFile = MockMultipartFile(name, originalFilename, contentType, content)
