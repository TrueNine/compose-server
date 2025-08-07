package io.github.truenine.composeserver.ksp.meta.annotations

import java.lang.annotation.Inherited

/**
 * ## 启用程序自管理
 * > 将元素的控制权移交，由其他自管理机制进行处理
 *
 * 告知编译器该元素不应当手动管理，而是其他诸如框架或后置处理，不应由用户进行干预。 例如：数据库元属性字段应当交由数据库自动生成或后置生成，而不建议在业务逻辑层进行手动控制，这些其中包括
 * - 主键
 * - 自生成订单号
 * - 逻辑删除字段
 * - 表创建时间
 * - 表修改时间
 *
 * 等的一系列数据库元数据。
 *
 * @author TrueNine
 * @since 2024-12-02
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
@Inherited
annotation class MetaAutoManagement
