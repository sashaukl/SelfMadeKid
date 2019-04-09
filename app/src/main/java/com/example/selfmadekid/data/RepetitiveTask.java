package com.example.selfmadekid.data;

public class RepetitiveTask extends ChildTask implements Comparable<RepetitiveTask> {
    private int startHour = -1;
    private int startMinute = -1;

    public RepetitiveTask(Integer task_id, String taskText, int necessaryProgress, int startHour, int startMinute) {
        super.task_id = task_id;
        super.taskText = taskText;
        this.finishReward = necessaryProgress;
        this.startHour = startHour;
        this.startMinute = startMinute;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    @Override
    public int compareTo(RepetitiveTask o) {
        int difference = startHour - o.getStartHour();
        if (difference == 0){
            return startMinute - o.getStartMinute();
        }
        return difference;
    }
}
