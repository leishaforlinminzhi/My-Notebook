package com.example.backend.dao;

import com.example.backend.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserDaoImple  implements UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int insert(User user) {
        String sql = "insert into Users(id,name,password) values(?,?,?)";
        return this.jdbcTemplate.update(
                sql,
                user.getId(),
                user.getUsername(),
                user.getPassword()
        );
    }

    @Override
    public int deleteById(Integer id) {
        String sql = "delete from Users where id = ?";
        return this.jdbcTemplate.update(sql, id);
    }

    @Override
    public int update(User user) {
        String sql = "update Users set password = ? where id = ?";
        return this.jdbcTemplate.update(
                sql,
                user.getPassword(),
                user.getId()
        );
    }

    @Override
    public User getById(Integer id) {
        String sql = "select * from Users where id = ?";
        return this.jdbcTemplate.queryForObject(sql, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUsername(resultSet.getString("name"));
                user.setPassword(resultSet.getString("password"));
                return user;
            }
        }, id);
    }

    @Override
    public User login(String name, String password) {
        System.out.println(name);
        System.out.println(password);
        String sql = "select * from Users where name=? and password=?";
        return this.jdbcTemplate.queryForObject(sql, (resultSet, i) -> {
            User user = new User();
            user.setId(resultSet.getInt("id"));
            user.setUsername(resultSet.getString("name"));
            user.setPassword(resultSet.getString("password"));
            return user;
        }, name,password);
    }

    @Override
    public List<Integer> getAllUserIds(){
        String sql = "select id from Users";
        return this.jdbcTemplate.queryForList(sql, Integer.class);
    }

    @Override
    public List<String> getAllUsernames(){
        String sql = "select name from Users";
        return this.jdbcTemplate.queryForList(sql, String.class);
    }
}
