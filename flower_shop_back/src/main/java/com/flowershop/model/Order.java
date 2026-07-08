package com.flowershop.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.math.BigDecimal;

public class Order {
    private int id;
    private String productDetails;

    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal price;
    private String status;
    private int userId;
    private String address;
    private String time;
    private String userName;

    public Order() {
    }

    public Order(int id, String productDetails, BigDecimal price, String status,
                 int userId, String address, String time) {
        this.id = id;
        this.productDetails = productDetails;
        this.price = price;
        this.status = status;
        this.userId = userId;
        this.address = address;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(String productDetails) {
        this.productDetails = productDetails;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
