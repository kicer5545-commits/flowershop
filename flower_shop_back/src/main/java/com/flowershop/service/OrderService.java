package com.flowershop.service;

import com.flowershop.dao.CartDao;
import com.flowershop.dao.OrderDao;
import com.flowershop.dao.ProductDao;
import com.flowershop.dao.UserDao;
import com.flowershop.model.Cart;
import com.flowershop.model.Order;
import com.flowershop.model.Product;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrderService {
    private OrderDao orderDao = new OrderDao();
    private CartDao cartDao = new CartDao();
    private ProductDao productDao = new ProductDao();
    private UserDao userDao = new UserDao();

    public List<Order> getOrdersByUserId(int userId) {
        try {
            return orderDao.findByUserId(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Order> getAllOrders() {
        try {
            return orderDao.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Order> getOrdersByParams(String userName, String status) {
        try {
            return orderDao.findByParams(userName, status);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Order getOrderById(int id) {
        try {
            return orderDao.findById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String createOrder(Order order) {
        try {
            System.out.println("=== 创建订单调试信息 ===");
            System.out.println("用户ID: " + order.getUserId());
            System.out.println("订单价格: " + order.getPrice());
            System.out.println("收货地址: " + order.getAddress());
            System.out.println("商品详情: " + order.getProductDetails());

            if (order.getTime() == null || order.getTime().isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                order.setTime(sdf.format(new Date()));
            }

            if (order.getStatus() == null || order.getStatus().isEmpty()) {
                order.setStatus("0");
            }

            int result = orderDao.save(order);
            if (result > 0) {
                System.out.println("订单创建成功");
                return "下单成功";
            } else {
                System.out.println("订单创建失败");
                return "下单失败";
            }
        } catch (SQLException e) {
            System.out.println("创建订单异常: " + e.getMessage());
            e.printStackTrace();
            return "下单失败：" + e.getMessage();
        }
    }

    public String createOrderFromCart(int userId, String address) {
        try {
            System.out.println("=== 从购物车创建订单 ===");
            System.out.println("用户ID: " + userId);
            System.out.println("收货地址: " + address);

            List<Cart> cartItems = cartDao.findByUserId(userId);

            if (cartItems == null || cartItems.isEmpty()) {
                System.out.println("购物车为空");
                return "购物车为空";
            }

            System.out.println("购物车商品数量: " + cartItems.size());

            StringBuilder productDetails = new StringBuilder();
            BigDecimal totalPrice = BigDecimal.ZERO;
            boolean hasValidItem = false;

            for (Cart cartItem : cartItems) {
                System.out.println("处理购物车项 ID: " + cartItem.getId() +
                        ", 商品ID: " + cartItem.getProductId() +
                        ", 数量: " + cartItem.getAmount());

                Product product = productDao.findById(cartItem.getProductId());

                if (product != null) {
                    System.out.println("找到商品: " + product.getName() +
                            ", 价格: " + product.getPrice());

                    if (product.getPrice() == null) {
                        System.out.println("警告: 商品价格无效，跳过此商品");
                        continue;
                    }

                    if (productDetails.length() > 0) {
                        productDetails.append("; ");
                    }
                    productDetails.append("[").append(product.getId()).append("]")
                            .append(product.getName())
                            .append(" x").append(cartItem.getAmount());

                    BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(cartItem.getAmount()));
                    totalPrice = totalPrice.add(itemTotal);
                    hasValidItem = true;

                    System.out.println("添加商品成功，小计: " + itemTotal + ", 累计总价: " + totalPrice);
                } else {
                    System.out.println("警告: 未找到商品ID " + cartItem.getProductId());
                }
            }

            if (!hasValidItem) {
                System.out.println("没有有效商品可以下单");
                return "没有有效商品";
            }

            if (totalPrice.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("订单总价无效: " + totalPrice);
                return "订单总价无效";
            }

            System.out.println("最终订单总价: " + totalPrice);
            System.out.println("商品详情: " + productDetails.toString());

            Order order = new Order();
            order.setUserId(userId);
            order.setAddress(address);
            order.setProductDetails(productDetails.toString());
            order.setPrice(totalPrice);
            order.setStatus("0");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            order.setTime(sdf.format(new Date()));

            System.out.println("准备保存订单...");
            int result = orderDao.save(order);

            if (result > 0) {
                System.out.println("订单保存成功，订单ID: " + result);
                System.out.println("清空购物车...");
                cartDao.deleteByUserId(userId);
                System.out.println("购物车已清空");
                return "下单成功";
            } else {
                System.out.println("订单保存失败，返回结果: " + result);
                return "下单失败";
            }
        } catch (SQLException e) {
            System.out.println("从购物车创建订单异常: " + e.getMessage());
            System.out.println("异常类型: " + e.getClass().getName());
            e.printStackTrace();
            return "下单失败：" + e.getMessage();
        } catch (Exception e) {
            System.out.println("从购物车创建订单未知异常: " + e.getMessage());
            e.printStackTrace();
            return "下单失败：" + e.getMessage();
        }
    }

    public String updateOrderStatus(int orderId, String status) {
        try {
            Order order = orderDao.findById(orderId);
            if (order == null) {
                return "订单不存在";
            }
            // 如果是从未支付改为已支付，需要扣减库存
            if ("0".equals(order.getStatus()) && "1".equals(status)) {
                String deductResult = deductStockForOrder(order);
                if (!"success".equals(deductResult)) {
                    return deductResult;
                }
            }
            order.setStatus(status);
            int result = orderDao.update(order);
            return result > 0 ? "更新成功" : "更新失败";
        } catch (SQLException e) {
            e.printStackTrace();
            return "更新失败：" + e.getMessage();
        }
    }

    public String deleteOrder(int id) {
        try {
            System.out.println("=== 删除订单 ===");
            System.out.println("订单ID: " + id);

            int result = orderDao.delete(id);
            if (result > 0) {
                System.out.println("订单删除成功");
                return "删除成功";
            } else {
                System.out.println("订单删除失败");
                return "删除失败";
            }
        } catch (SQLException e) {
            System.out.println("删除订单异常: " + e.getMessage());
            e.printStackTrace();
            return "删除失败：" + e.getMessage();
        }
    }

    // 扣减订单中所有商品的库存
    private String deductStockForOrder(Order order) {
        String productDetails = order.getProductDetails();
        if (productDetails == null || productDetails.isEmpty()) {
            return "订单商品详情为空";
        }
        // 解析格式: "[id]名称 x数量" 或 "[id]名称 x数量; [id]名称 x数量"
        String[] items = productDetails.split(";");
        for (String item : items) {
            item = item.trim();
            // 提取商品ID: 匹配 [数字]
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\[(\\d+)\\]");
            java.util.regex.Matcher matcher = pattern.matcher(item);
            if (!matcher.find()) {
                return "商品详情格式错误，缺少ID：" + item;
            }
            int productId = Integer.parseInt(matcher.group(1));
            // 提取数量: 匹配 "x数字"
            java.util.regex.Pattern countPattern = java.util.regex.Pattern.compile("x(\\d+)");
            java.util.regex.Matcher countMatcher = countPattern.matcher(item);
            if (!countMatcher.find()) {
                return "商品详情格式错误，缺少数量：" + item;
            }
            int amount = Integer.parseInt(countMatcher.group(1));

            boolean success = productService.decreaseStock(productId, amount);
            if (!success) {
                return "库存不足，商品ID：" + productId;
            }
        }
        return "success";
    }

    private ProductService productService = new ProductService();
}
