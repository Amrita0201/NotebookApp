package com.tarento.notebook.dao;

import com.tarento.notebook.models.*;

import java.util.List;

public interface NotebookDao {
	public User register(User user);
	public User login(User user);
	public Book addBook(Book book);
	public Note addNoteToBook(Note note, Long userId, Long bookId);
	public List<Book> getAllBooks(Long userId);
	public Boolean deleteBook(Long userId, Long bookId);
	public User findByEmail(String email);
	public Boolean updateBook(Book book, Long userId, Long bookId);
	public List<Note> getNotes(Long userId, Long bookId);
	public Tag addTag(Tag tag, Long bookID, Long noteID, Long userId);
	public List<Book> getBooksIfStarred(Long userId);
	public List<Book> getBooksByName(Long userId, String name);
	public List<Book> getBooksByTag(Long userId, String tag);
	NoteResponse getNoteById(Long userId, Long bookId, Long noteId);
	List<Tag> getTagsByUserId(Long userId);

}
