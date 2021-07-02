package uz.app.iTask.Repositories;

import uz.app.iTask.Models.ActionLog;

public class ActionLogRepo /*extends Repository<ActionLog>*/ {
    public ActionLogRepo()
    {
        //SetTargetEntity(ActionLog.class);
    }

    public ActionLog[] getAll() {
        return new ActionLog[0];
    }

    public ActionLog getById(long id) {
        return null;
    }

    public boolean save(ActionLog entity) {
        return false;
    }

    public boolean delete(long id) {
        return false;
    }

    public long count() {
        return 0;
    }
}
