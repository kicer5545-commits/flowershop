package com.flowershop.dao;

import com.flowershop.model.Favorite;
import com.flowershop.utils.DruidUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.List;

public class FavoriteDao {
    private QueryRunner queryRunner = new QueryRunner(DruidUtils.getDataSource());

    public Favorite findByUserIdAndProductId(int userId, int productId) throws SQLException {
        String sql = "SELECT id, user_id as userId, product_id as productId, create_time as createTime FROM t_favorite WHERE user_id = ? AND product_id = ?";
        return queryRunner.query(sql, new BeanHandler<>(Favorite.class), userId, productId);
    }

    public List<Favorite> findByUserId(int userId) throws SQLException {
        String sql = "SELECT f.id, f.user_id as userId, f.product_id as productId, f.create_time as createTime, " +
                "p.name as productName, p.image as productImage, p.price as productPrice " +
                "FROM t_favorite f JOIN t_product p ON f.product_id = p.id WHERE f.user_id = ? ORDER BY f.create_time DESC";
        return queryRunner.query(sql, new BeanListHandler<>(Favorite.class), userId);
    }

    public int save(int userId, int productId) throws SQLException {
        String sql = "INSERT INTO t_favorite(user_id, product_id) VALUES(?, ?)";
        return queryRunner.update(sql, userId, productId);
    }

    public int delete(int userId, int productId) throws SQLException {
        String sql = "DELETE FROM t_favorite WHERE user_id = ? AND product_id = ?";
        return queryRunner.update(sql, userId, productId);
    }

    public int deleteById(int id) throws SQLException {
        String sql = "DELETE FROM t_favorite WHERE id = ?";
        return queryRunner.update(sql, id);
    }
}