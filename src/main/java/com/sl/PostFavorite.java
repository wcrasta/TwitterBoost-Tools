/*
 * IDEAS:
 * Include # of tweets favorited today in Rate Limiting Info.
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

@WebServlet("/postFavorite")
public class PostFavorite extends HttpServlet {
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

		String keyword = request.getParameter("keyword");
		String ans = request.getParameter("RTs");
		int favorite = Integer.parseInt(request.getParameter("favorite"));
		int favorite2 = Integer.parseInt(request.getParameter("favorite2"));
		Twitter twitter = (Twitter)request.getSession().getAttribute("twitter");
		if (favorite == favorite2) {
			out.println("To stop the process at any time, click the <strong><font color='red'>Stop loading this page</font></strong> button on your browser.<br/><br/>");
			response.flushBuffer();
			Query query;
			if(ans.equalsIgnoreCase("yes") || ans.equalsIgnoreCase("y")) {
				query = new Query(keyword);
			} else{
				query = new Query(keyword + " -filter:retweets");
			}
			query.setResultType(Query.RECENT);

			/* Add tweets to a list. */
			long lastID = Long.MAX_VALUE;
			ArrayList<Status> tweets = new ArrayList<Status>();
			while (tweets.size() < favorite) {
				if (favorite - tweets.size() > 100)
					query.setCount(100);
				else
					query.setCount(favorite - tweets.size());
				try {
					QueryResult result = twitter.search(query);
					tweets.addAll(result.getTweets());
					for (Status t: tweets)
						if(t.getId() < lastID) lastID = t.getId();
				}
				catch (TwitterException te) {
					out.println("Couldn't retrieve tweets: " + te + "<br/>");
					response.flushBuffer();
					break;
				};
				query.setMaxId(lastID-1);
			}
			/* Favorite all users in the tweets list. */
			for (int i = 0; i < tweets.size(); i++){
				Status s = tweets.get(i);
				try {
					twitter.createFavorite(s.getId());
					out.println(i+1 + ". Favoriting <a href='https://twitter.com/" + s.getUser().getScreenName() + "/status/" + s.getId() +"' target='_blank'>tweet</a> by <a href='https://twitter.com/" + s.getUser().getScreenName() + "' target='_blank'>@" + s.getUser().getScreenName() + "</a>.<br />");
					response.flushBuffer();
				} catch (TwitterException te) {
					/* Ignore any errors and keep running. */
					if (te.toString().contains("Your account may not be allowed to perform this action.")) {
						out.println("Couldn't favorite tweets: " + te + "<br/>");
						response.flushBuffer();
						break;
					}
				}
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
			out.println("The two favorite numbers you entered did not match. Click the Back Button on your browser to try again.<br/>");
			response.flushBuffer();
		}
		out.println("</body>");
		out.println("</html>");
	}
}