package top.iotequ.framework.util;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CgTableAnnotation {
    public String name();
    public String title();
    public String join() default "";
    public String pk() default "id";
    public String entityPk() default "id";
    public String module();
    public String generatorName() default "";
    public String baseUrl();
    public boolean hasLicence() default false;
    public int trialLicence() default -1;
    public int trialDays() default -1;
    public String pkType() default "String";
    public String parentEntityField() default "";
    public String pkKeyType() default "0";
}