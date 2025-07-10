package io.github.truenine.composeserver.annotations

import java.lang.annotation.Inherited

/**
 * # 接口返回为脱敏后的数据
 *
 * @see [io.github.truenine.composeserver.domain.sensitive.ISensitivity]
 */
@Inherited @Retention @MustBeDocumented @Target(AnnotationTarget.FUNCTION) annotation class SensitiveResponse
