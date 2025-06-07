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

    @JvmStatic val MINORITIES_Z = MINORITIES.map { it + "族" }
  }

  fun findAllMinorities(): List<String> = MINORITIES

  fun findAllMinoritiesZ(): List<String> = MINORITIES_Z
}
