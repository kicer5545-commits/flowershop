package com.flowershop.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowershop.model.Cart;
import com.flowershop.service.CartService;
import com.flowershop.utils.TokenUtils;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.List;

@WebServlet(urlPatterns = {"/cart/list", "/cart/add", "/cart/update", "/cart/delete", "/cart/clear"})
public class CartServlet extends HttpServlet {
    CartService cartService = new CartService();

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String action = req.getServletPath();
        ObjectMapper om = new ObjectMapper();
        PrintWriter pw = resp.getWriter();

        String token = req.getHeader("token");
        Integer userId = null;

        if (token != null && TokenUtils.verifyToken(token)) {
            DecodedJWT jwt = com.auth0.jwt.JWT.decode(token);
            userId = jwt.getClaim("id").asInt();
        }

        try {
            if ("/cart/list".equals(action)) {
                if (userId == null) {
                    pw.write("{\"error\":\"请先登录\"}");
                    return;
                }
                List<Cart> carts = cartService.getCartByUserId(userId);
                om.writeValue(pw, carts);
            }

            if ("/cart/add".equals(action)) {
                if (userId == null) {
                    pw.write("{\"error\":\"请先登录\"}");
                    return;
                }
                BufferedReader reader = req.getReader();
                Cart cart = om.readValue(reader, Cart.class);
                cart.setUserId(userId);
                String result = cartService.addToCart(cart);
                om.writeValue(pw, result);
            }

            if ("/cart/update".equals(action)) {
                if (userId == null) {
                    pw.write("{\"error\":\"请先登录\"}");
                    return;
                }
                BufferedReader reader = req.getReader();
                Cart cart = om.readValue(reader, Cart.class);
                Cart existCart = cartService.getCartByUserId(userId).stream()
                        .filter(c -> c.getId() == cart.getId())
                        .findFirst()
                        .orElse(null);

                if (existCart == null) {
                    pw.write("{\"error\":\"无权操作\"}");
                    return;
                }
                String result = cartService.updateCart(cart);
                om.writeValue(pw, result);
            }

            if ("/cart/delete".equals(action)) {
                if (userId == null) {
                    pw.write("{\"error\":\"请先登录\"}");
                    return;
                }
                int id = Integer.parseInt(req.getParameter("id"));
                List<Cart> carts = cartService.getCartByUserId(userId);
                boolean owned = carts.stream().anyMatch(c -> c.getId() == id);

                if (!owned) {
                    pw.write("{\"error\":\"无权操作\"}");
                    return;
                }
                String result = cartService.deleteFromCart(id);
                om.writeValue(pw, result);
            }

            if ("/cart/clear".equals(action)) {
                if (userId == null) {
                    pw.write("{\"error\":\"请先登录\"}");
                    return;
                }
                String result = cartService.clearCart(userId);
                om.writeValue(pw, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            pw.write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
