package uz.springBoot.iTask.DataClass;

import java.util.Date;

public class Role {
    private int id;
    private String name;
    private int createdBy;
    private Date createDate;
    private Date updateDate;

    public Role(int id, String name, int createdBy, Date createDate, Date updateDate) {
        this.id = id;
        this.name = name;
        this.createdBy = createdBy;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }
}
