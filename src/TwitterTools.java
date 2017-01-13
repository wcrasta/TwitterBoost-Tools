import twitter4j.*;
import java.util.*;

/* Optional Delay. */
// import java.util.concurrent.TimeUnit;

/* 
 * I have purposely ignored exceptions in most places, because I want the program
 * to continue running regardless of errors. If you want to see potential errors,
 * add the following code to try/catch blocks:
 * 
	catch (TwitterException te) {
		te.printStackTrace();
		System.out.println("Error: " + te.getMessage());
		System.exit(-1);
	}
 *
 */

public class TwitterTools{	
	public static void main(String[] args) throws TwitterException, InterruptedException{
		/* Note: Twitter Rate Limits -- Can only call the API 15 times in 15 minutes. */;
		Scanner sc = new Scanner(System.in);
		boolean stop = false;
		while(!stop) {
			System.out.println("Welcome to Twitter Tools! Tools:"
					+ "\n1. Unfollow everyone who doesn't follow me"
					+ " back. (You can specify users to keep safe.)\n2. Follow users based"
					+ " on a keyword they tweeted.\n3. Follow users based on a keyword in"
					+ " their bio.\n4. Favorite tweets based on a keyword.\n5. Unfavorite"
					+ " all of your tweets.\n");
			int number;
			do {
				System.out.print("Enter the number of the tool you would like to use: ");
				while (!sc.hasNextInt()) {
					System.out.print("Enter the number of the tool you would like to use: ");
					sc.next();
				}
				number = sc.nextInt();
				sc.nextLine();
			} while (number < 1 || number > 5);
			System.out.println();
			Twitter twitter = TwitterFactory.getSingleton();
			switch (number){
			case 1:
				unfollowUnfollowers(twitter, sc);
				break;
			case 2:
				followByKeyword(twitter, sc);
				break;
			case 3:
				followByBIO(twitter, sc);
				break;
			case 4:
				favoriteByKeyword(twitter, sc);
				break;
			case 5:
				unfavoriteAllTweets(twitter, sc);
				break;
			}
			System.out.print("Would you like to run another tool? (Yes/No): ");
			String s = sc.nextLine();
			if(s.equalsIgnoreCase("no") || s.equalsIgnoreCase("n")) {
				stop = true;
			}
			System.out.println();
		}
	}


