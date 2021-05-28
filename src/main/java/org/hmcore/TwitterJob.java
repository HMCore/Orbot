package org.hmcore;

import com.github.redouane59.twitter.TwitterClient;
import com.github.redouane59.twitter.dto.tweet.Tweet;
import com.github.redouane59.twitter.signature.TwitterCredentials;
import de.wulkanat.Admin;
import de.wulkanat.Channels;
import net.dv8tion.jda.api.MessageBuilder;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Objects;

public class TwitterJob implements Job {

    public static TwitterClient twitterClient = new TwitterClient(TwitterCredentials.builder()
            .accessToken(Objects.requireNonNull(Admin.adFile.getTwitterApi()).getAccessToken())
            .accessTokenSecret(Objects.requireNonNull(Admin.adFile.getTwitterApi()).getAccessTokenSecret())
            .apiKey(Objects.requireNonNull(Admin.adFile.getTwitterApi()).getApiKey())
            .apiSecretKey(Objects.requireNonNull(Admin.adFile.getTwitterApi()).getApiKeySecret())
            .build());

    public static String hytaleTwitterID = twitterClient.getUserFromUserName("Hytale").getId();

    public static String lastTweetID = twitterClient.getUserTimeline(hytaleTwitterID, 20).get(0).getId();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        try {

            Tweet tweet = twitterClient.getUserTimeline(hytaleTwitterID, 20).get(0);
            String tweetID = tweet.getId();

            if(!lastTweetID.equalsIgnoreCase(tweetID)) {
                lastTweetID = tweetID;

                Channels.INSTANCE.sentToAll(new MessageBuilder().append(" You got mail! <:blobmail:817758108503769130> https://twitter.com/Hytale/status/").append(tweetID).build());

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
