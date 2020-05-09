package com.tarento.notebook.dao.impl;

import com.tarento.notebook.dao.NotebookDao;
import com.tarento.notebook.exception.BookNotOfUserException;
import com.tarento.notebook.exception.DuplicateTagForUserException;
import com.tarento.notebook.exception.DuplicateTagInNotesException;
import com.tarento.notebook.models.*;
import com.tarento.notebook.util.EncryptData;
import com.tarento.notebook.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

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
            if (u.getPassword().equals(encryptedPassword) && u.getIsActive() == true) {
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
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String[] returnValColumn = new String[]{"id"};
                    PreparedStatement statement = con.prepareStatement(
                            "insert into notes (name,content,created_by,creation_date, book_id) values  (?,?,?,curdate(),?)", returnValColumn);
//                    jdbcTemplate.update("update books set books.number_of_notes = (select COUNT(book_id) from notes where notes.book_id = ?)", new Object[]{bookId});
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
            jdbcTemplate.update("update books set number_of_notes = (select COUNT(book_id) from notes where notes.book_id = books.id) WHERE id=?", new Object[]{bookId});
            return note;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error!! " + e);
            return null;
        }
    }

    @Override
    public Boolean deleteBook(Long userId, Long bookId) {
        try {
            String sql = "SELECT EXISTS(SELECT * FROM books WHERE created_by=? AND id=?)";
            Boolean b = jdbcTemplate.queryForObject(sql, Boolean.class, userId, bookId);
            if (b == false) {
                throw new BookNotOfUserException(String.format("Book %s not associated with user %s", bookId, userId));
            }
            jdbcTemplate.update("update books set is_deleted=1 where id=?", new Object[]{bookId});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error!! " + e);
            return false;
        }
    }

    @Override
    public List<Book> getAllBooks(Long userId) {
        List<Book> bookList = null;
        try {
            String sql = "SELECT id,name,created_by as 'createdBy', number_of_notes as 'numOfNotes' FROM books WHERE created_by=?";
            bookList = jdbcTemplate.query(sql, new Object[]{userId},
                    new BeanPropertyRowMapper<>(Book.class));
            return bookList;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error!! " + e);
            return bookList;
        }

    }

    @Override
    public List<Note> getNotes(Long userId, Long bookId) {
        List<Note> noteList = null;
        try {
            String sql = "SELECT * FROM books WHERE created_by=? AND id=?";
            jdbcTemplate.queryForObject(sql, new Object[]{userId, bookId},
                    new BeanPropertyRowMapper<>(Book.class));
            String sql1 = "SELECT * FROM notes WHERE created_by=? AND book_id=?";
            noteList = jdbcTemplate.query(sql1, new Object[]{userId, bookId},
                    new BeanPropertyRowMapper<>(Note.class));
            if (noteList.size() == 0) {
                return null;
            }
            return noteList;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error!! " + e);
            return null;
        }
    }

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

    @Override
    public Boolean updateBook(Book book, Long userId, Long bookId) {
        try {
            String sql = "SELECT EXISTS(SELECT * FROM books WHERE created_by=? AND id=?)";
            Boolean b = jdbcTemplate.queryForObject(sql, Boolean.class, userId, bookId);
            if (b == false) {
                throw new BookNotOfUserException(String.format("Book %s not associated with user %s", bookId, userId));
            }
            jdbcTemplate.update("update books set name=? where id=?", new Object[]{book.getName(), bookId});
            return true;
        } catch (BookNotOfUserException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error!! " + e);
            return false;
        }
    }

    @Override
    public List<Book> getBooksIfStarred(Long userId) {
        List<Book> bookList = null;
        try {
            String sql = "select book_id from notes where is_starred=1 AND created_by=?;";
            List<Note> notesList = jdbcTemplate.query(sql, new Object[]{userId},
                    new BeanPropertyRowMapper<>(Note.class));
            Set<Note> bookIdSet = new HashSet<>(notesList);
            notesList.clear();
            notesList.addAll(bookIdSet);
            bookList = jdbcTemplate.query(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    String sql = "select * from books where ";
                    StringBuffer sqlSB = new StringBuffer(sql);
                    boolean flag = false;
                    for(Note note: notesList) {
                        if (flag) {
                            sqlSB.append(" or ");
                        }
                        sqlSB.append("(id=").append(note.getBookId()).append(" and ").append("created_by=").append(userId).append(")");
                        flag = true;
                    }
                    PreparedStatement ps = connection.prepareStatement(sqlSB.toString());
                    return ps;
                }
            }, new BeanPropertyRowMapper<>(Book.class));
            return bookList;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error!! " + e);
            return bookList;
        }
    }

    @Override
    public List<Book> getBooksByName(Long userId, String name) {
        List<Book> bookList = null;
        try {
            String sql = "SELECT id,name,created_by as 'createdBy', number_of_notes as 'numOfNotes' FROM books WHERE created_by=? AND name=?";
            bookList = jdbcTemplate.query(sql, new Object[]{userId, name},
                    new BeanPropertyRowMapper<>(Book.class));
            return bookList;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error!! " + e);
            return bookList;
        }
    }

    @Override
    public List<Book> getBooksByTag(Long userId, String tag) {
        List<Book> bookList = null;
        try {
            String sql = "select * from tags where name=?";
            List<Tag> tagList = jdbcTemplate.query(sql, new Object[]{tag},
                    new BeanPropertyRowMapper<>(Tag.class));
            if (tagList.size() == 0) {
                return null;
            }
            Long tagId = tagList.get(0).getId();
            sql = "select note_id from note_tag_map where tag_id=?";
            List<NoteTagMap> noteTagMapList = jdbcTemplate.query(sql, new Object[]{tagId},
                    BeanPropertyRowMapper.newInstance(NoteTagMap.class));
            List<Note> notesList = jdbcTemplate.query(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    String sql = "select * from notes where ";
                    StringBuffer sqlSB = new StringBuffer(sql);
                    boolean flag = false;
                    for(NoteTagMap noteTagMap: noteTagMapList) {
                        if (flag) {
                            sqlSB.append(" or ");
                        }
                        sqlSB.append("(id=").append(noteTagMap.getNoteId()).append(" and ").append("created_by=").append(userId).append(")");
                        flag = true;
                    }
                    PreparedStatement ps = connection.prepareStatement(sqlSB.toString());
                    return ps;
                }
            }, new BeanPropertyRowMapper<>(Note.class));
            Set<Note> bookIdSet = new HashSet<>(notesList);
            notesList.clear();
            notesList.addAll(bookIdSet);
            bookList = jdbcTemplate.query(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    String sql = "select * from books where ";
                    StringBuffer sqlSB = new StringBuffer(sql);
                    boolean flag = false;
                    for(Note note: notesList) {
                        if (flag) {
                            sqlSB.append(" or ");
                        }
                        sqlSB.append("(id=").append(note.getBookId()).append(" and ").append("created_by=").append(userId).append(")");
                        flag = true;
                    }
                    PreparedStatement ps = connection.prepareStatement(sqlSB.toString());
                    return ps;
                }
            }, new BeanPropertyRowMapper<>(Book.class));
            return bookList;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error!! " + e);
            return bookList;
        }
    }

    @Override
    public Tag addTag(Tag tag, Long bookID, Long noteID, Long userId) {
        List<Tag> t = null;
        String sql = "SELECT EXISTS(SELECT * FROM notes WHERE created_by=? AND id=? AND book_id=?)";
        Boolean b = jdbcTemplate.queryForObject(sql, Boolean.class, userId, noteID, bookID);
        if (!b) {
            return null;
        }
        sql = "SELECT * FROM tags WHERE name=?";
        t = jdbcTemplate.query(sql, new Object[]{tag.getName()},
                new BeanPropertyRowMapper<>(Tag.class));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        if (t.size() == 0) {
            jdbcTemplate.update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String[] returnValColumn = new String[]{"id"};
                    PreparedStatement statement = con.prepareStatement("insert into tags (name) values  (?)", returnValColumn);
                    statement.setString(1, tag.getName());
                    return statement;
                }
            }, keyHolder);
            Long id = keyHolder.getKey().longValue();
            t.add(new Tag());
            t.get(0).setName(tag.getName());
            t.get(0).setId(id);
        }
        tag.setId(t.get(0).getId());
        try {
            jdbcTemplate.update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String[] returnValColumn = new String[]{"id"};
                    PreparedStatement statement = con.prepareStatement("insert into note_tag_map (tag_id, note_id) values  (?,?)", returnValColumn);
                    statement.setLong(1, tag.getId());
                    statement.setLong(2, noteID);
                    return statement;
                }
            }, keyHolder);
        } catch (DuplicateKeyException e) {
            throw new DuplicateTagInNotesException(e.getMessage(), e.getCause());
        }
        try {
            jdbcTemplate.update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String[] returnValColumn = new String[]{"id"};
                    PreparedStatement statement = con.prepareStatement("insert into user_tag_map (tag_id, user_id) values  (?,?)", returnValColumn);
                    statement.setLong(1, tag.getId());
                    statement.setLong(2, userId);
                    return statement;
                }
            }, keyHolder);
        } catch (DuplicateKeyException e) {
            throw new DuplicateTagForUserException(e.getMessage(), e.getCause());
        }
        return tag;
    }

    @Override
    public NoteResponse getNoteById(Long userId, Long bookId, Long noteId) {
        List<Note> noteList = null;
        try {
            String sql = "SELECT * FROM books WHERE created_by=? AND id=?";
            jdbcTemplate.queryForObject(sql, new Object[]{userId, bookId},
                    new BeanPropertyRowMapper<>(Book.class));
            String sql1 = "SELECT * FROM notes WHERE created_by=? AND book_id=? AND id=?";
            noteList = jdbcTemplate.query(sql1, new Object[]{userId, bookId, noteId},
                    new BeanPropertyRowMapper<>(Note.class));
            if (noteList.size() == 0) {
                return null;
            }
            NoteResponse noteResponse = new NoteResponse();
            noteResponse.setNote(noteList.get(0));
            sql = "select * from note_tag_map where note_id=?";
            List<NoteTagMap> noteTagMapList = jdbcTemplate.query(sql, new Object[]{noteId},
                    new BeanPropertyRowMapper<>(NoteTagMap.class));
            if (noteTagMapList.size() != 0) {
                List<Tag> tagList = jdbcTemplate.query(new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        String sql = "select * from tags where ";
                        StringBuffer sqlSB = new StringBuffer(sql);
                        boolean flag = false;
                        for (NoteTagMap noteTagMap : noteTagMapList) {
                            if (flag) {
                                sqlSB.append(" or ");
                            }
                            sqlSB.append("id=").append(noteTagMap.getTagId());
                            flag = true;
                        }
                        PreparedStatement ps = connection.prepareStatement(sqlSB.toString());
                        return ps;
                    }
                }, new BeanPropertyRowMapper<>(Tag.class));
                noteResponse.setTags(tagList);
            }
            return noteResponse;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error!! " + e);
            return null;
        }
    }

    @Override
    public List<Tag> getTagsByUserId(Long userId) {
        try {
            String sql = "select * from user_tag_map where user_id=?";
            List<UserTagMap> userTagMapList = jdbcTemplate.query(sql, new Object[]{userId},
                    new BeanPropertyRowMapper<>(UserTagMap.class));
            if (userTagMapList.size() != 0) {
                List<Tag> tagList = jdbcTemplate.query(new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        String sql = "select * from tags where";
                        StringBuffer sqlSB = new StringBuffer(sql);
                        boolean flag = false;
                        for (UserTagMap userTagMap : userTagMapList) {
                            if (flag) {
                                sqlSB.append(" or");
                            }
                            sqlSB.append(" id=").append(userTagMap.getTagId());
                            flag = true;
                        }
                        PreparedStatement ps = connection.prepareStatement(sqlSB.toString());
                        return ps;
                    }
                }, BeanPropertyRowMapper.newInstance(Tag.class));
                return tagList;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error!! " + e);
            return null;

        }
    }
}
