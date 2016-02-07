import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.util.*;

public class SotonMenuScraper {
    private static boolean debugMode = false;

    private HashMap<String, URL> outlets;
    private ArrayList<String> foods;
    private LocalDate today;

    // TODO: Exceptions are a mess
    // Main method
    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("debug")) {
                debugMode = true;
            }
        }

        try {
            SotonMenuScraper sms = new SotonMenuScraper();
            sms.go();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Scraper constructor - sets date
    public SotonMenuScraper() throws IOException {
        foods = new ArrayList<String>();
        today = LocalDate.now();

        // Here for weekend coding - uses friday's dataset
        if (today.getDayOfWeek().toString().equals("SATURDAY")) {
            today = today.minusDays(1);
        } else if (today.getDayOfWeek().toString().equals("SUNDAY")) {
            today = today.minusDays(2);
        }
    }

    // Generates the food CSV
    public void go(){
        try {
            outlets = getVenuesURLs();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Iterator it = outlets.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, URL> entry = (Map.Entry<String, URL>)it.next(); // map and hashmap not needed
            foods.addAll(venueScraper(entry.getValue()));
        }
        createCSV("data.csv");
    }

    // Creates URLs and stores them in outlets hashmap
    private HashMap<String, URL> getVenuesURLs() throws IOException {
        HashMap<String, URL> myOutlets = new HashMap<String, URL>();
        URL terraceURL = new URL("http://data.southampton.ac.uk/dumps/catering-daily-menu/" + today.toString() + "/todays-menu-38-terrace.csv");
        URL piazzaURL = new URL("http://data.southampton.ac.uk/dumps/catering-daily-menu/" + today.toString() + "/todays-menu-42-piazza.csv");
        URL cafeURL = new URL("http://data.southampton.ac.uk/dumps/catering-daily-menu/" + today.toString() + "/todays-menu-63A-cafe.csv");
        URL avenueURL = new URL("http://data.southampton.ac.uk/dumps/catering-daily-menu/" + today.toString() + "/todays-menu-65-avenue.csv");
        myOutlets.put("Terrace", terraceURL);
        myOutlets.put("Piazza", piazzaURL);
        myOutlets.put("Cafe", cafeURL);
        myOutlets.put("Avenue", avenueURL);
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
                    //System.err.println(outputLine); // DEBUG
                    foods.add(outputLine);
                }

            }

        } catch (FileNotFoundException e) {
            System.err.println("The URL for the menu broke, error 404?");
            e.printStackTrace();
        } catch (MalformedURLException e) {
            System.err.println("The URL for the menu broke, error 404?");
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
                String out = it.next();
                // Clean up the data before it goes into the CSV file
                out = out.replace("£", "");
                out = out.replace("\"", "");
                out = out.replaceAll("\\((.*?)\\)", ""); // Remove anything in brackets (we don't want anything currently)
                out = out.replaceAll(" - Hot", ""); // Remove hot and cold options (group as one)
                out = out.replaceAll(" - Cold", "");
                out = out.replaceAll("- with meal", "(with meal)"); // Fill in wanted brackets
                csv.println(out);
                if (debugMode) System.out.println(out);
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
