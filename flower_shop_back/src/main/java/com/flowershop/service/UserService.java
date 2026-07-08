package com.flowershop.service;

import com.flowershop.dao.UserDao;
import com.flowershop.model.User;
import com.flowershop.utils.TokenUtils;

import java.sql.SQLException;
import java.util.List;

public class UserService {
    private UserDao userDao = new UserDao();

    public String register(User user) {
        try {
            // 检查用户名是否已存在
            User exist = userDao.findByUserName(user.getUserName());
            if (exist != null) {
                return "用户名已存在";
            }
            int result = userDao.save(user);
            if (result > 0) {
                return "注册成功";
            } else {
                return "注册失败";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "注册失败：" + e.getMessage();
        }
    }

    // 添加用户（管理员添加）
    public String addUser(User user) {
        try {
            // 检查用户名是否已存在
            User exist = userDao.findByUserName(user.getUserName());
            if (exist != null) {
                return "用户名已存在";
            }
            // 检查手机号是否已存在
            User existPhone = userDao.findByPhone(user.getPhone());
            if (existPhone != null) {
                return "手机号已被注册";
            }
            int result = userDao.save(user);
            if (result > 0) {
                return "添加成功";
            } else {
                return "添加失败";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "添加失败：" + e.getMessage();
        }
    }

    public User login(String userInfo, String password) {
        try {
            User user = userDao.findByUserNameAndPassword(userInfo, password);
            if (user != null) {
                String token = TokenUtils.createToken(user);
                user.setToken(token);
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User getUserById(int id) {
        try {
            return userDao.findById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<User> getAllUsers() {
        try {
            return userDao.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 搜索用户
    public List<User> searchUser(String keyword) {
        try {
            return userDao.searchByNameOrPhone(keyword);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String updateUser(User user) {
        try {
            int result = userDao.update(user);
            if (result > 0) {
                return "更新成功";
            } else {
                return "更新失败";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "更新失败：" + e.getMessage();
        }
    }

    public String deleteUser(int id) {
        try {
            int result = userDao.delete(id);
            if (result > 0) {
                return "删除成功";
            } else {
                return "删除失败";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "删除失败：" + e.getMessage();
        }
    }

    public boolean verifyUserByNameAndPhone(String userName, String phone) {
        try {
            User user = userDao.findByUserNameAndPhone(userName, phone);
            System.out.println("验证查询结果: " + (user != null ? "用户存在" : "用户不存在"));
            return user != null;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String resetPassword(String userName, String phone, String newPassword) {
        try {
            User user = userDao.findByUserNameAndPhone(userName, phone);
            if (user == null) {
                return "用户不存在或信息不匹配";
            }
            user.setPassword(newPassword);
            int result = userDao.update(user);
            return result > 0 ? "重置成功" : "重置失败";
        } catch (SQLException e) {
            e.printStackTrace();
            return "重置失败：" + e.getMessage();
        }
    }
}
