package src.database.model;

public abstract class BaseEntity {
    protected int id; // Common primary key field

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}