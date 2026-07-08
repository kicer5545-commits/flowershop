package com.flowershop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowershop.model.User;
import com.flowershop.service.UserService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

@WebServlet(urlPatterns = {"/register", "/login"})
public class LoginOrRegServlet extends HttpServlet {
    UserService userService = new UserService();

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String action = req.getServletPath();
        ObjectMapper om = new ObjectMapper();
        PrintWriter pw = resp.getWriter();

        if ("/register".equals(action)) {
            // 读取请求体
            StringBuilder sb = new StringBuilder();
            String line;
            try (BufferedReader reader = req.getReader()) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            String body = sb.toString();
            System.out.println("注册请求体: " + body);

            if (body == null || body.trim().isEmpty()) {
                om.writeValue(pw, "请求数据为空");
                return;
            }

            User user = om.readValue(body, User.class);
            user.setRole(2);  // 普通用户

            String registerMsg = userService.register(user);
            System.out.println("注册结果: " + registerMsg);
            om.writeValue(pw, registerMsg);
        }

        if ("/login".equals(action)) {
            String userInfo = req.getParameter("userInfo");
            String password = req.getParameter("password");
            boolean isAdminLogin = Boolean.parseBoolean(req.getParameter("isAdminLogin"));

            User loginUser = userService.login(userInfo, password);

//            管理员权限检查
            if (isAdminLogin) {
                if (loginUser != null && loginUser.getRole() != 1) {
                    om.writeValue(pw, "您没有权限！");
                    return;
                }
            }

            om.writeValue(pw, loginUser);
        }
    }
}
