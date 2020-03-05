package com.tarento.notebook.dao;

import com.tarento.notebook.models.Book;
import com.tarento.notebook.models.Note;
import com.tarento.notebook.models.User;

public interface NotebookDao {
	public User register(User user);
	public User login(User user);
	public Book addBook(Book book);
	public Note addNoteToBook(Note note);
//	public List getAllBooks(Book book);
	public String deleteBook(Book book);
	public User findByEmail(String email);
	
}
