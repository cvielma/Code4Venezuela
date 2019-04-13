
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONObject;

public class twitterConnect {

    private static final String PROTECTED_RESOURCE_URL = "https://api.twitter.com/1.1/search/tweets.json?q=%23ServicioPublico&geocode=5.1633,-69.4147,700km&count=50&result_type=recent";

    private twitterConnect() {
    }

    private File getFileFromResources(String fileName) {

        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }

    }
    @SuppressWarnings("PMD.SystemPrintln")
    public static void main(String... args) throws IOException, InterruptedException, ExecutionException {
        twitterConnect main = new twitterConnect();
        final OAuth10aService service = new ServiceBuilder("aqjymKONgAoTRBfZ7pFHBEHp1")
                .apiSecret("3WX5q2gNWEx3u7PiUXWR2OUkvETE19YENCsXdJ3Ydbefni0bi9")
                .build(TwitterApi.instance());
        /*final Scanner in = new Scanner(System.in);

        System.out.println("=== Twitter's OAuth Workflow ===");
        System.out.println();

        // Obtain the Request Token
        System.out.println("Fetching the Request Token...");
        final OAuth1RequestToken requestToken = service.getRequestToken();
        System.out.println("Got the Request Token!");
        System.out.println();

        System.out.println("Now go and authorize ScribeJava here:");
        System.out.println(service.getAuthorizationUrl(requestToken));
        System.out.println("And paste the verifier here");
        System.out.print(">>");
        final String oauthVerifier = in.nextLine();
        System.out.println();

        // Trade the Request Token and Verfier for the Access Token
        System.out.println("Trading the Request Token for an Access Token...");
        final OAuth1AccessToken accessToken = service.getAccessToken(requestToken, oauthVerifier);
        System.out.println("Got the Access Token!");
        System.out.println("(The raw response looks like this: " + accessToken.getRawResponse() + "')");
        System.out.println();*/

        final OAuth1AccessToken accessToken = new OAuth1AccessToken("65368099-1uHdzq1UT0EGNLqslks01WxFIYenPfmqgWSAMwAVy", "FzzJut9Xb1tS3SXuiuEuTeimCk6nSq04NrMW0szCrqYnZ", "oauth_token=65368099-1uHdzq1UT0EGNLqslks01WxFIYenPfmqgWSAMwAVy&oauth_token_secret=FzzJut9Xb1tS3SXuiuEuTeimCk6nSq04NrMW0szCrqYnZ&user_id=65368099&screen_name=cv13lm4");
        // Now let's go and ask for a protected resource!
        final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);

        final Response response = service.execute(request);
        String json = response.getBody();
        System.out.println(json);

        JSONObject obj = new JSONObject(json);

        JSONArray arr = obj.getJSONArray("statuses");


        File file = main.getFileFromResources("tweets.json");

        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

        writer.append(arr.toString(2));
        writer.append("\n");

        writer.close();

    }
}
