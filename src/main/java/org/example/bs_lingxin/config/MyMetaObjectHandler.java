package org.example.bs_lingxin.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * MyBatis‑Plus 提供的元对象处理器
 * 自动赋值创造时间和更新时间
 * */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

//    执行insert新增的时候触发
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject,"createTime", LocalDateTime::now,LocalDateTime.class);
        this.strictInsertFill(metaObject,"updateTime", LocalDateTime::now,LocalDateTime.class);
    }

//    执行update修改的时候触发
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject,"updateTime", LocalDateTime::now,LocalDateTime.class);
    }
}
