package com.tarento.notebook.service;

import com.tarento.notebook.dao.NotebookDao;
import com.tarento.notebook.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private NotebookDao notebookDao;

    @Autowired
    public MyUserDetailsService(NotebookDao notebookDao) {
        this.notebookDao = notebookDao;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = this.notebookDao.findByEmail(email);
        if (user == null){
            throw new UsernameNotFoundException(email + " was not found");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>()
        );
    }

}
