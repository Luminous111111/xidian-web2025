package com.wms.event;

import com.wms.entity.Task;
import org.springframework.context.ApplicationEvent;

public class TaskEvent extends ApplicationEvent {
    private final Task task;
    private final String action;

    public TaskEvent(Object source, Task task, String action) {
        super(source);
        this.task = task;
        this.action = action;
    }

    public Task getTask() {
        return task;
    }

    public String getAction() {
        return action;
    }
}
