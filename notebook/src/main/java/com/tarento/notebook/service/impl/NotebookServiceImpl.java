package com.tarento.notebook.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.tarento.notebook.dao.NotebookDao;
import com.tarento.notebook.models.Book;
import com.tarento.notebook.models.Note;
import com.tarento.notebook.models.User;
import com.tarento.notebook.service.NotebookService;

@Service
public class NotebookServiceImpl implements NotebookService{
	
	@Autowired
    JdbcTemplate jdbcTemplate;
	
	@Autowired
	NotebookDao notebookdao;

	@Override
	public User register(User user) {
		return (notebookdao.register(user));
	}

	@Override
	public User login(User user){
		return notebookdao.login(user);
	}

	@Override
	public Book addBook(Book book) {
		return notebookdao.addBook(book);
	}

	@Override
	public Note addNoteToBook(Note note, Long userId, Long bookId) {
		return notebookdao.addNoteToBook(note, userId, bookId);
	}
//
//	@Override
//	public List getAllBooks(Book book) {
//		// TODO Auto-generated method stub
//		return notebookdao.getAllBooks(book);
//	}

	@Override
	public Boolean deleteBook(Long userId, Long bookId) {
		return notebookdao.deleteBook(userId, bookId);
	}
	
	@Override
	public Boolean checkUserIdAuthToken(Long userId, String authToken) {
		 String sql = "SELECT EXISTS(SELECT * FROM user_data WHERE id=? AND token=?)";
         Boolean b=jdbcTemplate.queryForObject(sql, Boolean.class,userId, authToken);
         return b;
	}
	
}
	

	
	
	


