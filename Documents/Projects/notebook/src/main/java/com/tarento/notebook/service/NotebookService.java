package com.tarento.notebook.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.tarento.notebook.dao.NotebookDao;
import com.tarento.notebook.models.User;

public interface NotebookService {
	

	public User register(User user);
	
		
}
