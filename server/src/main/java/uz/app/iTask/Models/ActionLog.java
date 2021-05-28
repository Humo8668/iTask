package uz.app.iTask.Models;

import java.util.Date;

// Data-class of logs of user's actions.
public class ActionLog extends BaseEntity {
    private long id;
    private int userId;         // User's id that performed action;
    private int actionId;       // Id of action;
    private int subjectId;      // Id of subject upon that action was performed;
    private Date actionDate;

    public ActionLog(int id, int userId, int actionId, int subjectId, Date actionDate) {
        this.id = id;
        this.userId = userId;
        this.actionId = actionId;
        this.subjectId = subjectId;
        this.actionDate = actionDate;
    }

    public long getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getActionId() {
        return actionId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public Date getActionDate() {
        return actionDate;
    }
}
