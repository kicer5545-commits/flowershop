package com.flowershop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowershop.service.UserService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebServlet(urlPatterns = {"/forgot/verify", "/forgot/reset"})
public class ForgotPasswordServlet extends HttpServlet {
    private UserService userService = new UserService();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String action = req.getServletPath();
        ObjectMapper om = new ObjectMapper();
        PrintWriter pw = resp.getWriter();

        // 添加日志，确认请求到达
        System.out.println("ForgotPasswordServlet 收到请求: " + action);

        try {
            if ("/forgot/verify".equals(action)) {
                String userName = req.getParameter("userName");
                String phone = req.getParameter("phone");
                System.out.println("验证请求: userName=" + userName + ", phone=" + phone);
                boolean valid = userService.verifyUserByNameAndPhone(userName, phone);
                System.out.println("验证结果: " + valid);
                om.writeValue(pw, valid);
                return;
            }
            else if ("/forgot/reset".equals(action)) {
                // 读取POST请求体
                BufferedReader reader = req.getReader();
                Map<String, String> params = om.readValue(reader, Map.class);
                String userName = params.get("userName");
                String phone = params.get("phone");
                String newPassword = params.get("newPassword");

                System.out.println("重置密码请求: userName=" + userName + ", phone=" + phone);

                String result = userService.resetPassword(userName, phone, newPassword);
                om.writeValue(pw, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            pw.write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}