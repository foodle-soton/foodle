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

    public void getVenuesURLs() throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("Venues.txt"));
        } catch (FileNotFoundException e) {
            System.err.println("Venues.txt not found, please ensure it exists");
        }
        String myString;
        while(br.ready()){
            myString = br.readLine();
            
        }
    }

}
