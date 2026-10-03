package org.example.bs_lingxin.config;

import java.lang.annotation.*;

// 注解只能写在方法上
@Target(ElementType.METHOD)
// 运行时保留，AOP切面才能反射读取这个注解信息
@Retention(RetentionPolicy.RUNTIME)
// 生成Java文档时保留注解
@Documented
/*自定义注解*/
public @interface operation {
    /** 操作模块，如：商品管理 */
    String moudle() default "";
    /** 操作类型，如：ADD, UPDATE, DELETE */
    String type() default "";
    /** 操作描述，如：新增商品 */
    String desc() default "";

}
