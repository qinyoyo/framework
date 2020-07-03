package top.iotequ.framework.util;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CgFieldAnnotation {
    public String name();
    public String expression() default ""; // mysql 不允许别名为查询条件，使用表达式
    public String jdbcType() default "VARCHAR";
    public String format() default "@";
    public boolean nullable() default true;
    public boolean multiple() default true;
}