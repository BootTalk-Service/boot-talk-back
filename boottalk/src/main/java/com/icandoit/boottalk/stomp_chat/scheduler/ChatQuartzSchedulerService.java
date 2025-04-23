package com.icandoit.boottalk.stomp_chat.scheduler;

import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatQuartzSchedulerService {

    private final Scheduler scheduler;

    public void scheduleStartAndEndJobs(String roomUuid, LocalDateTime startTime,
        LocalDateTime endTime) {
        try {
            scheduleJob(roomUuid, startTime, StartCoffeeChatJob.class, "start");
        } catch (SchedulerException e) {
            log.error("시작 스케쥴러 작업 실패 - roomUuid : {}", roomUuid, e);
            throw new CustomException(ErrorCode.SCHEDULER_START_JOB_ERROR);
        }

        try {
            scheduleJob(roomUuid, endTime, EndCoffeeChatJob.class, "end");
        } catch (SchedulerException e) {
            log.error("종료 스케쥴러 작업 실패 - roomUuid : {}", roomUuid, e);
            throw new CustomException(ErrorCode.SCHEDULER_END_JOB_ERROR);
        }
    }

    private void scheduleJob(String roomUuid, LocalDateTime time, Class<? extends Job> jobClass,
        String type) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
            .withIdentity(type + "_" + roomUuid)
            .usingJobData("roomUuid", roomUuid)
            .build();

        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(type + "_trigger_" + roomUuid)
            .startAt(Timestamp.valueOf(time))
            .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }
}
