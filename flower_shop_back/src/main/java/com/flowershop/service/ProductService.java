package com.flowershop.service;

import com.flowershop.dao.ProductDao;
import com.flowershop.model.Product;

import java.sql.SQLException;
import java.util.List;

public class ProductService {
    private ProductDao productDao = new ProductDao();

    public List<Product> getAllProducts() {
        try {
            return productDao.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Product> getProductsByCategory(String category) {
        try {
            return productDao.findByCategory(category);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Product getProductById(int id) {
        try {
            return productDao.findById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String addProduct(Product product) {
        try {
            int result = productDao.save(product);
            if (result > 0) {
                return "添加成功";
            } else {
                return "添加失败";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "添加失败：" + e.getMessage();
        }
    }

    public String updateProduct(Product product) {
        try {
            int result = productDao.update(product);
            if (result > 0) {
                return "更新成功";
            } else {
                return "更新失败";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "更新失败：" + e.getMessage();
        }
    }

    public String deleteProduct(int id) {
        try {
            int result = productDao.delete(id);
            if (result > 0) {
                return "删除成功";
            } else {
                return "删除失败";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "删除失败：" + e.getMessage();
        }
    }

    public boolean decreaseStock(int productId, int amount) {
        try {
            int rows = productDao.decreaseStock(productId, amount);
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}