package net.yan100.compose.annotations

import java.lang.annotation.Inherited

/**
 * # 接口返回为脱敏后的数据
 *
 * @see [net.yan100.compose.domain.sensitive.ISensitivity]
 */
@Inherited @Retention @MustBeDocumented @Target(AnnotationTarget.FUNCTION) annotation class SensitiveResponse
