package com.flowershop.service;

import com.flowershop.dao.CartDao;
import com.flowershop.dao.ProductDao;
import com.flowershop.model.Cart;
import com.flowershop.model.Product;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CartService {
    private CartDao cartDao = new CartDao();
    private ProductDao productDao = new ProductDao();

    public List<Cart> getCartByUserId(int userId) {
        try {
            List<Cart> carts = cartDao.findByUserId(userId);
            if (carts != null) {
                for (Cart cart : carts) {
                    fillProductInfo(cart);
                }
            }
            return carts;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

//    public String addToCart(Cart cart) {
//        try {
//            Cart existCart = cartDao.findByUserIdAndProductId(cart.getUserId(), cart.getProductId());
//
//            if (existCart != null) {
//                int newAmount = existCart.getAmount() + cart.getAmount();
//                cartDao.updateAmountByUserIdAndProductId(cart.getUserId(), cart.getProductId(), newAmount);
//                return "添加成功";
//            } else {
//                if (cart.getDate() == null || cart.getDate().isEmpty()) {
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    cart.setDate(sdf.format(new Date()));
//                }
//                int result = cartDao.save(cart);
//                if (result > 0) {
//                    return "添加成功";
//                } else {
//                    return "添加失败";
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return "添加失败：" + e.getMessage();
//        }
//    }
public String addToCart(Cart cart) {
    try {
        System.out.println("=== 添加购物车调试信息 ===");
        System.out.println("接收到的 productId: " + cart.getProductId());
        System.out.println("接收到的 userId: " + cart.getUserId());
        System.out.println("接收到的 amount: " + cart.getAmount());

        Cart existCart = cartDao.findByUserIdAndProductId(cart.getUserId(), cart.getProductId());

        if (existCart != null) {
            int newAmount = existCart.getAmount() + cart.getAmount();
            cartDao.updateAmountByUserIdAndProductId(cart.getUserId(), cart.getProductId(), newAmount);
            return "添加成功";
        } else {
            if (cart.getDate() == null || cart.getDate().isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                cart.setDate(sdf.format(new Date()));
            }
            int result = cartDao.save(cart);
            if (result > 0) {
                return "添加成功";
            } else {
                return "添加失败";
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return "添加失败：" + e.getMessage();
    }
}
    public String updateCart(Cart cart) {
        try {
            int result = cartDao.update(cart);
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

    public String deleteFromCart(int id) {
        try {
            int result = cartDao.delete(id);
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

    public String clearCart(int userId) {
        try {
            int result = cartDao.deleteByUserId(userId);
            if (result > 0) {
                return "清空成功";
            } else {
                return "清空失败";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "清空失败：" + e.getMessage();
        }
    }

//    private void fillProductInfo(Cart cart) throws SQLException {
//        try {
//            Product product = productDao.findById(cart.getProductId());
//            if (product != null) {
//                cart.setProductName(product.getName());
//                cart.setProductImage(product.getImage());
//                cart.setProductPrice(product.getPrice());
//            } else {
//                System.out.println("未找到商品ID: " + cart.getProductId());
//            }
//        } catch (Exception e) {
//            System.out.println("填充商品信息失败，商品ID: " + cart.getProductId());
//            e.printStackTrace();
//        }
//    }

    private void fillProductInfo(Cart cart) throws SQLException {
        try {
            Product product = productDao.findById(cart.getProductId());
            if (product != null) {
                System.out.println("找到商品: " + product.getName());
                cart.setProductName(product.getName());
                cart.setProductImage(product.getImage());
                cart.setProductPrice(product.getPrice());
            } else {
                System.out.println("未找到商品ID: " + cart.getProductId());
            }
        } catch (Exception e) {
            System.out.println("填充商品信息失败，商品ID: " + cart.getProductId());
            e.printStackTrace();
        }
    }

}
