package com.flowershop.filter;

import com.flowershop.utils.TokenUtils;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class AccessFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 添加 CORS 跨域支持
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, token");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // 处理预检请求
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String token = request.getHeader("token");
        String uri = request.getRequestURI();

        if (uri.contains("/login") || uri.contains("/register") || uri.contains("/products") || uri.contains("/forgot")|| uri.contains("/upload")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (token != null && TokenUtils.verifyToken(token)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("请先登录");
        }
    }
}
