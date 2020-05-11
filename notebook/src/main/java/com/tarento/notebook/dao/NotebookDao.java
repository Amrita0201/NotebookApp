package com.tarento.notebook.dao;

import com.tarento.notebook.models.*;

import java.util.List;

public interface NotebookDao {
	User register(User user);
	User login(User user);
	Book addBook(Book book);
	Note addNoteToBook(Note note, Long userId, Long bookId);
	List<Book> getAllBooks(Long userId);
	Boolean deleteBook(Long userId, Long bookId);
	User findByEmail(String email);
	Boolean updateBook(Book book, Long userId, Long bookId);
	List<Note> getNotes(Long userId, Long bookId);
	Tag addTag(Tag tag, Long bookID, Long noteID, Long userId);
	List<Book> getBooksIfStarred(Long userId);
	List<Book> getBooksByName(Long userId, String name);
	List<Book> getBooksByTag(Long userId, String tag);
	NoteResponse getNoteById(Long userId, Long bookId, Long noteId);
	List<Tag> getTagsByUserId(Long userId);
	Boolean deleteNote(Long userId, Long bookId, Long noteId);

}
