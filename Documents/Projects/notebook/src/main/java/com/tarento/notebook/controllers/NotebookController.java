package com.tarento.notebook.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tarento.notebook.models.Book;
import com.tarento.notebook.models.Note;
import com.tarento.notebook.models.ResponseContainer;
import com.tarento.notebook.models.User;
import com.tarento.notebook.service.NotebookService;
import com.tarento.notebook.util.ResponseGenerator;
import com.tarento.notebook.constants.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;

@RestController

public class NotebookController {
	
	@Autowired
	NotebookService notebookservice;

	@GetMapping(value = "/hello")
	public String test() {
		return "Hello world";
	}

	@PostMapping(value = "/register", produces = "application/json")
	public String InsertUser(@RequestBody User user, HttpServletResponse response) throws JsonProcessingException {
		User u = notebookservice.register(user);
		ResponseContainer responseContainer = null;
		if (u.getId() == null) {
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			responseContainer = new ResponseContainer(ResponseMessage.USER_ALREADY_EXISTS.getMessage(), String.valueOf(HttpServletResponse.SC_CONFLICT), ResponseMessage.ERROR.getMessage());
			return ResponseGenerator.failureResponse(responseContainer);
		}
		response.setStatus(HttpServletResponse.SC_CREATED);
		responseContainer = new ResponseContainer(ResponseMessage.SUCCESSFUL.getMessage(), String.valueOf(HttpServletResponse.SC_CREATED), ResponseMessage.SUCCESSFUL.getMessage());
		return ResponseGenerator.successResponse(responseContainer);
	}
	
	@PostMapping(value = "/login", produces = "application/json")
	public String Login(@RequestBody User user, HttpServletResponse response) throws JsonProcessingException {
		User u = notebookservice.login(user);
		ResponseContainer responseContainer = null;
		if (u == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			responseContainer = new ResponseContainer(ResponseMessage.EMAIL_INVALID.getMessage(), String.valueOf(HttpServletResponse.SC_UNAUTHORIZED), ResponseMessage.ERROR.getMessage());
			return ResponseGenerator.failureResponse(responseContainer);
		}
		if (u.getPassword() == null) {
			response.setStatus(HttpServletResponse.SC_OK);
			responseContainer = new ResponseContainer(ResponseMessage.SUCCESSFUL.getMessage(), String.valueOf(HttpServletResponse.SC_OK), ResponseMessage.SUCCESSFUL.getMessage());
			return ResponseGenerator.successResponse(responseContainer, u);
		}
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		responseContainer = new ResponseContainer(ResponseMessage.INVALID_CREDENTIALS.getMessage(), String.valueOf(HttpServletResponse.SC_UNAUTHORIZED), ResponseMessage.ERROR.getMessage());
		return ResponseGenerator.failureResponse(responseContainer);
	}
	
	@PostMapping(value = "/book")
	public Book InsertBook(@RequestBody Book book) {
		return (notebookservice.addBook(book));
	}

//	@PostMapping(value = "/book/{id}")
//	public Object DeleteBook(@RequestBody Book book) {
//		return (notebookservice.deleteBook(book));
//	}
	
	@PostMapping(value = "/addNoteToBook")
	public Note InsertNote(@RequestBody Note note) {
		return (notebookservice.addNoteToBook(note));
	}
}
