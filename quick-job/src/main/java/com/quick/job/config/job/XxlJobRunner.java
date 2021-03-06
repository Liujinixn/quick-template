package com.quick.job.config.job;

import com.alibaba.fastjson.JSON;
import com.quick.common.utils.collection.CollectionsUtil;
import com.quick.common.utils.request.HttpClientUtil;
import com.quick.job.annotation.CronJob;
import com.quick.job.annotation.JobHandlers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 项目启动自动触发将 job handler 注入到xxl-job服务中
 *
 * @author Liujinxin
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "xxl.job", name = "enable", havingValue = "true", matchIfMissing = true)
public class XxlJobRunner implements ApplicationRunner {

    @Resource
    private ApplicationContext applicationContext;

    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    @Value("${xxl.job.executor.jobGroupId}")
    private String jobGroupId;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            long millisStart = System.currentTimeMillis();
            ArrayList<String> joblist = new ArrayList<>();
            // 遍历每个有注解 CronJob 的类
            Map<String, Object> controllers = applicationContext.getBeansWithAnnotation(JobHandlers.class);
            for (Map.Entry<String, Object> entry : controllers.entrySet()) {
                Object value = entry.getValue();
                Class<?> aClass = AopUtils.getTargetClass(value);
                // 下行注释的代码，可以获取当前对象声明的注解获取到注解后，还可以获取注解中的属性值
                // JobHandlers jobHandlers = aClass.getDeclaredAnnotation(JobHandlers.class);
                Method[] methods = aClass.getDeclaredMethods();
                for (Method method : methods) {
                    CronJob cronJob = method.getAnnotation(CronJob.class);
                    if (cronJob == null) {
                        continue;
                    }
                    HashMap<String, String> map = new HashMap<>();
                    map.put("glueType", "BEAN"); // 运行模式
                    map.put("executorHandler", cronJob.name()); // 执行器任务handler
                    map.put("jobDesc", cronJob.description());  // 执行器任务handler描述
                    map.put("jobGroup", jobGroupId);  // 执行器主键ID
                    map.put("author", "system");  // 作者
                    map.put("alarmEmail", ""); //报警邮件

                    map.put("scheduleType", "CRON");  // 调度类型
                    map.put("scheduleConf", cronJob.cron());  // Cron

                    map.put("executorRouteStrategy", "FIRST");// 路由策略
                    map.put("misfireStrategy", "DO_NOTHING");// 调度过期策略
                    map.put("executorBlockStrategy", "SERIAL_EXECUTION");// 阻塞处理策略
                    map.put("executorTimeout", "0");// 任务超时时间
                    map.put("executorFailRetryCount", "0");// 失败重试次数
                    map.put("triggerStatus", "1");// 调度状态：0-停止，1-运行

                    joblist.add(JSON.toJSONString(map));
                }
            }

            if (CollectionsUtil.isNotEmpty(joblist)) {
                String res = HttpClientUtil.sendHttpPostByJson(adminAddresses + "/api/registJobs", joblist.toString(), new HashMap<>());
                log.info("job新增成功id=" + res);
            }

            log.info("增加job方法执行时间:" + (System.currentTimeMillis() - millisStart) / 1000 + "秒");
        } catch (Exception e) {
            log.error("job regist error", e);
        }
    }

}
