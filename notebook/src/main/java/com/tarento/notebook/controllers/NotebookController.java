package com.tarento.notebook.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.tarento.notebook.constants.ResponseMessage;
import com.tarento.notebook.exception.DuplicateTagForUserException;
import com.tarento.notebook.exception.DuplicateTagInNotesException;
import com.tarento.notebook.models.*;
import com.tarento.notebook.service.NotebookService;
import com.tarento.notebook.util.ResponseGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController

public class NotebookController {
	
	@Autowired
	NotebookService notebookservice;
	
	private Gson gson = new Gson();

	@GetMapping(value = "/hello")
	public String test() {
		return "Hello world";
	}

	@GetMapping(value = "/isloggedin", produces = "application/json")
	public String IsLoggedIn(HttpServletResponse response) throws JsonProcessingException {
		response.setStatus(HttpServletResponse.SC_OK);
		ResponseContainer responseContainer = new ResponseContainer(ResponseMessage.SUCCESSFUL.getMessage(), String.valueOf(HttpServletResponse.SC_OK), ResponseMessage.SUCCESSFUL.getMessage());
		return  ResponseGenerator.successResponse(responseContainer);
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

	@GetMapping(value="/book", produces="application/json")
	public String GetBook(@RequestAttribute(value = "UserInfo")
									  String UserInfo, HttpServletResponse response) throws JsonProcessingException{
		User currentUser = fetchMyUser(UserInfo);
		List<Book> book = (notebookservice.getBooks(currentUser.getId()));
		ResponseContainer responseContainer = null;
		response.setStatus(HttpServletResponse.SC_OK);
		responseContainer = new ResponseContainer(ResponseMessage.SUCCESSFUL.getMessage(), String.valueOf(HttpServletResponse.SC_OK), ResponseMessage.SUCCESSFUL.getMessage());
		return ResponseGenerator.successResponse(responseContainer, book);
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

    @PostMapping(value = "/book/{book_id}", produces = "application/json")
	public String UpdateBook(@RequestBody Book book, @PathVariable(value = "book_id") long bookID, @RequestAttribute(value ="UserInfo")
	String UserInfo, HttpServletResponse response) throws JsonProcessingException {
		User currentUser = fetchMyUser(UserInfo);
		book.setUpdatedBy(currentUser.getId());
		Boolean b = (notebookservice.updateBook(book, currentUser.getId(), bookID));
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

	@GetMapping(value="/book/{book_id}/note", produces="application/json")
	public String GetNotes(@PathVariable(value = "book_id") long bookID, @RequestAttribute(value = "UserInfo")
		String UserInfo, HttpServletResponse response) throws JsonProcessingException{
		User currentUser = fetchMyUser(UserInfo);
		List<Note> note = (notebookservice.getNotes(currentUser.getId(), bookID));
		ResponseContainer responseContainer = null;
		if (note == null) {
			response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
			responseContainer = new ResponseContainer(ResponseMessage.INVALID_REQUEST.getMessage(), String.valueOf(HttpStatus.UNPROCESSABLE_ENTITY.value()), ResponseMessage.ERROR.getMessage());
			return ResponseGenerator.failureResponse(responseContainer);
		}
		response.setStatus(HttpServletResponse.SC_OK);
		responseContainer = new ResponseContainer(ResponseMessage.SUCCESSFUL.getMessage(), String.valueOf(HttpServletResponse.SC_OK), ResponseMessage.SUCCESSFUL.getMessage());
		return ResponseGenerator.successResponse(responseContainer, note);
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

	@GetMapping(value="/search", produces = "application/json")
	public String Search(@RequestAttribute(value = "UserInfo") String UserInfo,
						 @RequestParam(required = false) Boolean starred,
						 @RequestParam(required = false) String name,
						 @RequestParam(required = false) String tag,
						 HttpServletResponse response) throws JsonProcessingException{
		User currentUser = fetchMyUser(UserInfo);
		ResponseContainer responseContainer = null;
		if(starred==null && (name==null || name.trim().isEmpty()) && (tag==null || tag.trim().isEmpty())) {
			response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
			responseContainer = new ResponseContainer(ResponseMessage.INVALID_REQUEST.getMessage(), String.valueOf(HttpStatus.UNPROCESSABLE_ENTITY.value()), ResponseMessage.ERROR.getMessage());
			return ResponseGenerator.failureResponse(responseContainer);
		}
		if(starred!=null && (name==null || name.trim().isEmpty()) && (tag==null || tag.trim().isEmpty())){
			List<Book> book = (notebookservice.getBooksIfStarred(currentUser.getId()));
			response.setStatus(HttpServletResponse.SC_OK);
			responseContainer = new ResponseContainer(ResponseMessage.SUCCESSFUL.getMessage(), String.valueOf(HttpServletResponse.SC_OK), ResponseMessage.SUCCESSFUL.getMessage());
			return ResponseGenerator.successResponse(responseContainer, book);
		}

		if(starred==null && (name!=null && !name.trim().isEmpty()) && (tag==null || tag.trim().isEmpty())){
			List<Book> book = (notebookservice.getBooksByName(currentUser.getId(), name));
			response.setStatus(HttpServletResponse.SC_OK);
			responseContainer = new ResponseContainer(ResponseMessage.SUCCESSFUL.getMessage(), String.valueOf(HttpServletResponse.SC_OK), ResponseMessage.SUCCESSFUL.getMessage());
			return ResponseGenerator.successResponse(responseContainer, book);
		}

		if(starred==null && (name==null || name.trim().isEmpty()) && (tag!=null && !tag.trim().isEmpty())){
			List<Book> book = (notebookservice.getBooksByTag(currentUser.getId(), tag));
			response.setStatus(HttpServletResponse.SC_OK);
			responseContainer = new ResponseContainer(ResponseMessage.SUCCESSFUL.getMessage(), String.valueOf(HttpServletResponse.SC_OK), ResponseMessage.SUCCESSFUL.getMessage());
			return ResponseGenerator.successResponse(responseContainer, book);
		}
		return null;

	}

	@DeleteMapping(value = "/book/{book_id}/note/{note_id}", produces = "application/json")
	public String DeleteNote(@PathVariable(value = "book_id") Long bookID, @PathVariable(value = "note_id") Long noteID, @RequestAttribute(value = "UserInfo")
			String UserInfo, HttpServletResponse response) throws JsonProcessingException{
		User currentUser = fetchMyUser(UserInfo);
		Boolean b = (notebookservice.deleteNote(currentUser.getId(), bookID, noteID));
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

	@PostMapping(value = "/book/{book_id}/note/{note_id}", produces = "application/json")
	public String AddTag(@RequestBody Tag tag, @PathVariable(value = "book_id") long bookID, @PathVariable(value = "note_id") long noteID, @RequestAttribute(value = "UserInfo")
			String UserInfo, HttpServletResponse response) throws JsonProcessingException {
		User currentUser = fetchMyUser(UserInfo);
		Tag tag1 = null ;
		ResponseContainer responseContainer = null;
		try {
			tag1 = notebookservice.addTag(tag, bookID, noteID, currentUser.getId());
		} catch (DuplicateTagInNotesException e) {
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			responseContainer = new ResponseContainer(ResponseMessage.TAG_EXISTS_IN_NOTES.getMessage(), String.valueOf(HttpServletResponse.SC_CONFLICT), ResponseMessage.ERROR.getMessage());
			return ResponseGenerator.failureResponse(responseContainer);
		} catch (DuplicateTagForUserException e) {
			response.setStatus(HttpServletResponse.SC_CREATED);
			responseContainer = new ResponseContainer(ResponseMessage.SUCCESSFUL.getMessage(), String.valueOf(HttpServletResponse.SC_CREATED), ResponseMessage.SUCCESSFUL.getMessage());
			return ResponseGenerator.successResponse(responseContainer);
		}
		if (tag1 == null) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			responseContainer = new ResponseContainer(ResponseMessage.INTERNAL_SERVER_ERROR.getMessage(), String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), ResponseMessage.ERROR.getMessage());
			return ResponseGenerator.failureResponse(responseContainer);
		}
		response.setStatus(HttpServletResponse.SC_CREATED);
		responseContainer = new ResponseContainer(ResponseMessage.SUCCESSFUL.getMessage(), String.valueOf(HttpServletResponse.SC_CREATED), ResponseMessage.SUCCESSFUL.getMessage());
		return ResponseGenerator.successResponse(responseContainer);
	}

	@GetMapping(value = "/book/{book_id}/note/{note_id}", produces = "application/json")
	public String GetNoteById(@PathVariable(value = "book_id") long bookID, @PathVariable(value = "note_id") long noteID, @RequestAttribute(value = "UserInfo")
			String UserInfo, HttpServletResponse response) throws JsonProcessingException{
		User currentUser = fetchMyUser(UserInfo);
		NoteResponse noteResponse = (notebookservice.getNoteById(currentUser.getId(), bookID, noteID));
		ResponseContainer responseContainer = null;
		if (noteResponse == null) {
			response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
			responseContainer = new ResponseContainer(ResponseMessage.INVALID_REQUEST.getMessage(), String.valueOf(HttpStatus.UNPROCESSABLE_ENTITY.value()), ResponseMessage.ERROR.getMessage());
			return ResponseGenerator.failureResponse(responseContainer);
		}
		response.setStatus(HttpServletResponse.SC_OK);
		responseContainer = new ResponseContainer(ResponseMessage.SUCCESSFUL.getMessage(), String.valueOf(HttpServletResponse.SC_OK), ResponseMessage.SUCCESSFUL.getMessage());
		return ResponseGenerator.successResponse(responseContainer, noteResponse);
	}

	@GetMapping(value = "/tags", produces = "application/json")
	public String GetTagsByUserId(@RequestAttribute(value = "UserInfo")
			String UserInfo, HttpServletResponse response) throws JsonProcessingException{
		User currentUser = fetchMyUser(UserInfo);
		List<Tag> tagList = (notebookservice.getTagsByUserId(currentUser.getId()));
		response.setStatus(HttpServletResponse.SC_OK);
		ResponseContainer responseContainer = new ResponseContainer(ResponseMessage.SUCCESSFUL.getMessage(), String.valueOf(HttpServletResponse.SC_OK), ResponseMessage.SUCCESSFUL.getMessage());
		return ResponseGenerator.successResponse(responseContainer, tagList);
	}



	private User fetchMyUser(String userInfo) { 
		return gson.fromJson(userInfo, User.class);
	}
}
