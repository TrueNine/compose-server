package com.example.demo

/** 这个文件包含各种代码问题，用于测试改进的错误检测功能 */

// 类名应该使用 PascalCase
class badClassName {

  // 常量应该使用 UPPER_SNAKE_CASE
  companion object {
    const val badConstantName = "value"
    const val GOOD_CONSTANT_NAME = "good"
  }

  // 未使用的私有字段
  private val unusedField = "unused"

  // 函数名应该使用 camelCase
  fun BadFunctionName() {
    // 变量名应该使用 camelCase
    val BadVariableName = "test"

    // 未使用的变量
    val unusedVariable = "unused"

    // 潜在的空指针异常
    val nullableString: String? = null
    val length = nullableString.length // 这里会有警告

    println("Function executed")
  }

  // 未使用的私有函数
  private fun unusedPrivateFunction() {
    println("This function is never called")
  }

  // 参数未使用
  fun functionWithUnusedParameter(unusedParam: String) {
    println("Function with unused parameter")
  }

  // 返回值未使用
  fun functionWithUnusedReturn(): String {
    return "unused return value"
  }

  // 不必要的类型转换
  fun unnecessaryCast() {
    val number = 42
    val stringNumber = number.toString() as String // 不必要的转换
  }

  // 可以简化的代码
  fun canBeSimplified(): Boolean {
    val condition = true
    if (condition == true) { // 可以简化为 if (condition)
      return true
    } else {
      return false
    }
  }

  // 魔法数字
  fun magicNumbers() {
    val result = 42 * 3.14159 // 应该使用常量
    println(result)
  }

  // 过长的函数（代码质量问题）
  fun tooLongFunction() {
    println("Line 1")
    println("Line 2")
    println("Line 3")
    println("Line 4")
    println("Line 5")
    println("Line 6")
    println("Line 7")
    println("Line 8")
    println("Line 9")
    println("Line 10")
    // ... 更多行
  }
}

// 空类（可能的设计问题）
class EmptyClass {
  // 这个类是空的，可能有设计问题
}

// 单例模式实现不当
class BadSingleton {
  companion object {
    var instance: BadSingleton? = null // 线程不安全

    fun getInstance(): BadSingleton {
      if (instance == null) {
        instance = BadSingleton()
      }
      return instance!!
    }
  }
}

// 未使用的顶级函数
private fun unusedTopLevelFunction() {
  println("This top-level function is never used")
}

// 函数参数过多
fun tooManyParameters(param1: String, param2: Int, param3: Boolean, param4: Double, param5: Float, param6: Long, param7: Byte, param8: Short) {
  // 函数参数过多，应该考虑使用数据类
  println("Too many parameters")
}
