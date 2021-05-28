package uz.app.iTask.Repositories;

import uz.app.iTask.Models.Task;

public class TaskRepo extends Repository<Task>{
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
