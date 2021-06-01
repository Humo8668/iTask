package uz.app.iTask.Repositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.app.Anno.Repository;
import uz.app.iTask.Models.User;
import uz.app.iTask.Util.Setup;

public class UserRepo extends Repository<User> {
    static final Logger log = LoggerFactory.getLogger(UserRepo.class);

    public UserRepo() throws Exception
    {
        try {
            this.Init(User.class);    // Must be here
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /*public User[] getAll() throws Exception {

    }*/

    public User getById(long id) {
        return null;
    }

    public boolean save(User entity) {
        return false;
    }

    public boolean delete(long id) {
        return false;
    }

    public long count() {
        return 0;
    }
}
