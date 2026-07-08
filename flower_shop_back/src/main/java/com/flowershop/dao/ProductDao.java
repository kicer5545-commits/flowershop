package com.flowershop.dao;

import com.flowershop.model.Product;
import com.flowershop.utils.DruidUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDao {
    private QueryRunner queryRunner = new QueryRunner(DruidUtils.getDataSource());

    public List<Product> findAll() throws SQLException {
        String sql = "SELECT id, name, category, price, description, image, stock, rating FROM t_product";
        return queryRunner.query(sql, new BeanListHandler<>(Product.class));
    }

    public List<Product> findByCategory(String category) throws SQLException {
        String sql = "SELECT id, name, category, price, description, image, stock, rating FROM t_product WHERE category = ?";
        return queryRunner.query(sql, new BeanListHandler<>(Product.class), category);
    }

    public Product findById(int id) throws SQLException {
        String sql = "SELECT id, name, category, price, description, image, stock, rating FROM t_product WHERE id = ?";
        return queryRunner.query(sql, new BeanHandler<>(Product.class), id);
    }

    public int save(Product product) throws SQLException {
        String sql = "INSERT INTO t_product(name, category, price, description, image, stock, rating) VALUES(?,?,?,?,?,?,?)";
        return queryRunner.update(sql, product.getName(), product.getCategory(),
                product.getPrice(), product.getDescription(), product.getImage(),
                product.getStock(), product.getRating());
    }

    public int update(Product product) throws SQLException {
        String sql = "UPDATE t_product SET name=?, category=?, price=?, description=?, image=?, stock=?, rating=? WHERE id=?";
        return queryRunner.update(sql, product.getName(), product.getCategory(),
                product.getPrice(), product.getDescription(), product.getImage(),
                product.getStock(), product.getRating(), product.getId());
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM t_product WHERE id = ?";
        return queryRunner.update(sql, id);
    }

    public Map<String, Integer> getCategoryCount() throws SQLException {
        String sql = "SELECT category, COUNT(*) as count FROM t_product GROUP BY category";
        QueryRunner runner = new QueryRunner(DruidUtils.getDataSource());
        List<Map<String, Object>> list = runner.query(sql, new MapListHandler());
        Map<String, Integer> result = new HashMap<>();
        for (Map<String, Object> row : list) {
            result.put((String) row.get("category"), ((Long) row.get("count")).intValue());
        }
        return result;
    }

    // 添加方法：根据商品ID和扣减数量更新库存（使用乐观锁，防止并发超卖）
    public int decreaseStock(int productId, int amount) throws SQLException {
        // 扣减库存，要求当前库存 >= 扣减数量
        String sql = "UPDATE t_product SET stock = stock - ? WHERE id = ? AND stock >= ?";
        return queryRunner.update(sql, amount, productId, amount);
    }
}
