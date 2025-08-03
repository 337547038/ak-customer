package customer.config;

import java.lang.annotation.*;

/**
 * 用于权限校验，在服务层引用
 * 需要同时拥有permission1和permission2权限才能执行的方法
 * PermissionCheck( "permission2"}, logical = Logical.AND)
 * 只需拥有permission3或permission4任意一个权限即可执行的方法
 * PermissionCheck({"permission3", "permission4"})
 */
@Target(ElementType.METHOD) // 作用于方法
@Retention(RetentionPolicy.RUNTIME) // 运行时保留
@Documented
public @interface PermissionCheck {
    /**
     * 所需权限标识
     */
    String[] value() default {};

    /**
     * 逻辑关系：AND（需要全部权限）或 OR（只需任意一个权限）
     */
    Logical logical() default Logical.AND;

    enum Logical {
        AND, OR
    }
}
