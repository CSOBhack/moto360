package cz.gug.csobhack;

import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by me3x on 21/03/15.
 */
public class HackClient {
    static String url = "http://csob-hackathon.herokuapp.com:80/api/v1/news.json";

    public static News[] getNews2() {
        // Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Make the HTTP GET request, marshaling the response from JSON to an array of Events
        News[] news = restTemplate.getForObject(url, News[].class);

        return news;
    }

    public static List<News> getNews() {
        // Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Make the HTTP GET request, marshaling the response from JSON to an array of Events
        Embedded embededs = restTemplate.getForObject(url, Embedded.class);

        List<News> out = embededs.get_embedded().getNews();

        return out;
    }

}
