package com.flowershop.service;

import com.flowershop.dao.ProductDao;
import com.flowershop.dao.OrderDao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StatisticsService {
    private ProductDao productDao = new ProductDao();
    private OrderDao orderDao = new OrderDao();

    public Map<String, Integer> getCategoryCount() {
        try {
            return productDao.getCategoryCount();
        } catch (SQLException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public Map<String, Object> getLast7DaysOrderTrend() {
        try {
            return orderDao.getLast7DaysOrderCount();
        } catch (SQLException e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("dates", new String[0]);
            error.put("counts", new int[0]);
            return error;
        }
    }
}