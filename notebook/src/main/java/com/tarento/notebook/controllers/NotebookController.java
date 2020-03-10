package com.tarento.notebook.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.tarento.notebook.constants.ResponseMessage;
import com.tarento.notebook.models.Book;
import com.tarento.notebook.models.Note;
import com.tarento.notebook.models.ResponseContainer;
import com.tarento.notebook.models.User;
import com.tarento.notebook.service.NotebookService;
import com.tarento.notebook.util.ResponseGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController

public class NotebookController {
	
	@Autowired
	NotebookService notebookservice;
	
	private Gson gson = new Gson();

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
			responseContainer = new ResponseContainer(ResponseMessage.INVALID_CREDENTIALS.getMessage(), String.valueOf(HttpServletResponse.SC_UNAUTHORIZED), ResponseMessage.ERROR.getMessage());
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
	
	@PostMapping(value = "/book", produces = "application/json")
	public String InsertBook(@RequestBody Book book, @RequestAttribute(value = "UserInfo")
			String UserInfo, HttpServletResponse response) throws JsonProcessingException {
		User currentUser = fetchMyUser(UserInfo); 
		book.setCreatedBy(currentUser.getId());
		Book b = (notebookservice.addBook(book));
		ResponseContainer responseContainer = null;
		if (b == null) {
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			responseContainer = new ResponseContainer(ResponseMessage.INVALID_BOOKNAME.getMessage(), String.valueOf(HttpServletResponse.SC_CONFLICT), ResponseMessage.ERROR.getMessage());
			return ResponseGenerator.failureResponse(responseContainer);
		}
		response.setStatus(HttpServletResponse.SC_CREATED);
		responseContainer = new ResponseContainer(ResponseMessage.SUCCESSFUL.getMessage(), String.valueOf(HttpServletResponse.SC_CREATED), ResponseMessage.SUCCESSFUL.getMessage());
		return ResponseGenerator.successResponse(responseContainer, b);
	}
	
	@DeleteMapping(value = "/book/{book_id}", produces = "application/json")
    public String DeleteBook(@PathVariable(value = "book_id") Long bookID, @RequestAttribute(value = "UserInfo")
    String UserInfo, HttpServletResponse response) throws JsonProcessingException {
		User currentUser = fetchMyUser(UserInfo); 
		Boolean b = (notebookservice.deleteBook(currentUser.getId(), bookID));
		ResponseContainer responseContainer = null;
		if (b == false) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			responseContainer = new ResponseContainer(ResponseMessage.BOOK_DOESNOT_BELONG_TO_THE_USER.getMessage(), String.valueOf(HttpServletResponse.SC_UNAUTHORIZED), ResponseMessage.ERROR.getMessage());
			return ResponseGenerator.failureResponse(responseContainer);
		}
		response.setStatus(HttpServletResponse.SC_CREATED);
		responseContainer = new ResponseContainer(ResponseMessage.SUCCESSFUL.getMessage(), String.valueOf(HttpServletResponse.SC_CREATED), ResponseMessage.SUCCESSFUL.getMessage());
		return ResponseGenerator.successResponse(responseContainer);
    }
	
	@PostMapping(value = "/book/{book_id}/note", produces = "application/json")
	public String InsertNote(@RequestBody Note note, @PathVariable(value = "book_id") long bookID, @RequestAttribute(value = "UserInfo") 
		String UserInfo, HttpServletResponse response) throws JsonProcessingException {
		User currentUser = fetchMyUser(UserInfo);
		note.setCreatedBy(currentUser.getId());
		Note n=notebookservice.addNoteToBook(note, currentUser.getId(), bookID);
		ResponseContainer responseContainer = null;
		if (n == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			responseContainer = new ResponseContainer(ResponseMessage.BOOK_DOESNOT_BELONG_TO_THE_USER.getMessage(), String.valueOf(HttpServletResponse.SC_UNAUTHORIZED), ResponseMessage.ERROR.getMessage());
			return ResponseGenerator.failureResponse(responseContainer);
		}
		response.setStatus(HttpServletResponse.SC_CREATED);
		responseContainer = new ResponseContainer(ResponseMessage.SUCCESSFUL.getMessage(), String.valueOf(HttpServletResponse.SC_CREATED), ResponseMessage.SUCCESSFUL.getMessage());
		return ResponseGenerator.successResponse(responseContainer);
	}
	
	private User fetchMyUser(String userInfo) { 
		return gson.fromJson(userInfo, User.class);
	}
}
