package com.wms.entity;

public class TaskCommentRequest {
    private int userId;
    private Long taskId;
    private String content;

    // getter/setter
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}