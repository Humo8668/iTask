package uz.app.iTask.Models;

import uz.app.Anno.Annotations.*;
import uz.app.Anno.BaseEntity;
import uz.app.Anno.Util.Rgx;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Schema("public")
@Table("Users")
public class User extends BaseEntity {
    @Id
    @Generated
    @Column("id")
    private long id;
    @Column("login")
    private String login;
    @Column("fullName")
    private String fullName;
    @Column("email")
    private String email;
    @Column("passwordHash")
    private String passwordHash;
    @Column("state")
    private String state;

    public User() { }

    public User(int id, String login, String fullName, String email, String passwordHash, String state) {
        this.id = id;
        this.login = login;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.state = state;
    }

    @Override
    public boolean isValid() {
        if(!Rgx.isEmail(this.email))
            return false;

        return true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
