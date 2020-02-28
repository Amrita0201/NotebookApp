package com.tarento.notebook.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tarento.notebook.dao.NotebookDao;
import com.tarento.notebook.models.Book;
import com.tarento.notebook.models.Note;
import com.tarento.notebook.models.User;
import com.tarento.notebook.service.NotebookService;

@Service
public class NotebookServiceImpl implements NotebookService{
	
	@Autowired
	NotebookDao notebookdao;

	@Override
	public User register(User user) {
		
		
		// TODO Auto-generated method stub
		return (notebookdao.register(user));
	}

	@Override
	public User login(User user){
		// TODO Auto-generated method stub
		return notebookdao.login(user);
	}

	@Override
	public Book addBook(Book book) {
		// TODO Auto-generated method stub
		return notebookdao.addBook(book);
	}

	@Override
	public Note addNoteToBook(Note note) {
		// TODO Auto-generated method stub
		return notebookdao.addNoteToBook(note);
	}
//
//	@Override
//	public List getAllBooks(Book book) {
//		// TODO Auto-generated method stub
//		return notebookdao.getAllBooks(book);
//	}

	@Override
	public String deleteBook(Book book) {
		// TODO Auto-generated method stub
		return notebookdao.deleteBook(book);
	}
	

	
	
	

}
