/*
 * IDEAS:
 * Include # of users unfollowed in last 24 hours in Rate Limiting Info.
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

@WebServlet("/postUnfollow")
public class PostUnfollow extends HttpServlet {
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

		Twitter twitter = (Twitter)request.getSession().getAttribute("twitter");
		String owner = request.getParameter("owner");
		String listName = request.getParameter("listName");
		String unfollow = request.getParameter("unfollow");
		String already = request.getParameter("already");
		String already3 = request.getParameter("already3");

		if (unfollow.equalsIgnoreCase("I UNDERSTAND")) {
			out.println("To stop the process at any time, click the <strong><font color='red'>Stop loading this page</font></strong> button on your browser.<br/><br/>");
			response.flushBuffer();
			List<Long> neverUnfollow = new ArrayList<Long>();
			if (already.equals("no") && already3.equals("yes")){
				long cursor2 = -1;
				IDs friends;
				int count = 0;
				try {
					do {
						friends = twitter.getFriendsIDs(cursor2);
						for (long id : friends.getIDs()) {
							out.println(count+1 + ". Adding <a href='https://twitter.com/" + twitter.showUser(id).getScreenName() + "' target='_blank'>@" + twitter.showUser(id).getScreenName() +
									"</a> to the Never Unfollow list.<br/>");
							response.flushBuffer();
							count++;
							neverUnfollow.add(id);
						}
					} while ((cursor2 = friends.getNextCursor()) != 0);
				} catch (TwitterException te) {
					out.println("Failed to get friends' ids: " + te + "<br/>");
					response.flushBuffer();
					return;
				}
			} else if (owner != null && !owner.isEmpty()) {
				long cursor = -1;
				PagableResponseList<User> users;
				int count = 0;
				try {
					do {
						users = twitter.getUserListMembers(owner, listName, cursor);
						for (User user : users) {
							out.println(count+1 + ". Adding <a href='https://twitter.com/" + user.getScreenName() + "' target='_blank'>@" + user.getScreenName() +
									"</a> to Never Unfollow list.<br/>");
							response.flushBuffer();
							count++;
							neverUnfollow.add(user.getId());
						}
					} while ((cursor = users.getNextCursor()) != 0);
				} catch (TwitterException te) {
					out.println("Failed to get list members: " + te + "<br/>");
					response.flushBuffer();
					return;
					/* Ignore any errors and keep running. */
				}
			}
			out.println("<strong>" + neverUnfollow.size() + "</strong> users were added to the"
					+ " Never Unfollow list.<br/><br/>");
			response.flushBuffer();

			if (already.equals("no") && already3.equals("yes")) {
				out.println("<strong>NOTE:</strong> We did not add the above users to a list on Twitter because the Twitter API says that doing so can be buggy.<br/>");
				response.flushBuffer();
			}
			long cursor2 = -1;
			IDs friends;
			ArrayList<Long> following = new ArrayList<Long>();
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

			long cursor3 = -1;
			IDs followerIDs;
			ArrayList<Long> followers = new ArrayList<Long>();
			try {
				do {
					followerIDs = twitter.getFollowersIDs(cursor3);
					for (long id : followerIDs.getIDs()) {
						followers.add(id);
					}
				} while ((cursor3 = followerIDs.getNextCursor()) != 0);
			} catch (TwitterException te) {
				out.println("Failed to get followers' ids: " + te + "<br/>");
				response.flushBuffer();
				return;
			}
			int count2 = 0;
			/* Iterate through the list of people you are following. */
			for (int i = 0; i < following.size(); i++) {
				/* If a person doesn't follow you back, unfollow them. */
				if (!followers.contains(following.get(i))){
					if(!neverUnfollow.contains(following.get(i))){
						try {
							twitter.destroyFriendship(following.get(i));
							out.println(count2+1 + ". Unfollowed <a href='https://twitter.com/" + twitter.showUser(following.get(i)).getScreenName() +"' target='_blank'>@" + twitter.showUser(following.get(i)).getScreenName() + ".</a><br/>");
							response.flushBuffer();
							count2++;
							/* Optional Delay, in seconds. */
							// TimeUnit.SECONDS.sleep(1);
						} catch (TwitterException te) {
							/* Ignore any errors and keep running. */
						}
					}
				}
			}
			out.println("<strong>" + count2 + "</strong> users were unfollowed.<br/>");

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
				out.println("Couldn't retrieve rate-limits: " + te + "<br />");
				response.flushBuffer();
			}
		} else {
			out.println("You did not type \"I UNDERSTAND\" correctly. Click the Back Button on your browser to try again.<br/>");
			response.flushBuffer();
		}

		out.println("</body>");
		out.println("</html>");
	}
}