	public static void unfollowUnfollowers(Twitter twitter, Scanner sc)
			throws TwitterException, InterruptedException{
		System.out.println("Unfollow everyone who doesn't follow you. If you want"
				+ " to make sure that certain people\nare safe, add them to a"
				+ " Twitter list and specify the screenname of the list owner"
				+ " and\nthe name of the list below. If you don't have a list of"
				+ " non-followers that you want to\nsave, type \"None\" for list owner.");
		System.out.print("Specify the owner of the list: ");
		String owner = sc.nextLine();
		List<Long> neverUnfollow = new ArrayList<Long>();
		if (!owner.equalsIgnoreCase("none")){
			System.out.print("Specify the name of the list: ");
			String listName = sc.nextLine();
			System.out.println();
			long cursor = -1;
			PagableResponseList<User> users;
			try {
				do {
					users = twitter.getUserListMembers(owner, listName, cursor);
					for (User user : users) {
						System.out.println("Adding @" + user.getScreenName() +
								" to Don't Unfollow list.");
						neverUnfollow.add(user.getId());
					}
				} while ((cursor = users.getNextCursor()) != 0);
			} catch (TwitterException te) {
				te.printStackTrace();
				System.out.println("Failed to get list members: " + te.getMessage());
				System.exit(-1);
			}
		}
		System.out.println(neverUnfollow.size() + " users were added to the"
				+ " Don't Unfollow list.");		
		System.out.println();
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
			te.printStackTrace();
			System.out.println("Failed to get friends' ids: " + te.getMessage());
			System.exit(-1);
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
			te.printStackTrace();
			System.out.println("Failed to get followers' ids: " + te.getMessage());
			System.exit(-1);
		}
		int count = 0;
		/* Iterate through the list of people you are following. */
		for (int i = 0; i < following.size(); i++) {
			/* If a person doesn't follow you back, unfollow them. */
			if (!followers.contains(following.get(i))){
				if(!neverUnfollow.contains(following.get(i))){
					try {
						twitter.destroyFriendship(following.get(i));
						System.out.println("Unfollowed @" +
								twitter.showUser(following.get(i)).getScreenName() + ".");
						count++;
						/* Optional Delay, in seconds. */
						// TimeUnit.SECONDS.sleep(1);
					} catch (TwitterException te) {
						/* Ignore any errors and keep running. */
					}
				}
			}
		}
		System.out.println(count + " users were unfollowed.\n");
	}

	public static void followByKeyword(Twitter twitter, Scanner sc) throws TwitterException{
		System.out.println("Look for users who tweeted a certain keyword"
				+ " and follow them.");
		System.out.print("Enter keyword: ");
		String keyword = sc.nextLine();
		String ans;
		do {
			System.out.print("Include RTs? (Yes/No): ");
			while (!sc.hasNext()) {
				System.out.print("Include Retweets? (Yes/No): ");
				sc.next();
			}
			ans = sc.nextLine();
		} while (!(ans.equalsIgnoreCase("yes") || ans.equalsIgnoreCase("y")
				|| ans.equalsIgnoreCase("no") || ans.equalsIgnoreCase("n")));

		Query query;
		if(ans.equalsIgnoreCase("yes") || ans.equalsIgnoreCase("y")) {
			query = new Query(keyword);
		} else{
			query = new Query(keyword + " -filter:retweets");
		}		
		query.setResultType(Query.RECENT);
		int follow;
		do {
			System.out.print("Enter number of users to follow (max: 1000): ");
			while (!sc.hasNextInt()) {
				System.out.print("Enter number of users to follow (max: 1000): ");
				sc.next();
			}
			follow = sc.nextInt();
			sc.nextLine();
		} while (follow < 0 || follow > 1000);
		System.out.println();
		/* Add tweets to a list. */
		long lastID = Long.MAX_VALUE;
		ArrayList<Status> tweets = new ArrayList<Status>();
		while (tweets.size() < follow) {
			if (follow - tweets.size() > 100)
				query.setCount(100);
			else 
				query.setCount(follow - tweets.size());
			try {
				QueryResult result = twitter.search(query);
				tweets.addAll(result.getTweets());
				for (Status t: tweets) 
					if(t.getId() < lastID) lastID = t.getId();
			}
			catch (TwitterException te) {
				System.out.println("Couldn't retrieve tweets: " + te);
			};
			query.setMaxId(lastID-1);
		}
		/* Follow all users in the tweets list. */
		for (int i = 0; i < tweets.size(); i++){
			Status s = tweets.get(i);
			try {
				twitter.createFriendship(s.getUser().getId());
				System.out.println(i+1 + ". Following @" + s.getUser().getScreenName() + ".");
				/* Optional Delay, in seconds. */
				// TimeUnit.SECONDS.sleep(1);
			} catch (TwitterException te) {
				/* Ignore any errors and keep running. */
			}
		}
		System.out.println();
	}	

	public static void followByBIO(Twitter twitter, Scanner sc) throws TwitterException{
		System.out.println("Follow users based on a keyword in their bio. There are 20"
				+ " users/page, so if you notice\nthat you are following the same"
				+ " people over and over again, use different pages. In most\ncases,"
				+ " you should use page 1 first.");
		System.out.print("Enter keyword: ");
		String query = sc.nextLine();
		int follow;
		do {
			System.out.print("Enter number of users to follow (max: 1000): ");
			while (!sc.hasNextInt()) {
				System.out.print("Enter number of users to follow (max: 1000): ");
				sc.next();
			}
			follow = sc.nextInt();
			sc.nextLine();
		} while (follow < 0 || follow > 1000);
		int page;
		do {
			System.out.print("What page do you want to start on (max: 50)? ");
			while (!sc.hasNextInt()) {
				System.out.print("What page do you want to start on (max: 50)? ");
				sc.next();
			}
			page = sc.nextInt();
			sc.nextLine();
		} while (follow < 0 || follow > 1000);
		System.out.println();
		ResponseList<User> users;
		int count = 0;
		try {
			do {
				users = twitter.searchUsers(query, page);
				for (User user : users) {
					if (count < follow){
						try{
							twitter.createFriendship(user.getId());
							System.out.println(count+1 + ". Following @" +
									user.getScreenName() + ".");
							count++;
							/* Optional Delay, in seconds. */
							// TimeUnit.SECONDS.sleep(1);
						} catch (TwitterException te) {
							/* Ignore any errors and keep running. */
						}	
					} else {
						break;
					}
				}
				page++;
			} while (users.size() != 0 && page < 50 && (count < follow));
		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to search users: " + te.getMessage());
			System.exit(-1);
		}
		System.out.println();
	}

	public static void favoriteByKeyword(Twitter twitter, Scanner sc) throws TwitterException{
		System.out.println("Favorite tweets that contain a keyword.");
		System.out.print("Enter keyword: ");
		String keyword = sc.nextLine();
		String ans;
		do {
			System.out.print("Include RTs? (Yes/No): ");
			while (!sc.hasNext()) {
				System.out.print("Include Retweets? (Yes/No): ");
				sc.next();
			}
			ans = sc.nextLine();
		} while (!(ans.equalsIgnoreCase("yes") || ans.equalsIgnoreCase("y")
				|| ans.equalsIgnoreCase("no") || ans.equalsIgnoreCase("n")));

		Query query;
		if(ans.equalsIgnoreCase("yes") || ans.equalsIgnoreCase("y")) {
			query = new Query(keyword);
		} else{
			query = new Query(keyword + " -filter:retweets");
		}		
		query.setResultType(Query.RECENT);
		int favorite;
		do {
			System.out.print("Enter number of tweets to favorite (max: 1000): ");
			while (!sc.hasNextInt()) {
				System.out.print("Enter number of tweets to favorite (max: 1000): ");
				sc.next();
			}
			favorite = sc.nextInt();
			sc.nextLine();
		} while (favorite < 0 || favorite > 1000);
		System.out.println();
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
				System.out.println("Couldn't retrieve tweets: " + te);
			};
			query.setMaxId(lastID-1);
		}
		/* Favorite all tweets in the tweets list. */
		for (int i = 0; i < tweets.size(); i++){
			Status s = tweets.get(i);
			try {
				twitter.createFavorite(s.getId());
				System.out.println(i+1 + ". Favoriting tweet by @" +
						s.getUser().getScreenName() + ".");
				/* Optional Delay, in seconds. */
				// TimeUnit.SECONDS.sleep(1);
			} catch (TwitterException te) {
				/* Ignore any errors and keep running. */
			}
		}
		System.out.println();
	}

	/*  */
	public static void unfavoriteAllTweets(Twitter twitter, Scanner sc) throws TwitterException{
		System.out.println("Unfavorite all your favorites. If you have a lot of favorites, you"
				+ " might have to run\nthis tool a few times to unfavorite all of them.");
		System.out.print("Are you sure you want to unfavorite all your favorites? (Yes/No): ");
		String ans = sc.nextLine();
		if((ans.equalsIgnoreCase("yes") || ans.equalsIgnoreCase("y"))) {
			System.out.println();
			int page = 1;
			ResponseList<Status> tweets;
			int count = 0;
			try {
				do {
					tweets = twitter.getFavorites();
					for (Status s : tweets) {
						twitter.destroyFavorite(s.getId());
						System.out.println(count+1 + ". Unfavorited tweet by @" +
								s.getUser().getScreenName() + ".");
						count++;
					}
					page++;
				} while (tweets.size() != 0 && page < 50);
			} catch (TwitterException te) {
				/* Ignore any errors and keep running. */
			}
		}
		System.out.println();
	}
}