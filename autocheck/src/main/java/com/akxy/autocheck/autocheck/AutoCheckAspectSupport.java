package com.akxy.autocheck.autocheck;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class AutoCheckAspectSupport {

    private final Map<String, CheckItemInfo> checkTimes = new ConcurrentHashMap<>();

    @Value("${autocheck.showlog:false}")
    public boolean showLog;
    @Value("${autocheck.outtime.action:log}")
    public String action;

    @Pointcut("@annotation(com.akxy.autocheck.autocheck.AutoCheck)")
    public void aspect() {
    }

    @Around("aspect()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        String a = point.getTarget().getClass().getSimpleName() + "." + point.getSignature().getName();
        methodIn(a, ((MethodSignature) point.getSignature()).getMethod().getAnnotation(AutoCheck.class));
        try {
            Object proceed = point.proceed();
            methodOut(a);
            return proceed;
        } catch (Exception e) {
            methodOut(a);
            throw e;
        }
    }

    private void methodOut(String a) {
        if (showLog) {
            log.info(">>> method out {}", a);
        }
        checkTimes.get(a).removeTime();
    }

    private void methodIn(String a, AutoCheck autoCheck) {
        if (showLog) {
            log.info("<<< method in {}", a);
        }
        if (!checkTimes.containsKey(a)) {
            checkTimes.put(a, new CheckItemInfo(autoCheck));
        }
        checkTimes.get(a).addTime(System.currentTimeMillis());
    }

    @Scheduled(cron = "0/30 * * * * ?")
    public void check() {
        if (showLog) {
            log.info("method info >>>\n {}", health());
        }
        if (checkTimes.isEmpty()) {
            return;
        }
        checkTimes.forEach((s, checkItemInfo) -> {
            if (checkItemInfo.isOutOfTime()) {
                log.error("函数 {} 超时({}{})未响应", s, checkItemInfo.outOfTime, checkItemInfo.timeUnit);
                if ("exit".equals(action)) {
                    outOfTimeExit();
                }
            }
        });
    }


    protected void outOfTimeExit() {
        log.error("exit");
        System.exit(0);
    }

    public List<HealthItem> health() {
        List<HealthItem> strings = new ArrayList<>();
        checkTimes.forEach((s, checkItemInfo) ->
                strings.add(checkItemInfo.healthAnalysis(s)));
        return strings;
    }

    private static class CheckItemInfo {

        private long avgTime;
        private int totalRecord;
        private final long outOfTime;
        private final TimeUnit timeUnit;
        private static final int MAX_LENGTH = 10;
        private final Queue<Long> times;

        public CheckItemInfo(AutoCheck autoCheck) {
            outOfTime = autoCheck.value();
            times = new ArrayDeque<>(MAX_LENGTH);
            timeUnit = autoCheck.timeUnit();
        }

        void addTime(long time) {
            if (times.size() < MAX_LENGTH) {
                times.add(time);
            }
        }

        void removeTime() {
            if (!times.isEmpty()) {
                Long poll = times.poll();
                if (poll != null) {
                    if (totalRecord == 0) {
                        avgTime = System.currentTimeMillis() - poll;
                    } else {
                        avgTime = (long) (0.9 * avgTime + 0.1 * (System.currentTimeMillis() - poll));
                    }
                    totalRecord++;
                }
            }
        }

        HealthItem healthAnalysis(String methodName) {
            HealthItem healthItem = new HealthItem();
            healthItem.methodName = methodName;
            healthItem.totalCall = totalRecord + times.size();
            healthItem.success = totalRecord;
            healthItem.blocked = times.size();
            healthItem.avgTime = avgTime;
            if (totalRecord == 0) {
                if (!times.isEmpty()) {
                    healthItem.health = String.format(" %.2f %%", (1 - (times.peek()) * 1.0 / timeUnit.toMillis(outOfTime)) * 100);
                } else {
                    healthItem.health = "none record any action";
                }
            } else {
                healthItem.health = String.format(" %.2f %%", (1 - avgTime * 1.0 / timeUnit.toMillis(outOfTime)) * 100);
            }
            return healthItem;
        }

        boolean isOutOfTime() {
            if (times.isEmpty()) {
                return false;
            }
            Long peek = times.peek();

            return peek != null && System.currentTimeMillis() - peek > timeUnit.toMillis(outOfTime);
        }
    }

    @Getter
    @Setter
    public static class HealthItem {
        String methodName;
        int totalCall;
        int success;
        int blocked;
        long avgTime;
        String health;

        @Override
        public String toString() {
            return "HealthItem{" +
                    "methodName='" + methodName + '\'' +
                    ", totalCall=" + totalCall +
                    ", success=" + success +
                    ", blocked=" + blocked +
                    ", avgTime=" + avgTime +
                    ", health='" + health + '\'' +
                    '}';
        }
    }

}


