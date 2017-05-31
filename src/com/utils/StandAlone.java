package com.utils;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class StandAlone {
   public static void main(String[] args) throws TwitterException {
	   // configure twitter object with consumer key and consumer secret 
	   ConfigurationBuilder cb = new ConfigurationBuilder();
   	   cb.setDebugEnabled(true)
   	     .setOAuthConsumerKey(Setup.CONSUMER_KEY)
   	     .setOAuthConsumerSecret(Setup.CONSUMER_SECRET); 
   	   TwitterFactory tf = new TwitterFactory(cb.build());
	   Twitter twitter = tf.getInstance();
	   
	   // set access token and access token secret and user id
	   AccessToken accessToken1 = new AccessToken("access token here", "access token secret here", 1593166574);
   	   twitter.setOAuthAccessToken(accessToken1);
   	   
   	   // update status from
   	   System.out.println(twitter.verifyCredentials());
   	   twitter.updateStatus("Sample tweet from standalone java - 2nd Tweet");
       System.out.println("access token " + twitter.getOAuthAccessToken());
   }
}
