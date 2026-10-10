package model;

public class Order {

    private int id;

    private int buyerId;

    private double total;

    private String status;


    public Order() {
    }


    public Order(
            int id,
            int buyerId,
            double total,
            String status) {

        this.id = id;

        this.buyerId = buyerId;

        this.total = total;

        this.status = status;
    }


    public int getId() {

        return id;
    }


    public int getBuyerId() {

        return buyerId;
    }


    public double getTotal() {

        return total;
    }


    public String getStatus() {

        return status;
    }

}