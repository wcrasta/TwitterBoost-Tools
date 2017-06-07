# TwitterBoost-Tools

A web application to boost your Twitter account stats/follower engagement. I've used these tools sporadically to grow my own Twitter account, [@warrencrasta](https://twitter.com/warrencrasta), with tremendous success. If you liked this project, or found these tools interesting, please consider starring the repository.

**Link to app:** http://twitterboost-tools.herokuapp.com/

**NOTE:** Use this program within the bounds of the [Twitter Terms of Service](https://twitter.com/tos?lang=en). Please do not abuse this application.

## Possible Uses For This Application

I have used this program sporadically to follow 1000 people a day based on a keyword they tweeted/a keyword in their bio. I also favorite 1000 tweets a day. I tweet about basketball/school a lot, so I use keywords related to basketball/school in order to increase the chance of them following me back. **Typically, I gain about 100 *real* new followers a day, and lots of *real* RTs/Favorites on my tweets.** A few days later, I use the tool to unfollow users who don't follow me back and to unfavorite all of my tweets.  

## Instructions For Contributing/Installation

Feel free to contribute to this project! There are many improvements that can be made, both in terms of code quality and in terms of whole new ideas that can be implemented. If you encounter a bug, please let me know.

**To Develop:**

1. Clone the repository.
2. Open Eclipse IDE (Java EE perspective) and import this repo as a Maven project.
3. Create a Tomcat server in Eclipse and add the project to the Tomcat server.
4. Go to [https://apps.twitter.com/](https://apps.twitter.com/) and create a Twitter Application. Go to the Keys and Access Tokens tab of your application and note the **Consumer Key (API Key)** and **Consumer Secret (API Secret)**.
5. Edit **src/main/java/com/utils/Setup.java** with the appropriate values. For DB_URL, create a PostgreSQL database and a table called twitter_user. See **postgresql.txt** in this repo for the create table query. The DB_URL should look like: `jdbc:postgresql://<host>:<port>/<database>?user=<username>&password=<password>&sslmode=require` but some trial-and-error might be needed. For more information, Google "PostgreSQL JDBC connection".
6. Follow existing coding conventions.

The steps above are just high-level descriptions of how to set up the development environment. Setting up Tomcat in Eclipse with Maven can be tricky.

If you do contribute, be advised that it may take some time to get your PR merged in. If you're interested in being a collaborator, e-mail me. If you don't know how to implement something, but do have an idea that you would like to see implemented, feel free to shoot me an e-mail and I can try to implement it.

## Credits

Author: Warren Crasta (warrencrasta@gmail.com)

Created with the help of [Twitter4J](http://twitter4j.org/en/index.html). Thank you to [Dasari Srinivas](http://blog.sodhanalibrary.com/2016/05/login-with-twitter-using-java-tutorial.html) for providing much of the boilerplate code. 
