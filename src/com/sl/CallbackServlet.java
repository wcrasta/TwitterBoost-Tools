package com.sl;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sl.dao.TwitterDAO;
import com.sl.db.DBException;
import com.sl.pojo.UserPojo;

import java.io.IOException;

@WebServlet("/callback")
public class CallbackServlet extends HttpServlet {
    private static final long serialVersionUID = 1657390011452788111L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Get twitter object from session
		Twitter twitter = (Twitter) request.getSession().getAttribute("twitter");
		//Get twitter request token object from session
		RequestToken requestToken = (RequestToken) request.getSession().getAttribute("requestToken");
		String verifier = request.getParameter("oauth_verifier");
		try {
			// Get twitter access token object by verifying request token 
		    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
		    request.getSession().removeAttribute("requestToken");
		    
		    // Get user object from database with twitter user id
		    UserPojo user = TwitterDAO.selectTwitterUser(accessToken.getUserId());
		    if(user == null) {
		       // if user is null, create new user with given twitter details 
		       user = new UserPojo();
		       user.setTwitter_user_id(accessToken.getUserId());
		       user.setTwitter_screen_name(accessToken.getScreenName());
		       user.setAccess_token(accessToken.getToken());
		       user.setAccess_token_secret(accessToken.getTokenSecret());
		       TwitterDAO.insertRow(user);
		       user = TwitterDAO.selectTwitterUser(accessToken.getUserId());
		    } else {
		       // if user already there in database, update access token
		       user.setAccess_token(accessToken.getToken());
		       user.setAccess_token_secret(accessToken.getTokenSecret());
		       TwitterDAO.updateAccessToken(user);
		    }
		    request.setAttribute("user", user);
		} catch (TwitterException | DBException e) {
		    throw new ServletException(e);
		} 
		request.getRequestDispatcher("/index2.html").forward(request, response);
    }
}
