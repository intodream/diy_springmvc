package dream.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)  //类上面注册
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DreamController {
    String value() default "";
}
