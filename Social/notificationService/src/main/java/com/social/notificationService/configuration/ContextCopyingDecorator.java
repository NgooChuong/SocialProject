package com.social.notificationService.configuration;

import org.springframework.core.task.TaskDecorator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class ContextCopyingDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        RequestAttributes context = RequestContextHolder.getRequestAttributes(); // lay context cua super thread
        return () -> {
            try {
                if (context != null) {
                    RequestContextHolder.setRequestAttributes(context);// set context cho sub thread
                }
                runnable.run(); // tiep tuc tac vu chạy sub thread
            } finally {
                RequestContextHolder.resetRequestAttributes(); // reset lai context cho sub thread, tranh gay loi khi tai dung thread trong threadpool
            }
        };
    }
}
