package com.tarento.notebook.service.impl;

import com.tarento.notebook.dao.NotebookDao;
import com.tarento.notebook.models.Book;
import com.tarento.notebook.models.Note;
import com.tarento.notebook.models.User;
import com.tarento.notebook.service.NotebookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotebookServiceImpl implements NotebookService{
	
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
	public String deleteBook(Book book) {
		return notebookdao.deleteBook(book);
	}
	

	
	
	

}
