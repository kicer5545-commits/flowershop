package com.flowershop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowershop.model.Product;
import com.flowershop.service.ProductService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/products", "/product/detail", "/product/add", "/product/update", "/product/delete"})
public class ProductServlet extends HttpServlet {
    ProductService productService = new ProductService();

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String action = req.getServletPath();
        ObjectMapper om = new ObjectMapper();
        PrintWriter pw = resp.getWriter();

        try {
            if ("/products".equals(action)) {
                String category = req.getParameter("category");
                List<Product> products;
                if (category == null || "all".equals(category)) {
                    products = productService.getAllProducts();
                } else {
                    products = productService.getProductsByCategory(category);
                }
                om.writeValue(pw, products);
            }

            if ("/product/detail".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                Product product = productService.getProductById(id);
                om.writeValue(pw, product);
            }

            if ("/product/add".equals(action)) {
                BufferedReader reader = req.getReader();
                Map<String, Object> map = om.readValue(reader, Map.class);

                Product product = new Product();
                product.setName((String) map.get("name"));
                product.setCategory((String) map.get("category"));
                product.setPrice(new BigDecimal(map.get("price").toString()));
                product.setDescription((String) map.get("description"));
                product.setImage((String) map.get("image"));
                product.setStock(((Number) map.get("stock")).intValue());
                product.setRating(new BigDecimal(map.get("rating").toString()));

                String result = productService.addProduct(product);
                om.writeValue(pw, result);
            }

            if ("/product/update".equals(action)) {
                BufferedReader reader = req.getReader();
                Map<String, Object> map = om.readValue(reader, Map.class);

                Product product = new Product();
                product.setId(((Number) map.get("id")).intValue());
                product.setName((String) map.get("name"));
                product.setCategory((String) map.get("category"));
                product.setPrice(new BigDecimal(map.get("price").toString()));
                product.setDescription((String) map.get("description"));
                product.setImage((String) map.get("image"));
                product.setStock(((Number) map.get("stock")).intValue());
                product.setRating(new BigDecimal(map.get("rating").toString()));

                String result = productService.updateProduct(product);
                om.writeValue(pw, result);
            }

            if ("/product/delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                String result = productService.deleteProduct(id);
                om.writeValue(pw, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            pw.write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
