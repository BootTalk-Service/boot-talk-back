package com.icandoit.boottalk.stomp_chat.scheduler;

import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
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
public class ChatQuartzSchedulerService {

    private final Scheduler scheduler;

    public void scheduleStartAndEndJobs(String roomUuid, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            scheduleJob(roomUuid, startTime, StartCoffeeChatJob.class, "start");
            scheduleJob(roomUuid, endTime, EndCoffeeChatJob.class, "end");
        } catch (SchedulerException e) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
    }

    private void scheduleJob(String roomUuid, LocalDateTime time, Class<? extends Job> jobClass, String type) throws SchedulerException {
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
