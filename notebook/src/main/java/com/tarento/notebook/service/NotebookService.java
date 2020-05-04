package com.tarento.notebook.service;

import com.tarento.notebook.models.*;

import java.util.List;

public interface NotebookService {
	

	public User register(User user);
	
	public User login(User user);
	
	public Book addBook(Book book);

	public List<Book> getBooks(Long userId);

	public Note addNoteToBook(Note note, Long userId, Long bookId);

	public Boolean deleteBook(Long userId, Long bookId);
	
	public Boolean checkUserIdAuthToken(Long userId, String authToken);

	public Boolean updateBook(Book book, Long userId, Long bookId);

	public List<NoteResponse> getNotes(Long userId, Long BookId);

	Tag addTag(Tag tag,Long bookID, Long noteID, Long userId);
}
