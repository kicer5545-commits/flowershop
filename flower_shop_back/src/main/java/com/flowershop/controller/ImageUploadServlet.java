package com.flowershop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@WebServlet(urlPatterns = {"/upload/image"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 5,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 20
)
public class ImageUploadServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "uploads/images/";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> result = new HashMap<>();

        try {
            Part filePart = request.getPart("file");
            String fileName = filePart.getSubmittedFileName();

            if (fileName == null || fileName.isEmpty()) {
                result.put("success", false);
                result.put("message", "未找到上传文件");
                objectMapper.writeValue(response.getWriter(), result);
                return;
            }

            String extension = fileName.substring(fileName.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID().toString() + extension;

            String applicationPath = getServletContext().getRealPath("");
            String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;

            File uploadDir = new File(uploadFilePath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String filePath = uploadFilePath + File.separator + uniqueFileName;
            try (InputStream fileContent = filePart.getInputStream();
                 FileOutputStream fos = new FileOutputStream(filePath)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fileContent.read(buffer)) != -1) {
                    fos.write(buffer, 0, length);
                }
            }

            String imageUrl = "/api/" + UPLOAD_DIR + uniqueFileName;
            result.put("success", true);
            result.put("message", "上传成功");
            Map<String, String> data = new HashMap<>();
            data.put("url", imageUrl);
            result.put("data", data);

            System.out.println("图片上传成功，路径: " + imageUrl);
            System.out.println("文件保存位置: " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "上传失败：" + e.getMessage());
        }

        objectMapper.writeValue(response.getWriter(), result);
    }
}
