package com.tarento.notebook.configurations.filters;

import com.google.gson.Gson;
import com.tarento.notebook.models.User;
import com.tarento.notebook.service.MyUserDetailsService;
import com.tarento.notebook.service.NotebookService;
import com.tarento.notebook.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    MyUserDetailsService userDetailsService;
    
    @Autowired
    NotebookService notebookService; 

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;
        Boolean existsOrNot  = Boolean.FALSE;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (SignatureException exception) {
                System.out.println(exception);
                username = null;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            User loggedInUser = new User(userDetails); 
            Claims tokenClaims = jwtUtil.extractAllClaims(jwt);
            int id = (int) tokenClaims.get("USER_ID"); 
            Long userId = (long) id; 
            loggedInUser.setId(userId);
            existsOrNot = notebookService.checkUserIdAuthToken(userId, jwt);
           
            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
            Gson gson = new Gson(); 
            request.setAttribute("UserInfo", gson.toJson(loggedInUser));
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            ResponseContainer responseContainer = new ResponseContainer(ResponseMessage.NOT_LOGGED_IN.getMessage(), String.valueOf(HttpServletResponse.SC_FORBIDDEN), ResponseMessage.NOT_LOGGED_IN.getMessage());
//            response.getWriter().write(ResponseGenerator.failureResponse(responseContainer));
//            response.setContentType("application/json");
//            response.getWriter().flush();
        }
//		if(existsOrNot || request.getRequestURI().equals("/login") || request.getRequestURI().equals("/register") ) {
            chain.doFilter(request, response);
//		}
    }
}
