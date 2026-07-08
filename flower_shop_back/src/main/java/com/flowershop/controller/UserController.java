package com.flowershop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowershop.model.User;
import com.flowershop.service.UserService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/user/all", "/user/add", "/user/update", "/user/delete", "/user/search"})
public class UserController extends HttpServlet {
    UserService userService = new UserService();

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String action = req.getServletPath();
        ObjectMapper om = new ObjectMapper();
        PrintWriter pw = resp.getWriter();

        try {
            if ("/user/all".equals(action)) {
                List<User> users = userService.getAllUsers();
                for (User user : users) {
                    user.setToken(null);
                }
                om.writeValue(pw, users);
            }

            if ("/user/add".equals(action)) {
                BufferedReader reader = req.getReader();
                Map<String, Object> map = om.readValue(reader, Map.class);

                User user = new User();
                user.setUserName((String) map.get("userName"));
                user.setPassword((String) map.get("password"));
                user.setPhone((String) map.get("phone"));
                user.setAddress((String) map.get("address"));

                // 处理 role 字段，如果是数字则转换
                Object roleObj = map.get("role");
                if (roleObj instanceof Number) {
                    user.setRole(((Number) roleObj).intValue());
                } else {
                    user.setRole(2); // 默认为普通用户
                }

                String result = userService.addUser(user);
                om.writeValue(pw, result);
            }

            if ("/user/update".equals(action)) {
                BufferedReader reader = req.getReader();
                Map<String, Object> map = om.readValue(reader, Map.class);

                User user = new User();
                Object idObj = map.get("id");
                if (idObj instanceof Number) {
                    user.setId(((Number) idObj).intValue());
                }
                user.setUserName((String) map.get("userName"));
                user.setPassword((String) map.get("password"));
                user.setPhone((String) map.get("phone"));
                user.setAddress((String) map.get("address"));

                String result = userService.updateUser(user);
                om.writeValue(pw, result);
            }

            if ("/user/delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                String result = userService.deleteUser(id);
                om.writeValue(pw, result);
            }

            if ("/user/search".equals(action)) {
                String nameOrPhone = req.getParameter("nameOrPhone");
                List<User> users = userService.searchUser(nameOrPhone);
                for (User user : users) {
                    user.setToken(null);
                }
                om.writeValue(pw, users);
            }
        } catch (Exception e) {
            e.printStackTrace();
            pw.write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
