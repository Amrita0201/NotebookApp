package com.tarento.notebook.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tarento.notebook.dao.NotebookDao;
import com.tarento.notebook.models.User;
import com.tarento.notebook.service.NotebookService;

@Service
public class NotebookServiceImpl implements NotebookService{
	
	@Autowired
	NotebookDao notebookdao;

	@Override
	public User register(User user) {
		
		
		// TODO Auto-generated method stub
		return (notebookdao.register(user));
	}

	@Override
	public User login(User user){
		// TODO Auto-generated method stub
		return notebookdao.login(user);
	}
	

	
	
	

}
