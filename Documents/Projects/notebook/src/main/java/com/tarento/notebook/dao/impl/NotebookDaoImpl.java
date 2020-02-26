package com.tarento.notebook.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.tarento.notebook.dao.NotebookDao;
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
				PreparedStatement statement = con.prepareStatement("insert into user_data (name, email,password) values  (?,?,?)", returnValColumn);
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
	
	


}
