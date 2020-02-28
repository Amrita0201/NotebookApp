package com.tarento.notebook.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.tarento.notebook.dao.NotebookDao;
import com.tarento.notebook.models.Book;
import com.tarento.notebook.models.Note;
import com.tarento.notebook.models.User;
import com.tarento.notebook.utility.EncryptData;

@Repository
public class NotebookDaoImpl implements NotebookDao{
	
	final String secretKey = "key21";
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	EncryptData encryptdata;

	@Override
	public User register(User user) {
		try {
			String enpassword = EncryptData.encrypt(user.getPassword(),secretKey) ;
		 //String  = encryptdata.encryptData(user.getPassword());
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				String[] returnValColumn = new String[] { "id" };
				PreparedStatement statement = con.prepareStatement("insert into user_data (name, email,password,creationDate) values  (?,?,?,curdate())", returnValColumn);
				statement.setString(1, user.getUserName());
				statement.setString(2, user.getEmail());
				statement.setString(3, enpassword);
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
		User u=null;
		try {
			
			String sql = "SELECT id, name as 'userName', email, password, isActive as 'isActive', isDeleted as 'isDeleted',creationDate as 'creationDate'\n"
					+ " FROM user_data WHERE EMAIL=? AND PASSWORD=?";
			//String enteredPassword = EncryptData.encrypt(user.getPassword(),secretKey) ;
			u = jdbcTemplate.queryForObject(sql, new Object[] { user.getEmail(),EncryptData.encrypt(user.getPassword(),secretKey) },
					new BeanPropertyRowMapper<User>(User.class));
			u.setPassword(null);
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
				String[] returnValColumn = new String[] { "id" };
				PreparedStatement statement = con.prepareStatement("insert into books (name,created_by,creation_time,update_time) values  (?,?,curdate(),curdate())", returnValColumn);
				statement.setString(1, book.getName());
				statement.setLong(2, book.getCreatedBy());
				return statement;
			}
		}, keyHolder);
		long id = keyHolder.getKey().longValue();
		book.setId(id);
	} catch (Exception e) {
		System.out.println("Error!! " + e.getMessage());
	}
		
		return book;
	}

	@Override
	public Note addNoteToBook(Note note) {
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					String[] returnValColumn = new String[] { "id" };
					PreparedStatement statement = con.prepareStatement("insert into notes (name,content,created_by,creation_date,updated_by,updation_date) values  (?,?,?,curdate(),?,curdate())", returnValColumn);
					statement.setString(1, note.getName());
					statement.setBlob(2, note.getContent());
					statement.setLong(3, note.getCreatedBy());
					statement.setLong(4, note.getUpdatedBy());
					return statement;
				}
			}, keyHolder);
			long id = keyHolder.getKey().longValue();
			note.setId(id);
		} catch (Exception e) {
			System.out.println("Error!! " + e.getMessage());
		}
			
			return note;
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

}
