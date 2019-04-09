package com.example.selfmadekid.data;

public abstract class ChildTask  {

    protected Integer task_id;
    protected String taskText;
    protected int finishReward;

    public Integer getTask_id() {
        return task_id;
    }

    public void setTask_id(Integer task_id) {
        this.task_id = task_id;
    }

    public String getTaskText() {
        return taskText;
    }

    public void setTaskText(String taskText) {
        this.taskText = taskText;
    }

    public int getFinishReward() {
        return finishReward;
    }

    public void setFinishReward(int finishReward) {
        this.finishReward = finishReward;
    }


}
