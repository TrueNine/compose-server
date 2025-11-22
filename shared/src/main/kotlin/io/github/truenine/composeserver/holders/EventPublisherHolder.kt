package io.github.truenine.composeserver.holders

import org.springframework.context.ApplicationEventPublisher

/**
 * # Spring application event publisher holder
 *
 * Provides a thread-local holder for Spring's ApplicationEventPublisher.
 *
 * @author TrueNine
 * @since 2024-06-02
 */
object EventPublisherHolder : AbstractThreadLocalHolder<ApplicationEventPublisher>()
