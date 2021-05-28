package uz.app.iTask.Models;

public class Task extends BaseEntity {
    private long id;
    private String title;
    private String description;
    private float order;
    private int ownerId;

    public Task(int id, String title, String description, float order, int ownerId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.order = order;
        this.ownerId = ownerId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public float getOrder() {
        return order;
    }

    public void setOrder(float order) {
        this.order = order;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }
}
