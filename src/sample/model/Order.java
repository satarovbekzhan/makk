package sample.model;

import java.util.Date;

public class Order {
    private Integer id;
    private Status status;
    private String user;
    private Date ordered;

    public Order(Integer id, Status status, String user, Date ordered) {
        this.id = id;
        this.status = status;
        this.user = user;
        this.ordered = ordered;
    }

    public Integer getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public String getUser() {
        return user;
    }

    public Date getOrdered() {
        return ordered;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", status=" + status +
                ", user='" + user + '\'' +
                ", ordered='" + ordered + '\'' +
                '}';
    }
}
