package com.tarento.notebook.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tarento.notebook.models.Book;
import com.tarento.notebook.models.Note;
import com.tarento.notebook.models.User;
import com.tarento.notebook.service.NotebookService;
import com.tarento.notebook.service.impl.NotebookServiceImpl;

@RestController

public class NotebookController {
	
	@Autowired
	NotebookService notebookservice;

	@PostMapping(value = "/addUser")
	public User InsertUser(@RequestBody User user) {
		//return (notebookserviceimpl.register(user.getUserName(),user.getEmail(),user.getPassword()));
		return (notebookservice.register(user));
	}
	
	@PostMapping(value = "/login")
	public User Login(@RequestBody User user) {
		return (notebookservice.login(user));
	}
	
	@PostMapping(value = "/addBook")
	public Book InsertBook(@RequestBody Book book) {
		return (notebookservice.addBook(book));
	}
	

//	@PostMapping(value = "/deleteBook")
//	public Object DeleteBook(@RequestBody Book book) {
//		return (notebookservice.deleteBook(book));
//	}
	
	@PostMapping(value = "/addNoteToBook")
	public Note InsertNote(@RequestBody Note note) {
		return (notebookservice.addNoteToBook(note));
	}
}
