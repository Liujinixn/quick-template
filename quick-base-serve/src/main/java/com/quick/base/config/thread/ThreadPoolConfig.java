package com.quick.base.config.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * 线程池配置
 *
 * @author Liujinxin
 */
@Slf4j
@Configuration
public class ThreadPoolConfig {

	/*
	案例：
	@Autowired
	ExecutorService dataProcessingThreadPool;

	// 1. 等待线程完成结束案例：
	CompletableFuture<?>[] cfs = new CompletableFuture[5];  // 设置预计完成的线程任务
	int index = 0;
	for (LaoUser laoUser : laoUsers) {
		CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
			// 任务内容
			// ...

		}, dataProcessingThreadPool);
		cfs[index++] = cf;
	}
	CompletableFuture.allOf(cfs).join();


	// 2. 不等待线程结束案例
	dataProcessingThreadPool.execute(() -> {
		// 任务内容
		// ...
	});

	*/

    /**
     * 数据处理线程池
     */
    @Bean
    public ExecutorService dataProcessingThreadPool() {
        ThreadFactory factory = new ThreadFactoryBuilder()
                .setUncaughtExceptionHandler((t, e) -> log.error(t.getName() + " excute error:", e))
                .setNameFormat("dataProcessing-pool-%d").build();
        int corePoolSize = 2;
        int maxPoolSize = 10;
        long keepAliveTime = 5;
        ExecutorService pool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.MINUTES,
                new ArrayBlockingQueue<Runnable>(128), factory, new ThreadPoolExecutor.AbortPolicy());
        return pool;
    }

}
