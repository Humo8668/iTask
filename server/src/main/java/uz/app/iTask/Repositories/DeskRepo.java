package uz.app.iTask.Repositories;

import uz.app.Anno.Repository;
import uz.app.iTask.Models.Desk;

public class DeskRepo /*extends Repository<Desk>*/ {
    public DeskRepo()
    {
        //SetTargetEntity(Desk.class);
    }

    public Desk[] getAll() {
        return new Desk[0];
    }

    public Desk getById(long id) {
        return null;
    }

    public boolean save(Desk entity) {
        return false;
    }

    public boolean delete(long id) {
        return false;
    }

    public long count() {
        return 0;
    }
}
