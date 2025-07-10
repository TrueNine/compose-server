package net.yan100.compose.holders

import org.springframework.context.ApplicationEventPublisher

/**
 * # spring 事件发布者 Holder
 *
 * @author TrueNine
 * @since 2024-06-02
 */
object EventPublisherHolder : AbstractThreadLocalHolder<ApplicationEventPublisher>()
