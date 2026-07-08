package com.flowershop.dao;

import com.flowershop.model.User;
import com.flowershop.utils.DruidUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.List;

public class UserDao {
    private QueryRunner queryRunner = new QueryRunner(DruidUtils.getDataSource());

    /**
     * 根据用户名和密码查询用户
     */
    public User findByUserNameAndPassword(String userName, String password) throws SQLException {
        String sql = "SELECT id, user_name AS userName, password, phone, address, role FROM t_user WHERE user_name = ? AND password = ?";
        return queryRunner.query(sql, new BeanHandler<>(User.class), userName, password);
    }

    /**
     * 根据 ID 查询用户
     */
    public User findById(int id) throws SQLException {
        String sql = "SELECT id, user_name AS userName, password, phone, address, role FROM t_user WHERE id = ?";
        return queryRunner.query(sql, new BeanHandler<>(User.class), id);
    }

    /**
     * 查询所有用户
     */
    public List<User> findAll() throws SQLException {
        String sql = "SELECT id, user_name AS userName, password, phone, address, role FROM t_user";
        return queryRunner.query(sql, new BeanListHandler<>(User.class));
    }

    /**
     * 更新用户信息
     */
    public int update(User user) throws SQLException {
        String sql = "UPDATE t_user SET user_name=?, password=?, phone=?, address=? WHERE id=?";
        return queryRunner.update(sql, user.getUserName(), user.getPassword(),
                user.getPhone(), user.getAddress(), user.getId());
    }

    /**
     * 根据 ID 删除用户
     */
    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM t_user WHERE id = ?";
        return queryRunner.update(sql, id);
    }

    /**
     * 根据用户名和手机号查询用户（用于忘记密码验证）
     */
    public User findByUserNameAndPhone(String userName, String phone) throws SQLException {
        String sql = "SELECT id, user_name AS userName, password, phone, address, role FROM t_user WHERE user_name = ? AND phone = ?";
        return queryRunner.query(sql, new BeanHandler<>(User.class), userName, phone);
    }

    /**
     * 根据用户名查询用户（用于检查重复）
     */
    public User findByUserName(String userName) throws SQLException {
        String sql = "SELECT id, user_name AS userName, password, phone, address, role FROM t_user WHERE user_name = ?";
        return queryRunner.query(sql, new BeanHandler<>(User.class), userName);
    }

    /**
     * 根据手机号查询用户（用于检查重复）
     */
    public User findByPhone(String phone) throws SQLException {
        String sql = "SELECT id, user_name AS userName, password, phone, address, role FROM t_user WHERE phone = ?";
        return queryRunner.query(sql, new BeanHandler<>(User.class), phone);
    }

    /**
     * 根据用户名或手机号搜索用户
     */
    public List<User> searchByNameOrPhone(String keyword) throws SQLException {
        String sql = "SELECT id, user_name AS userName, password, phone, address, role FROM t_user WHERE user_name LIKE ? OR phone LIKE ?";
        return queryRunner.query(sql, new BeanListHandler<>(User.class), "%" + keyword + "%", "%" + keyword + "%");
    }

    /**
     * 保存用户
     */
    public int save(User user) throws SQLException {
        String sql = "INSERT INTO t_user(user_name, password, phone, address, role) VALUES(?,?,?,?,?)";
        return queryRunner.update(sql, user.getUserName(), user.getPassword(),
                user.getPhone(), user.getAddress(), user.getRole());
    }
}
