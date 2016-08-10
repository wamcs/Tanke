package com.lptiyu.tanke.permission;

/**
 * @author : xiaoxiaoda
 * date: 16-5-19
 * email: wonderfulifeel@gmail.com
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * this annotation
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetMethod {
  /**
   * this sets which target method to be invoked
   * when permission is admitted by user
   *
   * @return
   */
  int requestCode() default 0;
}
