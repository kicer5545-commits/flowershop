package com.flowershop.controller;

import com.flowershop.model.Product;
import com.flowershop.service.ProductService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/importProducts")
@MultipartConfig(maxFileSize = 10485760, maxRequestSize = 20971520)
public class ImportProductServlet extends HttpServlet {
    private ProductService productService = new ProductService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        Part filePart = req.getPart("file");
        if (filePart == null || filePart.getSize() == 0) {
            resp.getWriter().write("{\"success\":false,\"message\":\"未上传文件或文件为空\"}");
            return;
        }

        String fileName = filePart.getSubmittedFileName();
        if (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls")) {
            resp.getWriter().write("{\"success\":false,\"message\":\"只支持Excel文件格式(.xlsx, .xls)\"}");
            return;
        }

        try (InputStream inputStream = filePart.getInputStream()) {
            Workbook workbook;
            if (fileName.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
            } else {
                workbook = WorkbookFactory.create(inputStream);
            }

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null || sheet.getLastRowNum() == 0) {
                resp.getWriter().write("{\"success\":false,\"message\":\"Excel文件为空或没有数据行\"}");
                return;
            }

            List<Product> products = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isEmptyRow(row)) {
                    continue;
                }

                try {
                    Product product = parseRowToProduct(row, i + 1);
                    if (product != null && validateProduct(product, errors, i + 1)) {
                        products.add(product);
                    }
                } catch (Exception e) {
                    errors.add("第" + (i + 1) + "行解析失败: " + e.getMessage());
                }
            }

            if (products.isEmpty()) {
                resp.getWriter().write("{\"success\":false,\"message\":\"没有有效的产品数据可导入。错误信息: " + String.join("; ", errors) + "\"}");
                return;
            }

            int successCount = 0;
            int failCount = 0;
            List<String> importErrors = new ArrayList<>();

            for (Product p : products) {
                try {
                    String result = productService.addProduct(p);
                    if ("添加成功".equals(result)) {
                        successCount++;
                    } else {
                        failCount++;
                        importErrors.add(p.getName() + ": " + result);
                    }
                } catch (Exception e) {
                    failCount++;
                    importErrors.add(p.getName() + ": " + e.getMessage());
                }
            }

            StringBuilder message = new StringBuilder();
            message.append("导入完成！成功: ").append(successCount).append("条，失败: ").append(failCount).append("条");

            if (!importErrors.isEmpty()) {
                message.append("。失败详情: ").append(String.join("; ", importErrors.subList(0, Math.min(5, importErrors.size()))));
                if (importErrors.size() > 5) {
                    message.append("等").append(importErrors.size()).append("个错误");
                }
            }

            if (!errors.isEmpty()) {
                message.append("。解析警告: ").append(String.join("; ", errors.subList(0, Math.min(3, errors.size()))));
            }

            resp.getWriter().write("{\"success\":" + (successCount > 0) + ",\"message\":\"" + message.toString() + "\"}");

        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("{\"success\":false,\"message\":\"导入失败: " + e.getMessage() + "\"}");
        }
    }

    private boolean isEmptyRow(Row row) {
        for (int i = 0; i < 7; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellStringValue(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private Product parseRowToProduct(Row row, int rowNum) {
        Product product = new Product();

        String name = getCellStringValue(row.getCell(0));
        product.setName(name);

        String category = getCellStringValue(row.getCell(1));
        product.setCategory(category);

        String priceStr = getCellStringValue(row.getCell(2));
        if (priceStr != null && !priceStr.trim().isEmpty()) {
            try {
                product.setPrice(new BigDecimal(priceStr.trim()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("价格格式错误");
            }
        }

        String description = getCellStringValue(row.getCell(3));
        product.setDescription(description);

        String image = getCellStringValue(row.getCell(4));
        if (image != null && !image.trim().isEmpty()) {
            image = image.trim();
            if (!image.startsWith("/api/") && !image.startsWith("http")) {
                if (image.startsWith("/images/")) {
                    image = "/api" + image;
                } else if (!image.startsWith("/")) {
                    image = "/api/images/" + image;
                }
            }
        } else {
            image = "";
        }
        product.setImage(image);

        String stockStr = getCellStringValue(row.getCell(5));
        if (stockStr != null && !stockStr.trim().isEmpty()) {
            try {
                product.setStock((int) Double.parseDouble(stockStr.trim()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("库存格式错误");
            }
        } else {
            product.setStock(0);
        }

        String ratingStr = getCellStringValue(row.getCell(6));
        if (ratingStr != null && !ratingStr.trim().isEmpty()) {
            try {
                BigDecimal rating = new BigDecimal(ratingStr.trim());
                if (rating.compareTo(BigDecimal.ZERO) < 0 || rating.compareTo(new BigDecimal("5")) > 0) {
                    throw new IllegalArgumentException("评分必须在0-5之间");
                }
                product.setRating(rating);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("评分格式错误");
            }
        } else {
            product.setRating(new BigDecimal("0.0"));
        }

        return product;
    }

    private boolean validateProduct(Product product, List<String> errors, int rowNum) {
        boolean isValid = true;

        if (product.getName() == null || product.getName().trim().isEmpty()) {
            errors.add("第" + rowNum + "行: 产品名称不能为空");
            isValid = false;
        }

        if (product.getCategory() == null || product.getCategory().trim().isEmpty()) {
            errors.add("第" + rowNum + "行: 产品分类不能为空");
            isValid = false;
        }

        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("第" + rowNum + "行: 价格必须大于0");
            isValid = false;
        }

        if (product.getStock() < 0) {
            errors.add("第" + rowNum + "行: 库存不能为负数");
            isValid = false;
        }

        return isValid;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == Math.floor(numericValue)) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return "";
        }
    }
}
