/*
 * IDEAS:
 * Include # of tweets unfavorited today in Rate Limiting Info.
 * Instead of unfavoriting all tweets, unfavorite only tweets from users you do not follow.
 */

package com.sl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.*;
import java.util.*;

@WebServlet("/postUnfavorite")
public class PostUnfavorite extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		response.setHeader("Connection", "keep-alive");
		response.setHeader("Transfer-Encoding", "chunked");

		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Results</title>");
		out.println("<a href='index2.html'>Tools Page</a>");
		out.println("</head>");
		out.println("<body>");
		out.println("<h2>Results</h2>");

		String unfavorite = request.getParameter("unfavorite");
		String unfavorite1 = request.getParameter("no1");
		String unfavorite2 = request.getParameter("unfavorite2");

		Twitter twitter = (Twitter)request.getSession().getAttribute("twitter");
		if ((unfavorite2.equalsIgnoreCase("I UNDERSTAND")) && (unfavorite.equals("yes") || unfavorite1.equals("yes"))) {
			out.println("To stop the process at any time, click the <strong><font color='red'>Stop loading this page</font></strong> button on your browser.<br/><br/>");
			response.flushBuffer();
			ArrayList<Long> following = new ArrayList<Long>();
			if (unfavorite1 != null) {
				if (unfavorite1.equals("yes")) {
					long cursor2 = -1;
					IDs friends;
					try {
						do {
							friends = twitter.getFriendsIDs(cursor2);
							for (long id : friends.getIDs()) {
								following.add(id);
							}
						} while ((cursor2 = friends.getNextCursor()) != 0);
					} catch (TwitterException te) {
						out.println("Failed to get friends' ids: " + te + "<br/>");
						response.flushBuffer();
						return;
					}
				}
			}
			int page = 1;
			ResponseList<Status> tweets;
			int count = 0;
			try {
				do {
					tweets = twitter.getFavorites();
					for (Status s : tweets) {
						if (unfavorite.equals("yes") || !following.contains(s.getUser().getId())) {
							twitter.destroyFavorite(s.getId());
							out.println(count+1 + ". Unfavoriting <a href='https://twitter.com/" + s.getUser().getScreenName() + "/status/" + s.getId() +"' target='_blank'>tweet</a> by <a href='https://twitter.com/" + s.getUser().getScreenName() + "' target='_blank'>@" + s.getUser().getScreenName() + "</a>.<br />");
							response.flushBuffer();
							count++;
						}
					}
					page++;
				} while (tweets.size() != 0 && page < 50);
			} catch (TwitterException te) {
				if (te.toString().contains("Your account may not be allowed to perform this action.")) {
					out.println("Couldn't unfavorite tweets: " + te + "<br/>");
					response.flushBuffer();
					return;
				}
				/* Ignore any errors and keep running. */
			} 

			out.println("<h2>Twitter Rate-Limiting Info</h2>");
			response.flushBuffer();

			Map<String, RateLimitStatus> rateLimitStatus;
			try {
				rateLimitStatus = twitter.getRateLimitStatus();
				for (String endpoint : rateLimitStatus.keySet()) {
					RateLimitStatus status = rateLimitStatus.get(endpoint);
					if (status.getRemaining() < status.getLimit()) {
						out.println("<strong>Endpoint:</strong> " + endpoint + "<br />");
						out.println("<strong>Limit:</strong> " + status.getLimit() + "<br />");
						out.println("<strong>Remaining:</strong> " + status.getRemaining() + "<br />");
						out.println("<strong>Seconds until remaining resets:</strong> " + status.getSecondsUntilReset() + "<br /><br />");
					}
				}
			} catch (TwitterException te) {
				out.println("Couldn't retrieve rate-limits: " + te + "<br/>");
				response.flushBuffer();
				return;
			}
		} else {
			out.println("One or both of the values you entered in the previous page are incorrect. Click the Back Button on your browser to try again.<br/>");
			response.flushBuffer();
		}
		out.println("</body>");
		out.println("</html>");
	}
}