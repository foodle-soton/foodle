import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by Will on 05/02/2016.
 */
public class SotonMenuScraper {

    private HashMap<String, URL> outlets;
    private ArrayList<String> foods;
    private LocalDate today;
    // TODO: Exceptions are a mess
    public static void main(String[] args) {
        try {
            SotonMenuScraper sms = new SotonMenuScraper();
            sms.go();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public SotonMenuScraper() throws IOException {
        today = LocalDate.now();

        today = today.minusDays(1); // Here for weekend coding - remove from production



    }

    // Generates the food csv
    public void go(){
        try {
            outlets = getVenuesURLs();
        } catch (IOException e) {
            e.printStackTrace();
        }
        foods = new ArrayList<>();
        Iterator it = outlets.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, URL> entry = (Map.Entry<String, URL>)it.next(); // map and hashmap not needed
            foods.addAll(venueScraper(entry.getValue()));
        }
        createCSV("themightycsvoffood.csv");
    }

    // Creates URLs and stores them in outlets hashmap
    private HashMap<String, URL> getVenuesURLs() throws IOException {
        HashMap<String, URL> myOutlets = new HashMap<String, URL>();
        URL terraceURL = new URL("http://data.southampton.ac.uk/dumps/catering-daily-menu/" + today.toString() + "/todays-menu-38-terrace.csv");
        URL piazzaURL = new URL("http://data.southampton.ac.uk/dumps/catering-daily-menu/" + today.toString() + "/todays-menu-42-piazza.csv");
        myOutlets.put("Terrace", terraceURL);
        myOutlets.put("Piazza", piazzaURL);
        return myOutlets;
    }

    // Reads data from URL passed to it and adds food to foods in format location,food,price
    private ArrayList<String> venueScraper(URL venueURL){
        ArrayList<String> foodList = new ArrayList<>();
        try {

            Scanner vsc = new Scanner(venueURL.openStream());
            String nowLine, lastValue, foodPrice, foodName, foodCategory, foodVenue, outputLine;
            String[] nowLineContents;
            vsc.nextLine(); // Skips over first line with headers
            while(vsc.hasNext()){
                nowLine = vsc.nextLine();
                nowLineContents = nowLine.split(",");
                lastValue = nowLineContents[nowLineContents.length - 1];
                if(!lastValue.contains("£")){
                    nowLine = nowLine + vsc.nextLine();
                }
                foodPrice = getFoodPrice(nowLine);
                foodName = getFoodName(nowLine);
                foodCategory = getFoodCat(nowLine);
                foodVenue = getFoodVenue(nowLine);
                if(!foodPrice.equals("")) {
                    outputLine = foodVenue + "," + foodCategory + "," + foodName + "," + foodPrice;
                    //System.err.println(outputLine);
                    foods.add(outputLine);
                }

            }

        } catch (FileNotFoundException e) {
            System.err.println("The URL for the menu broke");
            e.printStackTrace();
        } catch (MalformedURLException e) {
            System.err.println("The URL for the menu broke");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return foodList;

    }

    private String getFoodName(String line){
        return line.split(",")[2];
    }

    private String getFoodCat(String line){
        return line.split(",")[1];
    }

    private String getFoodPrice(String line){
        String price = line.split(",")[line.split(",").length - 2];
        if(price.length() < 2){
            return "";
        }
        //Pence case
        if(price.substring(price.length() - 1, price.length()).equals("p")){
            if(price.length() == 2){
                price = price.substring(0, 1);
                price = "£0.0" + price;
            } else if(price.length() == 3){
                price = price.substring(0, 2);
                price = "£0." + price;
            } else {
                return "";
            }
        }
        if(!price.substring(0, 1).equals("£")){
            return "";
        }
        return price;
    }

    private String getFoodVenue(String line){
        String firstValue = line.split(",")[0];
        return firstValue.split("-")[1];
    }


    public void createCSV(String fileName){
        Iterator<String> it = foods.iterator();
        try {
            PrintStream csv = new PrintStream(fileName);
            while(it.hasNext()){
                String myStr = it.next();
                csv.println(myStr);
                System.out.println(myStr);


            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Returns the arraylist of foods, locations prices for csv
    public ArrayList<String> getFoodList(){
        return foods;
    }



}
