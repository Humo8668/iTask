package uz.springBoot.iTask.Controllers;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uz.springBoot.iTask.DataClass.User;

public class UserController {

    public static User getUserById(int id)
    {
        User user = new User(
                id,
                "admin",
                "Mr. Anderson",
                "mr_anderson@mail.com",
                "$2a$10$AjHGc4x3Nez/p4ZpvFDWeO6FGxee/cVqj5KHHnHfuLnIOzC5ag4fm"
        );
        return user;
    }

    public static User getUserByLogin(String login)
    {
        return UserController.getUserById(1);
    }


    public static void saveUser(User user)
    {
        throw new NotImplementedException();
    }

    public static void deleteUser(int userId)
    {
        throw new NotImplementedException();
    }

}
