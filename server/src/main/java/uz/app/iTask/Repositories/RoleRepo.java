package uz.app.iTask.Repositories;

import uz.app.Anno.Repository;
import uz.app.iTask.Models.Role;

public class RoleRepo /*extends Repository<Role>*/ {
    public RoleRepo()
    {
        //SetTargetEntity(Role.class);
    }

    public Role[] getAll() {
        return new Role[0];
    }

    public Role getById(long id) {
        return null;
    }

    public boolean save(Role entity) {
        return false;
    }

    public boolean delete(long id) {
        return false;
    }

    public long count() {
        return 0;
    }
}
