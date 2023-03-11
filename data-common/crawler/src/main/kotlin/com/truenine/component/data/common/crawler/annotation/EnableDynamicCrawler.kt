package com.truenine.component.data.common.crawler.annotation

import java.lang.annotation.Inherited

/**
 * 一个用于在极端情况下
 * <br/>
 * 比如没有一个page是支持动态的来启动动态
 * <br/>
 * 这在比如spring的执行环境下会比较有效
 * <br/>
 * 假设没有任何一个动态注解注入到容器，则不会开启动态驱动池，现在则可以使用此注解
 */
@Inherited
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnableDynamicCrawler
