package com.tarento.notebook.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tarento.notebook.models.User;
import com.tarento.notebook.service.NotebookService;
import com.tarento.notebook.service.impl.NotebookServiceImpl;

@RestController

public class NotebookController {
	
	@Autowired
	NotebookService notebookservice;

	@RequestMapping(value = "/addUser", method = RequestMethod.POST)
	public User InsertUser(@RequestBody User user) {
		//return (notebookserviceimpl.register(user.getUserName(),user.getEmail(),user.getPassword()));
		return (notebookservice.register(user));
	}
}
