# Twitter-Tools

A program to boost your Twitter account/follower engagement. I've used these tools sporadically to grow my own Twitter account, [@warrencrasta](https://twitter.com/warrencrasta), with tremendous success. If you liked this project, or found these tools interesting, please consider starring the repository.

**Screenshots:** http://i.imgur.com/GpTlULE.png

**Features:**
* Unfollow users who don't follow you back (with an option to save a group of people from being unfollowed).
* Follow users based on a keyword they tweeted (can follow a maximum of 1000 people every 24 hours).
* Follow users based on a keyword in their bio (can follow a maximum of 1000 people every 24 hours).
* Favorite tweets based on a keyword (can favorite a maximum of 1000 tweets every 24 hours).
* Unfavorite all of your tweets.

**NOTE:** Use this program within the bounds of the [Twitter Terms of Service](https://twitter.com/tos?lang=en).

## Installation

1. Clone this repository. `git clone https://github.com/wcrasta/Twitter-Tools.git`
2. Add `twitter4j-core-4.0.4.jar` to your project library. If you don't know how to do this, check out [this link](http://stackoverflow.com/questions/3280353/how-to-import-a-jar-in-eclipse).
3. Go to https://apps.twitter.com/ and create a Twitter Application. Go to the Keys and Access Tokens tab of your application and note the **Consumer Key (API Key)**, **Consumer Secret (API Secret)**, **Access Token**, and **Access Token Secret**. Fill in these values in the `twitter4j.properties` file.
4. Navigate to `src/TwitterTools.java` and run the program with Java.

## Possible Uses For This Program

I have used this program to follow 1000 people a day based on a keyword they tweeted/a keyword in their bio. I also favorite 1000 tweets a day. I tweet about basketball/school a lot, so I use keywords related to basketball/school in order to increase the chance of them following me back. **Typically, I gain about 100 *real* new followers a day, and lots of *real* RTs/Favorites on my tweets.** A few days later, I use the tool to unfollow users who don't follow me back and to unfavorite all of my tweets.  

## Improvements/New Features & Instructions For Contributing

Feel free to contribute to this project! There are many improvements that can be made, both in terms of code quality and in terms of whole new ideas that can be implemented. If you encounter a bug, please let me know. Thoughts I have for new features (may or may not ever be implemented):

1. **Turn the code into a web application.** *It would be great if someone could do this. I'd definitely add you to the authors list.*
2. Add a feature that would determine which verified Twitter users are most likely to follow you back, based on their following/follower ratio. 

**To Contribute:**

1. Fork the repository!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request

If you do contribute, be advised that it may take some time to get your PR merged in. If you're interested in being a collaborator, e-mail me. If you don't know how to implement something, but do have an idea that you would like to see implemented, feel free to shoot me an e-mail and I can try to implement it.

## Credits

Author: Warren Crasta (warrencrasta@gmail.com)

Created with the help of [Twitter4J](http://twitter4j.org/en/index.html).
