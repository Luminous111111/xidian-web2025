package com.wms.listener;

import com.wms.entity.Notify;
import com.wms.entity.Task;
import com.wms.event.TaskEvent;
import com.wms.mapper.NotifyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TaskEventListener {

    @Autowired
    private NotifyMapper notifyMapper;

    @EventListener
    public void handleTaskEvent(TaskEvent event) {
        Task task = event.getTask();
        String action = event.getAction();
        String title = "";
        String content = "";

        switch (action) {
            case "insert":
                title = "新任务";
                content = "新任务已创建：" + task.getTitle();
                break;
            case "update":
                title = "任务变动";
                content = "任务已更新：" + task.getTitle();
                break;
            case "delete":
                title = "任务被删除";
                content = "任务已删除：" + task.getTitle();
                break;
        }

        if (!title.isEmpty()) {
            createNotify(task, title, content);
        }
    }

    private void createNotify(Task task, String title, String content) {
        Notify notify = new Notify();
        notify.setTitle(title);
        notify.setContent(content);
        notify.setUserId(task.getAssigneeId());
        notify.setTaskId(task.getId());
        notify.setStatus(0); // 0 表示未读
        notifyMapper.insert(notify);
    }
}
