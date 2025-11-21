package io.github.truenine.composeserver.data.extract.service

/**
 * Chinese 56 ethnic groups.
 *
 * @author TrueNine
 * @since 2024-01-11
 */
interface IMinoritiesService {
  companion object {
    @JvmStatic
    val MINORITIES: List<String> =
      listOf(
        "Han",
        "Zhuang",
        "Hui",
        "Manchu",
        "Uyghur",
        "Miao",
        "Yi",
        "Tujia",
        "Tibetan",
        "Mongol",
        "Dong",
        "Yao",
        "Bai",
        "Korean",
        "Hani",
        "Li",
        "Kazakh",
        "Dai",
        "Lisu",
        "Va",
        "She",
        "Gaoshan",
        "Lahu",
        "Shui",
        "Dongxiang",
        "Naxi",
        "Jingpo",
        "Kirgiz",
        "Tu",
        "Daur",
        "Mulao",
        "Qiang",
        "Blang",
        "Salar",
        "Maonan",
        "Gelao",
        "Xibe",
        "Achang",
        "Pumi",
        "Tajik",
        "Nu",
        "Uzbek",
        "Russian",
        "Ewenki",
        "Deang",
        "Bonan",
        "Yugur",
        "Jing",
        "Tatar",
        "Derung",
        "Oroqen",
        "Hezhe",
        "Monba",
        "Lhoba",
        "Jino",
      )

    @JvmStatic val MINORITIES_Z = MINORITIES.map { "$it ethnicity" }
  }

  fun findAllMinorities(): List<String> = MINORITIES

  fun findAllMinoritiesZ(): List<String> = MINORITIES_Z
}
