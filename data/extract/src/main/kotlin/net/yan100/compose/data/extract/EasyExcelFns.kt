package net.yan100.compose.data.extract

import com.alibaba.excel.EasyExcel
import com.alibaba.excel.context.AnalysisContext
import com.alibaba.excel.read.builder.ExcelReaderBuilder
import com.alibaba.excel.read.listener.ReadListener
import java.util.concurrent.CopyOnWriteArrayList
import org.springframework.web.multipart.MultipartFile

inline fun <reified T> MultipartFile.readExcelList(readFn: (readerBuilder: ExcelReaderBuilder) -> Unit = { r -> r.sheet().doRead() }): List<T> {
  val dataList = CopyOnWriteArrayList<T>()

  val e =
    try {
      EasyExcel.read(
        inputStream,
        T::class.java,
        object : ReadListener<T> {
          override fun invoke(data: T?, context: AnalysisContext?) {
            data?.let { dataList += it }
          }

          override fun doAfterAllAnalysed(context: AnalysisContext?) {
            // do nothing
          }
        },
      )
    } catch (ex: Throwable) {
      ex.printStackTrace()
      null
    }
  if (null != e) readFn(e)
  return dataList
}
