package uz.app.iTask.Repositories;

import uz.app.iTask.Annotations.Schema;
import uz.app.iTask.Annotations.Table;
import uz.app.iTask.Models.User;
import uz.app.iTask.Util.Anno;
import uz.app.iTask.Util.Database;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.LinkedList;

public class UserRepo extends Repository<User> {

    public UserRepo() throws Exception
    {
        this.Init(User.class);    // Must be here
        this.SCHEMA_NAME = User.class.getAnnotation(Schema.class).value();
        this.TABLE_NAME = User.class.getAnnotation(Table.class).value();
        this.Annotations = User.class.getAnnotations();
        /*Field[] fields = User.class.getDeclaredFields();
        for (Field field: fields) {
            field.setAccessible(true);  // Gives access to non-public fields
        }*/
    }


    public User[] getAll() throws Exception {
        Connection connection = Database.getConnection();
        if(connection == null)
            throw new Exception("Couldn't connect to database.");

        LinkedList<User> users = new LinkedList<User>();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM " + getTableFullName());
        ResultSet rs = stmt.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        User user;
        while(rs.next())
        {
            user = this.makeObject(rs, rsmd);
            users.add(user);
        }

        int i = 0;
        User[] res = new User[users.size()];
        for (User u: users) {
            if(i > users.size())
                break;
            res[i] = u;
            i++;
        }

        return users.toArray(res);
    }

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
