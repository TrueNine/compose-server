package net.yan100.compose.core.annotations;


import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.cors.CorsConfiguration;

import java.lang.annotation.*;

/**
 * 开启全部跨域
 *
 * @author TrueNine
 * @since 2022-12-10
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({
  CorsConfiguration.class
})
@ConditionalOnWebApplication
public @interface EnableAllCrossOrigin {
}
