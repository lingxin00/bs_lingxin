package org.example.bs_lingxin.framework.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器
 *
 * 作用：实体上标注了 @TableField(fill = FieldFill.INSERT) 的 createTime、
 *       @TableField(fill = FieldFill.INSERT_UPDATE) 的 updateTime，
 *       会在 insert / update 时自动写入当前时间，业务代码不用手动 set。
 * 注意：只有在容器里注册了 MetaObjectHandler，@TableField 的 fill 才会真正生效，
 *       否则这两个字段插入后一直是 NULL。
 */
@Component
public class MybatisMetaObjectHandler implements MetaObjectHandler {

    private static final String CREATE_TIME = "createTime";
    private static final String UPDATE_TIME = "updateTime";

    /**
     * 新增时填充：createTime、updateTime 同时写入当前时间
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        // strictInsertFill 只在字段有 fill = INSERT / INSERT_UPDATE 注解时才填，避免误伤
        this.strictInsertFill(metaObject, CREATE_TIME, LocalDateTime.class, now);
        this.strictInsertFill(metaObject, UPDATE_TIME, LocalDateTime.class, now);
    }

    /**
     * 修改时填充：只刷新 updateTime，createTime 保持不变
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, UPDATE_TIME, LocalDateTime.class, LocalDateTime.now());
    }
}
