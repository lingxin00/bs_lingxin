package org.example.bs_lingxin.framework.config.pool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 操作日志线程池
 *
 * 日志任务是「短、多、丢了就查不到」的类型，核心线程常驻、最大线程为核心线程的 2 倍，
 * 队列给足，队列满后由调用线程兜底执行，宁可让请求慢一点也不丢日志。
 * */
@Configuration
public class AsyncConfig {

    @Bean("logExecutor")
    public Executor logExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(15);       // 核心线程数：常驻线程，空闲也不回收，撑住日常写库量
        executor.setMaxPoolSize(30);        // 最大线程数：核心线程的 2 倍，队列满后才扩容到 30 个
        executor.setQueueCapacity(500);     // 阻塞队列容量：核心线程忙时先排队，最多排 500 个日志任务
        executor.setKeepAliveSeconds(60);   // 非核心线程空闲 60 秒后回收
        executor.setThreadNamePrefix("log-thread-");    // 线程名称前缀，打印日志的时候方便区分是日志线程
        // 队列也满了就由调用线程（业务线程）自己执行；默认 AbortPolicy 会抛异常直接丢任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 应用关闭时先处理完队列中的日志任务再退出
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(10);
        executor.initialize();          // 初始化线程池，提前创建核心线程
        return executor;
    }
}
