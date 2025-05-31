package com.wms.common.interceptor;

import com.wms.entity.Task;
import com.wms.event.TaskEvent;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class NotifyInterceptor implements InnerInterceptor {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void beforeUpdate(Executor executor, MappedStatement ms, Object parameter) {
        if (parameter instanceof Task) {
            Task task = (Task) parameter;
            String action = "";
            if (ms.getId().contains("insert")) {
                action = "insert";
            } else if (ms.getId().contains("update")) {
                action = "update";
            } else if (ms.getId().contains("delete")) {
                action = "delete";
            }
            if (!action.isEmpty()) {
                eventPublisher.publishEvent(new TaskEvent(this, task, action));
            }
        }
    }
}
