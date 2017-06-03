/*
 * IDEAS:
 * Make Twitter username clickable.
 * Include # of users followed today in Rate Limiting Info
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
		String already2 = request.getParameter("already2");
		String already3 = request.getParameter("already3");

		if (unfollow.equalsIgnoreCase("I UNDERSTAND")) {
			List<Long> neverUnfollow = new ArrayList<Long>();
			if (already2 != null && !already2.isEmpty() && already3 != null && !already3.isEmpty()){
				long cursor2 = -1;
				IDs friends;
				int count = 0;
				try {
					do {
						friends = twitter.getFriendsIDs(cursor2);
						for (long id : friends.getIDs()) {
							out.println(count+1 + ". Adding @" + twitter.showUser(id).getScreenName() +
									" to Never Unfollow list.<br/>");
							response.flushBuffer();
							count++;
							neverUnfollow.add(id);
						}
					} while ((cursor2 = friends.getNextCursor()) != 0);
					try {
						UserList newList = twitter.createUserList("Never Unfollow", false, "Created by @warrencrasta Twitter-Tools");
						for (int i = 0; i < neverUnfollow.size(); i += 100) {
							List<Long> sub = neverUnfollow.subList(i, Math.min(neverUnfollow.size(),i+100));
							twitter.createUserListMembers(newList.getId(), neverUnfollow.stream().mapToLong(l -> l).toArray());						
						}						
					} catch (TwitterException te) {
						out.println("Couldn't create Never Unfollow List: " + te + "<br/>");
						response.flushBuffer();
						return;
					}
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
							out.println(count+1 + ". Adding @" + user.getScreenName() +
									" to Never Unfollow list.<br/>");
							response.flushBuffer();
							count++;
							neverUnfollow.add(user.getId());
						}
					} while ((cursor = users.getNextCursor()) != 0);
				} catch (TwitterException te) {
					out.println("Failed to get list members: " + te);
					response.flushBuffer();
					return;
					/* Ignore any errors and keep running. */
				}
			}
			
			if (already2 != null && !already2.isEmpty() && already3 != null && !already3.isEmpty()) {
				try {
					out.println("<br/> <strong>" + neverUnfollow.size() + "</strong> users were added to the"
							+ " <a href='https://twitter.com/" + twitter.getScreenName() + "/lists/never-unfollow' target='_blank'>Never Unfollow list</a>.<br/><br/>");
					response.flushBuffer();
				} catch (TwitterException te) {
					out.println("Failed to get user's username: " + te);
					response.flushBuffer();
					return;
				}
			} else {
				out.println("<br/> <strong>" + neverUnfollow.size() + "</strong> users were added to the"
						+ " Never Unfollow list.<br/><br/>");
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
							out.println(+ count2+1 + ". Unfollowed @" +
									twitter.showUser(following.get(i)).getScreenName() + ".<br/>");
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
			out.println("<br/><strong>" + count2 + "</strong> users were unfollowed.<br/>");
			
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
			out.println("You did not type \"I UNDERSTAND\" correctly. Click the Back Button on your browser to try again.<br/>");
			response.flushBuffer();
		}
		
		out.println("</body>");
		out.println("</html>");
	}
}