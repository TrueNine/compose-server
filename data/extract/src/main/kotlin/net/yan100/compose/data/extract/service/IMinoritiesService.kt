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
package net.yan100.compose.data.extract.service

/**
 * ## 中国 56 民族
 *
 * @author TrueNine
 * @since 2024-01-11
 */
interface IMinoritiesService {
  companion object {
    @JvmStatic
    val MINORITIES: List<String> =
      listOf(
        "汉",
        "蒙古",
        "回",
        "藏",
        "维吾尔",
        "苗",
        "彝",
        "壮",
        "布依",
        "朝鲜",
        "满",
        "侗",
        "瑶",
        "白",
        "土家",
        "哈尼",
        "哈萨克",
        "傣",
        "黎",
        "傈僳",
        "佤",
        "畲",
        "高山",
        "拉祜",
        "水",
        "东乡",
        "纳西",
        "景颇",
        "柯尔克孜",
        "土",
        "达斡尔",
        "仫佬",
        "羌",
        "布朗",
        "撒拉",
        "毛难",
        "仡佬",
        "锡伯",
        "阿昌",
        "普米",
        "塔吉克",
        "怒",
        "乌孜别克",
        "俄罗斯",
        "鄂温克",
        "德昂",
        "保安",
        "裕固",
        "京",
        "塔塔尔",
        "独龙",
        "鄂伦春",
        "赫哲",
        "门巴",
        "珞巴",
        "基诺",
      )

    @JvmStatic val MINORITIES_Z = net.yan100.compose.data.extract.service.IMinoritiesService.Companion.MINORITIES.map { it + "族" }
  }

  fun findAllMinorities(): List<String> =
      net.yan100.compose.data.extract.service.IMinoritiesService.Companion.MINORITIES

  fun findAllMinoritiesZ(): List<String> =
      net.yan100.compose.data.extract.service.IMinoritiesService.Companion.MINORITIES_Z
}
