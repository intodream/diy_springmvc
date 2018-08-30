package dream.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)  //字段标注
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DreamAuwowired {
    String value() default "";
}

