package pico.erp.restapi.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheControl {

  /**
   * The maximum amount of time, in seconds, that this content will be considered fresh.
   */
  int maxAge() default 0;

  /**
   * The <code>cache-control</code> policies map apply map the response.
   *
   * @see CachePolicy
   */
  CachePolicy[] policy() default {CachePolicy.PUBLIC};

  /**
   * The maximum amount of time, in seconds, that this content will be considered fresh only for
   * shared caches (e.g., proxy) caches.
   */
  int sharedMaxAge() default -1;

}
