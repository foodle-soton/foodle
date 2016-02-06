import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by William on 05/02/2016.
 */
public class SotonMenuScraper {

    HashMap<String, String> outlets;

    public SotonMenuScraper(){

    }

    // Reads URLs and venues from "Venues.txt" - "Arlott,[urlgoeshere]" gets stored in outlets as Arlott and the url
    public void getVenuesURLs() throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("Venues.txt"));
        } catch (FileNotFoundException e) {
            System.err.println("Venues.txt not found, please ensure it exists");
        }
        String myString;
        String venue;
        String url;
        // Splits URL and stores
        while(br.ready()){
            myString = br.readLine();
            venue = myString.split(",")[0];
            url = myString.split(",")[1];
            outlets.put(venue, url);

        }
    }

}
