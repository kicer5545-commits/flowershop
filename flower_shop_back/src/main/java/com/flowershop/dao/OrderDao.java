package com.flowershop.dao;

import com.flowershop.model.Order;
import com.flowershop.utils.DruidUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDao {
    private QueryRunner queryRunner = new QueryRunner(DruidUtils.getDataSource());

    public List<Order> findByUserId(int userId) throws SQLException {
        String sql = "SELECT o.id, o.product_details AS productDetails, o.price, o.status, o.user_id AS userId, o.address, o.time, u.user_name AS userName FROM t_order o JOIN t_user u ON o.user_id = u.id WHERE o.user_id = ? ORDER BY o.time DESC";
        return queryRunner.query(sql, new BeanListHandler<>(Order.class), userId);
    }

    public List<Order> findAll() throws SQLException {
        String sql = "SELECT o.id, o.product_details AS productDetails, o.price, o.status, o.user_id AS userId, o.address, o.time, u.user_name AS userName FROM t_order o JOIN t_user u ON o.user_id = u.id ORDER BY o.time DESC";
        return queryRunner.query(sql, new BeanListHandler<>(Order.class));
    }

    public List<Order> findByParams(String userName, String status) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT o.id, o.product_details AS productDetails, o.price, o.status, o.user_id AS userId, o.address, o.time, u.user_name AS userName FROM t_order o JOIN t_user u ON o.user_id = u.id WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (userName != null && !userName.trim().isEmpty()) {
            sql.append(" AND u.user_name LIKE ?");
            params.add("%" + userName + "%");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND o.status = ?");
            params.add(status);
        }
        sql.append(" ORDER BY o.time DESC");

        return queryRunner.query(sql.toString(), new BeanListHandler<>(Order.class), params.toArray());
    }

    public Order findById(int id) throws SQLException {
        String sql = "SELECT o.id, o.product_details AS productDetails, o.price, o.status, o.user_id AS userId, o.address, o.time, u.user_name AS userName FROM t_order o JOIN t_user u ON o.user_id = u.id WHERE o.id = ?";
        return queryRunner.query(sql, new BeanHandler<>(Order.class), id);
    }

    public int save(Order order) throws SQLException {
        String sql = "INSERT INTO t_order(product_details, price, status, user_id, address, time) VALUES(?,?,?,?,?,?)";
        return queryRunner.update(sql, order.getProductDetails(), order.getPrice(),
                order.getStatus(), order.getUserId(), order.getAddress(), order.getTime());
    }

    public int update(Order order) throws SQLException {
        String sql = "UPDATE t_order SET status=? WHERE id=?";
        return queryRunner.update(sql, order.getStatus(), order.getId());
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM t_order WHERE id = ?";
        return queryRunner.update(sql, id);
    }

    public Map<String, Object> getLast7DaysOrderCount() throws SQLException {
        String sql = "SELECT DATE(time) as order_date, COUNT(*) as count FROM t_order WHERE time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) GROUP BY DATE(time) ORDER BY order_date";
        QueryRunner runner = new QueryRunner(DruidUtils.getDataSource());
        List<Map<String, Object>> list = runner.query(sql, new MapListHandler());

        List<String> dates = new java.util.ArrayList<>();
        List<Integer> counts = new java.util.ArrayList<>();
        for (Map<String, Object> row : list) {
            dates.add(row.get("order_date").toString());
            counts.add(((Long) row.get("count")).intValue());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("dates", dates);
        result.put("counts", counts);
        return result;
    }
}
