package net.yan100.compose.domain

interface IChinaName {
  companion object {
    @JvmStatic
    operator fun get(name: String): IChinaName = DefaultChinaName(name)

    @JvmStatic
    fun splitName(name: String): Pair<String, String> {
      val cName = name.trim()
      check(cName.length in 2..4) { "姓名 $cName 长度不符合要求" }
      return if (name.length == 2) Pair(name.substring(0, 1), name.substring(1))
      else if (name.length == 3) Pair(name.substring(0, 1), name.substring(1))
      else if (name.length == 4 && name[2] == ' ')
        Pair(name.substring(0, 2), name.substring(3))
      else if (name.length == 4) Pair(name.substring(0, 2), name.substring(2))
      else error("姓名 $cName 格式不符合要求")
    }
  }

  private class DefaultChinaName(override val fullName: String) : IChinaName {
    private val f: String
    private val l: String

    init {
      val (f, l) = splitName(fullName)
      this.f = f
      this.l = l
    }

    override val firstName: String = f
    override val lastName: String = l
  }

  val fullName: String
  val firstName: String
  val lastName: String
}
