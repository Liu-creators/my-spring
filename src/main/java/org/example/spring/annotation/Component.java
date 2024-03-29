package org.example.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * -03/28-23:33
 * -@Component注解
 */
@Target(ElementType.TYPE) // 接口、类
@Retention(RetentionPolicy.RUNTIME) // 运行时加载
public @interface Component {
    String value() default "";
}
