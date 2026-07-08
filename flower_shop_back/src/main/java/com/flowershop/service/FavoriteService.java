package com.flowershop.service;

import com.flowershop.dao.FavoriteDao;
import com.flowershop.model.Favorite;

import java.sql.SQLException;
import java.util.List;

public class FavoriteService {
    private FavoriteDao favoriteDao = new FavoriteDao();

    public boolean isFavorited(int userId, int productId) {
        try {
            return favoriteDao.findByUserIdAndProductId(userId, productId) != null;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String addFavorite(int userId, int productId) {
        try {
            if (favoriteDao.findByUserIdAndProductId(userId, productId) != null) {
                return "已收藏";
            }
            int result = favoriteDao.save(userId, productId);
            return result > 0 ? "收藏成功" : "收藏失败";
        } catch (SQLException e) {
            e.printStackTrace();
            return "收藏失败：" + e.getMessage();
        }
    }

    public String removeFavorite(int userId, int productId) {
        try {
            int result = favoriteDao.delete(userId, productId);
            return result > 0 ? "取消收藏成功" : "取消收藏失败";
        } catch (SQLException e) {
            e.printStackTrace();
            return "取消收藏失败：" + e.getMessage();
        }
    }

    public List<Favorite> getUserFavorites(int userId) {
        try {
            return favoriteDao.findByUserId(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}