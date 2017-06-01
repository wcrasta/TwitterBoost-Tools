/*
 * IDEAS:
 * Make Twitter username/tweet clickable.
 * Include # of tweets unfavorited today in Rate Limiting Info
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
		String unfavorite2 = request.getParameter("unfavorite2");

		Twitter twitter = (Twitter)request.getSession().getAttribute("twitter");
		if (unfavorite.equalsIgnoreCase("yes") && unfavorite2.equalsIgnoreCase("I UNDERSTAND")) {
			int page = 1;
			ResponseList<Status> tweets;
			int count = 0;
			try {
				do {
					tweets = twitter.getFavorites();
					for (Status s : tweets) {
						twitter.destroyFavorite(s.getId());
						out.println(count+1 + ". Unfavorited tweet by @" +
								s.getUser().getScreenName() + ".<br />");
						count++;
					}
					page++;
				} while (tweets.size() != 0 && page < 50);
			} catch (TwitterException te) {
				if (te.toString().contains("Your account may not be allowed to perform this action.")) {
					out.println("Couldn't unfavorite tweets: " + te);
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
				out.println("Couldn't retrieve rate-limits: " + te);
				response.flushBuffer();
			}
		} else {
			out.println("One or both of the values you entered in the previous page are incorrect. Click the Back Button on your browser to try again.");
			response.flushBuffer();
		}
		out.println("</body>");
		out.println("</html>");
	}
}