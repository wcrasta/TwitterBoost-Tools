/*
 * IDEAS:
 * Include # of users followed today in Rate Limiting Info.
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

@WebServlet("/postBiokeyword")
public class PostBiokeyword extends HttpServlet {
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

		String query = request.getParameter("keyword");
		int page = Integer.parseInt(request.getParameter("page"));
		int follow = Integer.parseInt(request.getParameter("follow"));
		int follow2 = Integer.parseInt(request.getParameter("follow2"));
		Twitter twitter = (Twitter)request.getSession().getAttribute("twitter");
		if (follow == follow2) {
			out.println("To stop the process at any time, click the <strong><font color='red'>Stop loading this page</font></strong> button on your browser.<br/><br/>");
			response.flushBuffer();
			ResponseList<User> users;
			int count = 0;
			try {
				do {
					users = twitter.searchUsers(query, page);
					for (User user : users) {
						if (count < follow){
							try{
								twitter.createFriendship(user.getId());
								out.println(count+1 + ". Following <a href='https://twitter.com/" + user.getScreenName() + "' target='_blank'>@" +
										user.getScreenName() + "</a>.<br/>");
								response.flushBuffer();
								count++;
								/* Optional Delay, in seconds. */
								// TimeUnit.SECONDS.sleep(1);
							} catch (TwitterException te) {
								if (te.toString().contains("You are unable to follow more people at this time.")) {
									out.println("Couldn't follow users: " + te + "<br/>");
									response.flushBuffer();
									break;
								}
								/* Ignore any errors and keep running. */
							}
						} else {
							break;
						}
					}
					page++;
				} while (users.size() != 0 && page < 50 && (count < follow));
			} catch (TwitterException te) {
				out.println("Couldn't retrieve tweets: " + te + "<br/>");
				response.flushBuffer();
				return;
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
			}
		} else {
			out.println("The two follow numbers you entered did not match. Click the Back Button on your browser to try again. <br/>");
			response.flushBuffer();
		}
		out.println("</body>");
		out.println("</html>");
	}
}