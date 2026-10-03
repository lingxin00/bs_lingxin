package org.example.bs_lingxin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;


@Configuration
public class AsyncConfig {
    @Bean("logExecutor")
    public Executor logExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);    // 核心线程数：常驻线程，空闲也不会被回收
        executor.setMaxPoolSize(5);      // 最大线程数：线程池最多能创建5个工作线程
        executor.setQueueCapacity(100);     // 阻塞队列容量：任务超过核心线程数量，先放到队列，队列最多存100个任务
        executor.setThreadNamePrefix("log-thread-");    // 线程名称前缀，打印日志的时候方便区分是日志线程
        executor.initialize();          // 初始化线程池，提前创建核心线程
        return executor;
    }
}
