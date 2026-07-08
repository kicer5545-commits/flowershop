package com.flowershop.dao;

import com.flowershop.model.Cart;
import com.flowershop.utils.DruidUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.List;

public class CartDao {
    private QueryRunner queryRunner = new QueryRunner(DruidUtils.getDataSource());

//    public List<Cart> findByUserId(int userId) throws SQLException {
//        String sql = "SELECT * FROM t_cart WHERE user_id = ?";
//        return queryRunner.query(sql, new BeanListHandler<>(Cart.class), userId);
//    }
//
//    public Cart findById(int id) throws SQLException {
//        String sql = "SELECT * FROM t_cart WHERE id = ?";
//        return queryRunner.query(sql, new BeanHandler<>(Cart.class), id);
//    }
//
//    public Cart findByUserIdAndProductId(int userId, int productId) throws SQLException {
//        String sql = "SELECT * FROM t_cart WHERE user_id = ? AND product_id = ?";
//        return queryRunner.query(sql, new BeanHandler<>(Cart.class), userId, productId);
//    }

    // ... existing code ...

    public Cart findById(int id) throws SQLException {
        String sql = "SELECT id, product_id AS productId, amount, user_id AS userId, date FROM t_cart WHERE id = ?";
        return queryRunner.query(sql, new BeanHandler<>(Cart.class), id);
    }

    public Cart findByUserIdAndProductId(int userId, int productId) throws SQLException {
        String sql = "SELECT id, product_id AS productId, amount, user_id AS userId, date FROM t_cart WHERE user_id = ? AND product_id = ?";
        return queryRunner.query(sql, new BeanHandler<>(Cart.class), userId, productId);
    }

    public List<Cart> findByUserId(int userId) throws SQLException {
        String sql = "SELECT id, product_id AS productId, amount, user_id AS userId, date FROM t_cart WHERE user_id = ?";
        return queryRunner.query(sql, new BeanListHandler<>(Cart.class), userId);
    }

// ... existing code ...


    public int save(Cart cart) throws SQLException {
        String sql = "INSERT INTO t_cart(product_id, amount, user_id, date) VALUES(?,?,?,?)";
        return queryRunner.update(sql, cart.getProductId(), cart.getAmount(),
                cart.getUserId(), cart.getDate());
    }

    public int update(Cart cart) throws SQLException {
        String sql = "UPDATE t_cart SET amount=? WHERE id=?";
        return queryRunner.update(sql, cart.getAmount(), cart.getId());
    }

    public int updateAmountByUserIdAndProductId(int userId, int productId, int amount) throws SQLException {
        String sql = "UPDATE t_cart SET amount=? WHERE user_id=? AND product_id=?";
        return queryRunner.update(sql, amount, userId, productId);
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM t_cart WHERE id = ?";
        return queryRunner.update(sql, id);
    }

    public int deleteByUserId(int userId) throws SQLException {
        String sql = "DELETE FROM t_cart WHERE user_id = ?";
        return queryRunner.update(sql, userId);
    }
}
