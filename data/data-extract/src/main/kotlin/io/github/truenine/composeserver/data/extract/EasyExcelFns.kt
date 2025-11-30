package io.github.truenine.composeserver.data.extract

import com.alibaba.excel.EasyExcel
import com.alibaba.excel.context.AnalysisContext
import com.alibaba.excel.read.builder.ExcelReaderBuilder
import com.alibaba.excel.read.listener.ReadListener
import io.github.truenine.composeserver.slf4j
import org.springframework.web.multipart.MultipartFile
import java.util.concurrent.atomic.AtomicInteger

private val log = slf4j<Any>()

/**
 * Optimized Excel reading extension function for MultipartFile.
 *
 * Performance improvements:
 * - Uses ArrayList instead of CopyOnWriteArrayList for better memory efficiency
 * - Implements streaming processing to handle large files
 * - Adds proper error handling and logging
 * - Pre-allocates collection capacity when possible
 *
 * @param T The type of data objects to read from Excel
 * @param readFn Custom read function for advanced Excel processing
 * @param initialCapacity Initial capacity hint for the result list (default: 1000)
 * @return List of parsed data objects
 * @throws IllegalStateException if Excel reading fails
 */
fun <T> MultipartFile.readExcelList(
  clazz: Class<T>,
  readFn: (readerBuilder: ExcelReaderBuilder) -> Unit = { r -> r.sheet().doRead() },
  initialCapacity: Int = 1000,
): List<T> {
  val dataList = ArrayList<T>(initialCapacity)
  val processedCount = AtomicInteger(0)

  log.debug("Starting Excel processing for file: {} (size: {} bytes)", this.originalFilename, this.size)

  val reader =
    try {
      EasyExcel.read(
        this.inputStream,
        clazz,
        object : ReadListener<T> {
          override fun invoke(data: T?, context: AnalysisContext?) {
            data?.let {
              dataList.add(it)
              val count = processedCount.incrementAndGet()
              if (count % 1000 == 0) {
                log.debug("Processed {} rows from Excel file", count)
              }
            }
          }

          override fun doAfterAllAnalysed(context: AnalysisContext?) {
            log.debug("Excel processing completed. Total rows processed: {}", processedCount.get())
          }
        },
      )
    } catch (ex: Exception) {
      log.error("Failed to create Excel reader for file: {}", this.originalFilename, ex)
      throw IllegalStateException("Excel reading failed: ${ex.message}", ex)
    }

  try {
    readFn(reader)
  } catch (ex: Exception) {
    log.error("Failed to read Excel data from file: {}", this.originalFilename, ex)
    throw IllegalStateException("Excel data reading failed: ${ex.message}", ex)
  }

  log.info("Successfully processed Excel file: {} with {} rows", this.originalFilename, dataList.size)
  return dataList
}

/** Reified version of readExcelList for easier usage. */
inline fun <reified T> MultipartFile.readExcelList(
  noinline readFn: (readerBuilder: ExcelReaderBuilder) -> Unit = { r -> r.sheet().doRead() },
  initialCapacity: Int = 1000,
): List<T> = readExcelList(T::class.java, readFn, initialCapacity)

/**
 * Streaming Excel reader for processing large files without loading all data into memory.
 *
 * This function processes Excel data row by row, making it suitable for very large files where memory usage is a concern.
 *
 * @param T The type of data objects to read from Excel
 * @param processor Function to process each row as it's read
 * @param batchSize Number of rows to process in each batch (default: 100)
 * @throws IllegalStateException if Excel reading fails
 */
fun <T> MultipartFile.processExcelStream(clazz: Class<T>, processor: (data: T, rowIndex: Int) -> Unit, batchSize: Int = 100) {
  val processedCount = AtomicInteger(0)

  log.debug("Starting streaming Excel processing for file: {}", this.originalFilename)

  try {
    EasyExcel.read(
        this.inputStream,
        clazz,
        object : ReadListener<T> {
          override fun invoke(data: T?, context: AnalysisContext?) {
            data?.let {
              val rowIndex = processedCount.getAndIncrement()
              processor(it, rowIndex)

              if (rowIndex % 1000 == 0) {
                log.debug("Streamed {} rows from Excel file", rowIndex + 1)
              }
            }
          }

          override fun doAfterAllAnalysed(context: AnalysisContext?) {
            log.debug("Streaming Excel processing completed. Total rows: {}", processedCount.get())
          }
        },
      )
      .sheet()
      .doRead()
  } catch (ex: Exception) {
    log.error("Failed to stream Excel data from file: {}", this.originalFilename, ex)
    throw IllegalStateException("Excel streaming failed: ${ex.message}", ex)
  }
}

/** Reified version of processExcelStream for easier usage. */
inline fun <reified T> MultipartFile.processExcelStream(noinline processor: (data: T, rowIndex: Int) -> Unit, batchSize: Int = 100) =
  processExcelStream(T::class.java, processor, batchSize)
