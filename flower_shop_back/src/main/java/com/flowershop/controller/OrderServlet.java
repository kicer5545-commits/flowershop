package com.flowershop.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowershop.model.Order;
import com.flowershop.service.OrderService;
import com.flowershop.utils.TokenUtils;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/order/list", "/order/all", "/order/create", "/order/createFromCart", "/order/update", "/order/delete", "/order/delivery", "/order/search"})
public class OrderServlet extends HttpServlet {
    OrderService orderService = new OrderService();

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String action = req.getServletPath();
        ObjectMapper om = new ObjectMapper();
        PrintWriter pw = resp.getWriter();

        String token = req.getHeader("token");
        Integer userId = null;

        System.out.println("=== OrderServlet 调试信息 ===");
        System.out.println("请求路径: " + action);
        System.out.println("Token: " + token);

        if (token != null && TokenUtils.verifyToken(token)) {
            DecodedJWT jwt = com.auth0.jwt.JWT.decode(token);
            userId = jwt.getClaim("id").asInt();
            System.out.println("解析出的用户ID: " + userId);
        } else {
            System.out.println("Token 验证失败或为空");
        }

        try {
            if ("/order/list".equals(action)) {
                if (userId == null) {
                    pw.write("{\"error\":\"请先登录\"}");
                    return;
                }
                List<Order> orders = orderService.getOrdersByUserId(userId);
                System.out.println("查询到订单数量: " + (orders != null ? orders.size() : 0));
                om.writeValue(pw, orders);
            }

            if ("/order/all".equals(action)) {
                List<Order> orders = orderService.getAllOrders();
                System.out.println("查询到所有订单数量: " + (orders != null ? orders.size() : 0));
                om.writeValue(pw, orders);
            }

            if ("/order/search".equals(action)) {
                String userName = req.getParameter("userName");
                String status = req.getParameter("status");
                List<Order> orders = orderService.getOrdersByParams(userName, status);
                if (orders == null) {
                    orders = new java.util.ArrayList<>();
                }
                System.out.println("搜索到订单数量: " + orders.size());
                om.writeValue(pw, orders);
            }

            if ("/order/create".equals(action)) {
                if (userId == null) {
                    pw.write("{\"error\":\"请先登录\"}");
                    return;
                }
                BufferedReader reader = req.getReader();
                Order order = om.readValue(reader, Order.class);
                order.setUserId(userId);
                String result = orderService.createOrder(order);
                om.writeValue(pw, result);
            }

            if ("/order/createFromCart".equals(action)) {
                if (userId == null) {
                    pw.write("{\"error\":\"请先登录\"}");
                    return;
                }
                BufferedReader reader = req.getReader();
                Map<String, String> params = om.readValue(reader, Map.class);
                String address = params.get("address");

                if (address == null || address.trim().isEmpty()) {
                    pw.write("{\"error\":\"请填写收货地址\"}");
                    return;
                }

                String result = orderService.createOrderFromCart(userId, address);
                om.writeValue(pw, result);
            }

            if ("/order/update".equals(action)) {
                BufferedReader reader = req.getReader();
                Order order = om.readValue(reader, Order.class);
                String result = orderService.updateOrderStatus(order.getId(), order.getStatus());
                om.writeValue(pw, result);
            }

            if ("/order/delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                String result = orderService.deleteOrder(id);
                om.writeValue(pw, result);
            }

            if ("/order/delivery".equals(action)) {
                BufferedReader reader = req.getReader();
                Map<String, Integer> params = om.readValue(reader, Map.class);
                int orderId = params.get("id");
                String result = orderService.updateOrderStatus(orderId, "2");
                om.writeValue(pw, result);
            }
        } catch (Exception e) {
            System.out.println("OrderServlet 异常: " + e.getMessage());
            e.printStackTrace();
            pw.write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
