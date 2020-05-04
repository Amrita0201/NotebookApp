package com.tarento.notebook.service.impl;

import com.tarento.notebook.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.tarento.notebook.dao.NotebookDao;
import com.tarento.notebook.service.NotebookService;

import java.util.List;

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
	public List<Book> getBooks(Long userId) {
		return notebookdao.getAllBooks(userId);
	}

	@Override
	public Note addNoteToBook(Note note, Long userId, Long bookId) {
		return notebookdao.addNoteToBook(note, userId, bookId);
	}

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

	@Override
	public List<NoteResponse> getNotes(Long userId, Long bookId) {
		return notebookdao.getNotes(userId, bookId );
	}

	@Override
	public Boolean updateBook(Book book, Long userId, Long bookId){
		return notebookdao.updateBook(book, userId, bookId);
	}

	@Override
	public Tag addTag(Tag tag, Long bookID, Long noteID, Long userId) {
		return notebookdao.addTag(tag, bookID, noteID, userId);
	}
}
	

	
	
	


