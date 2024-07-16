/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.datacommon.dataextract.extensionfunctions

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
