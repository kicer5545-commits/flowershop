package com.flowershop.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowershop.model.Favorite;
import com.flowershop.service.FavoriteService;
import com.flowershop.utils.TokenUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = {"/favorite/list", "/favorite/add", "/favorite/remove", "/favorite/check"})
public class FavoriteServlet extends HttpServlet {
    private FavoriteService favoriteService = new FavoriteService();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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

        if (userId == null) {
            pw.write("{\"error\":\"请先登录\"}");
            return;
        }

        try {
            if ("/favorite/list".equals(action)) {
                List<Favorite> list = favoriteService.getUserFavorites(userId);
                om.writeValue(pw, list);
            } else if ("/favorite/add".equals(action)) {
                int productId = Integer.parseInt(req.getParameter("productId"));
                String result = favoriteService.addFavorite(userId, productId);
                om.writeValue(pw, result);
            } else if ("/favorite/remove".equals(action)) {
                int productId = Integer.parseInt(req.getParameter("productId"));
                String result = favoriteService.removeFavorite(userId, productId);
                om.writeValue(pw, result);
            } else if ("/favorite/check".equals(action)) {
                int productId = Integer.parseInt(req.getParameter("productId"));
                boolean favorited = favoriteService.isFavorited(userId, productId);
                om.writeValue(pw, favorited);
            }
        } catch (Exception e) {
            e.printStackTrace();
            pw.write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}