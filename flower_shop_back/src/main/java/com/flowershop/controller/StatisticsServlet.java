package com.flowershop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowershop.service.StatisticsService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = {"/admin/statistics/categoryCount", "/admin/statistics/orderTrend"})
public class StatisticsServlet extends HttpServlet {
    private StatisticsService statisticsService = new StatisticsService();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String action = req.getServletPath();
        ObjectMapper om = new ObjectMapper();
        PrintWriter pw = resp.getWriter();

        try {
            if ("/admin/statistics/categoryCount".equals(action)) {
                Map<String, Integer> data = statisticsService.getCategoryCount();
                om.writeValue(pw, data);
            } else if ("/admin/statistics/orderTrend".equals(action)) {
                Map<String, Object> data = statisticsService.getLast7DaysOrderTrend();
                om.writeValue(pw, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            pw.write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}