package uz.app.iTask.Repositories;

import uz.app.Anno.Repository;
import uz.app.iTask.Models.Task;
import uz.app.iTask.Models.User;

public class TaskRepo /*extends Repository<Task>*/ {
    public TaskRepo()
    {
        //SetTargetEntity(Task.class);
    }

    public Task[] getAll() {
        return new Task[0];
    }

    public Task getById(long id) {
        return null;
    }

    public boolean save(Task entity) {
        return false;
    }

    public boolean delete(long id) {
        return false;
    }

    public long count() {
        return 0;
    }
}
