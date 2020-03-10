package com.tarento.notebook.service;

import com.tarento.notebook.models.Book;
import com.tarento.notebook.models.Note;
import com.tarento.notebook.models.User;

public interface NotebookService {
	

	public User register(User user);
	
	public User login(User user);
	
	public Book addBook(Book book);

	public Note addNoteToBook(Note note, Long userId, Long bookId);

	public Boolean deleteBook(Long userId, Long bookId);
	
	public Boolean checkUserIdAuthToken(Long userId, String authToken); 
		
}
