package com.example.selfmadekid.data;

import org.threeten.bp.LocalDate;

public class OneTimeTask extends ChildTask implements Comparable<OneTimeTask> {

    private int deadlineHour;
    private int deadlineMinute;
    private LocalDate deadlineDate;
    private int confirmed = 0;


    public OneTimeTask(Integer task_id, String taskText, LocalDate deadlineDate, int deadlineHour, int deadline_minute, int finishReward){
        super.task_id = task_id;
        super.taskText = taskText;
        this.deadlineDate = deadlineDate;
        this.deadlineHour = deadlineHour;
        this.deadlineMinute = deadline_minute;
        super.finishReward = finishReward;
    }

    public OneTimeTask(Integer task_id, String taskText, LocalDate deadlineDate, int deadlineHour, int deadline_minute, int finishReward, int confirmed){
        super.task_id = task_id;
        super.taskText = taskText;
        this.deadlineDate = deadlineDate;
        this.deadlineHour = deadlineHour;
        this.deadlineMinute = deadline_minute;
        super.finishReward = finishReward;
        this.confirmed = confirmed;
    }

    public int getDeadlineHour() {
        return deadlineHour;
    }

    public void setDeadlineHour(int deadlineHour) {
        this.deadlineHour = deadlineHour;
    }

    public int getDeadlineMinute() {
        return deadlineMinute;
    }

    public void setDeadlineMinute(int deadlineMinute) {
        this.deadlineMinute = deadlineMinute;
    }

    public LocalDate getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(LocalDate deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public int getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }

    @Override
    public int compareTo(OneTimeTask o) {
        int i = deadlineDate.compareTo(o.getDeadlineDate());
        if (i==0){
            i = deadlineHour -o.getDeadlineHour();
            if (i==0){
                i = deadlineMinute-o.getDeadlineMinute();
            }
        }
        return i;

    }
}
