package uz.springBoot.iTask.DataClass;

public class User {
    private int id;
    private String login;
    private String fullName;
    private String email;
    private String passwordHash;


    public User(int id, String login, String fullName, String email, String passwordHash) {
        this.id = id;
        this.login = login;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public int getId() {
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
}
