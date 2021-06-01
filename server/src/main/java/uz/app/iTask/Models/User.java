package uz.app.iTask.Models;

import uz.app.Anno.Annotations.Column;
import uz.app.Anno.Annotations.Schema;
import uz.app.Anno.Annotations.Table;
import uz.app.Anno.BaseEntity;

@Schema("public")
@Table("Users")
public class User extends BaseEntity {
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

    public long getId() {
        return id;
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

    public String getLogin() {
        return login;
    }

    public String getState() {
        return state;
    }
}
