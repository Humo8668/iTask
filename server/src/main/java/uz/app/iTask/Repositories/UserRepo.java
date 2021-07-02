package uz.app.iTask.Repositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.app.Anno.Repository;
import uz.app.iTask.Models.User;

public class UserRepo extends Repository<User> {
    static final Logger log = LoggerFactory.getLogger(UserRepo.class);

    public UserRepo()
    {
        SetTargetEntity(User.class);
    }
}
