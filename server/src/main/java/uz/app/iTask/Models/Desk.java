package uz.app.iTask.Models;

import java.util.Date;

public class Desk extends BaseEntity {
    private long id;
    private String name;
    private String description;
    private float order;
    private int ownerId;
    private int createdBy;
    private Date createDate;
    private Date updateDate;

    public Desk(int id, String name, String description, float order, int ownerId, int createdBy, Date createDate, Date updateDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.order = order;
        this.ownerId = ownerId;
        this.createdBy = createdBy;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public long getId() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getOrder() {
        return order;
    }

    public void setOrder(float order) {
        this.order = order;
    }

    public int getOwnerId() {
        return ownerId;
    }
}
