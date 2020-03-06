package com.tarento.notebook.dao.impl;

import com.tarento.notebook.dao.NotebookDao;
import com.tarento.notebook.exception.BookNotOfUserException;
import com.tarento.notebook.models.Book;
import com.tarento.notebook.models.Note;
import com.tarento.notebook.models.User;
import com.tarento.notebook.util.EncryptData;
import com.tarento.notebook.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

@Repository
public class NotebookDaoImpl implements NotebookDao {

    @Value("{jwt.secret.key}")
    String secretKey;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    EncryptData encryptdata;

    @Autowired
    JwtUtil jwtUtil;

    @Override
    public User register(User user) {
        try {
            String encryptedPassword = EncryptData.encrypt(user.getPassword(), secretKey);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String[] returnValColumn = new String[]{"id"};
                    PreparedStatement statement = con.prepareStatement("insert into user_data (name, email,password,creationDate) values  (?,?,?,curdate())", returnValColumn);
                    statement.setString(1, user.getName());
                    statement.setString(2, user.getEmail());
                    statement.setString(3, encryptedPassword);
                    return statement;
                }
            }, keyHolder);
            long id = keyHolder.getKey().longValue();
            user.setId(id);
        } catch (Exception e) {
            System.out.println("Error!! " + e.getMessage());
        }

        return user;
    }

    @Override
    public User login(User user) {
        User u = null;
        try {
            String sql = "SELECT id, name, email, password, isActive as 'isActive', isDeleted as 'isDeleted',creationDate as 'creationDate'\n"
                    + " FROM user_data WHERE EMAIL=?";
            u = jdbcTemplate.queryForObject(sql, new Object[]{user.getEmail()},
                    new BeanPropertyRowMapper<>(User.class));
            String encryptedPassword = EncryptData.encrypt(user.getPassword(), secretKey);
            if(u.getPassword().equals(encryptedPassword)) {
                String jwt = jwtUtil.generateToken(new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        new ArrayList<>()
                ), u.getId());
                u.setToken(jwt);
                sql = "update user_data set token='" + u.getToken() + "' where id=" + u.getId();
                jdbcTemplate.update(sql);
                u.setPassword(null);
            }
            return u;
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Error!! " + e.getMessage());
        }
        return u;
    }
//    @Override
//    public User login(User user) {
//        User u = null;
//        try {
//
//            String sql = "SELECT id, name as 'userName', email, password, isActive as 'isActive', isDeleted as 'isDeleted',creationDate as 'creationDate'\n"
//                    + " FROM user_data WHERE EMAIL=? AND PASSWORD=?";
//            //String enteredPassword = EncryptData.encrypt(user.getPassword(),secretKey) ;
//            u = jdbcTemplate.queryForObject(sql, new Object[]{user.getEmail(), EncryptData.encrypt(user.getPassword(), secretKey)},
//                    new BeanPropertyRowMapper<User>(User.class));
//            u.setPassword(null);
//            return u;
//        } catch (Exception e) {
//            // TODO: handle exception
//            System.out.println("Error!! " + e.getMessage());
//        }
//        return u;
//    }

    @Override
    public Book addBook(Book book) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String[] returnValColumn = new String[]{"id"};
                    PreparedStatement statement = con.prepareStatement("insert into books (name,created_by,created_time,updated_time) values  (?,?,?,?)", returnValColumn);
                    statement.setString(1, book.getName());
                    statement.setLong(2, book.getCreatedBy());
                    statement.setLong(3, new Date().getTime());
                    statement.setLong(4, new Date().getTime());
                    return statement;
                }
            }, keyHolder);
            long id = keyHolder.getKey().longValue();
            book.setId(id);
        } catch (Exception e) {
            System.out.println("Error!! " + e.getMessage());
            return null;
        }

        return book;
    }

    @Override
    public Note addNoteToBook(Note note, Long userId, Long bookId) {
        Book book = null;
        try {
            String sql = "SELECT * FROM books WHERE created_by=? AND id=?";
            book = jdbcTemplate.queryForObject(sql, new Object[]{userId, bookId},
                    new BeanPropertyRowMapper<>(Book.class));
            if (book == null) {
                throw new BookNotOfUserException(String.format("Book %s not associated with user %s", bookId, userId));
            }
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String[] returnValColumn = new String[]{"id"};
                    PreparedStatement statement = con.prepareStatement(
                            "insert into notes (name,content,created_by,creation_date, book_id) values  (?,?,?,curdate(),?)", returnValColumn);
                    statement.setString(1, note.getName());
                    statement.setString(2, note.getContent());
                    statement.setLong(3, note.getCreatedBy());
                    statement.setLong(4, bookId);
                    return statement;
                }
            }, keyHolder);
            long id = keyHolder.getKey().longValue();
            note.setBookId(bookId);
            note.setId(id);
            return note;
        } catch (BookNotOfUserException e) {
            System.out.println("Error: " + e.getMessage());
            note.setId(-1L);
            return note;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error!! " + e);
            return null;
        }
    }

    @Override
    public String deleteBook(Book book) {
        // TODO Auto-generated method stub
        return null;
    }

//	@Override
//	public Book getAllBooks(Book book) {
//		// TODO Auto-generated method stub
//		return null;
//	}

    @Override
    public User findByEmail(String email) {
        User u = null;
        try {
            String sql = "SELECT id, name as 'userName', email, password, isActive as 'isActive', isDeleted as 'isDeleted',creationDate as 'creationDate'\n"
                    + " FROM user_data WHERE EMAIL=?";
            u = jdbcTemplate.queryForObject(sql, new Object[]{email},
                    new BeanPropertyRowMapper<>(User.class));
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Error!! " + e.getMessage());
        } finally {
            return u;
        }
    }
}
