import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class FoodTruckFinder {

	public static List<FoodTruck> foodTruckData  = new ArrayList<FoodTruck>();

	public static void main(String[] args) {

		//get data from sfgov.org food truck data API
		JsonArray foodTruckAPIData = new JsonArray();
		try {
			foodTruckAPIData = getAPIData();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		//store the food trucks that are currently open inside a list
		populateFoodTruckArray(foodTruckAPIData);

		//sort the list alphabetically
		sortFoodTruckList();

		//iterate through list and print as output
		printList();
	}

	public static JsonArray getAPIData() throws IOException {

		String dayOfWeek = getDayOfWeek();
		//format JSON response to get desired dayOfWeek
		String sURL = "http://data.sfgov.org/resource/bbb8-hzi6.json" + "?dayofweekstr=" + dayOfWeek;

		URL url = new URL(sURL);
		HttpURLConnection request = (HttpURLConnection) url.openConnection();
		request.connect();
		if(request.getResponseCode() != 200)
			System.out.println("API Access Failed - data invalid");

		//Convert data to JSON object
		JsonParser jp = new JsonParser(); //from gson

		JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream
		return root.getAsJsonArray();

	}

	public static void populateFoodTruckArray(JsonArray rootobj){

		//iterate and store in array of class FoodTruck
		for(int i=0; i<rootobj.size(); i++){

			JsonObject foodTruckJsonObject = rootobj.get(i).getAsJsonObject();
			//create new FoodTruck object
			FoodTruck foodTruck = new FoodTruck();

			//API Details
			//	name of restaurant is "applicant"
			// 	address of restaurant is "location"
			//	time open: "start24" < currenTime < "end24"

			foodTruck.name = foodTruckJsonObject.get("applicant").getAsString();
			foodTruck.location = foodTruckJsonObject.get("location").getAsString();
			String startTime = foodTruckJsonObject.get("start24").getAsString();
			String endTime = foodTruckJsonObject.get("end24").getAsString();

			boolean openNow = isFoodTruckOpen(startTime, endTime);
			foodTruck.openNow = openNow;

			//only add to our list if it is open right now
			if(openNow){
				foodTruckData.add(foodTruck);
			}
		}
	}

	private static void sortFoodTruckList() {
		Collections.sort(foodTruckData, new Comparator<FoodTruck>() {
	        public int compare(FoodTruck f1, FoodTruck f2) {
	            return f1.getName().compareTo(f2.getName());
	        }
	    });
	}

	private static void printList() {
		//format for cleaner output
		String format = "%-80s%s%n";

		int dataSetSize = foodTruckData.size()+1;
		int currentPage = 0;
		int totalPages = dataSetSize/10;
		if(totalPages >= 1 && dataSetSize%10 != 0){
			totalPages++;
		}
		int counter = 0; //use to show 10 foodtrucks at a time

		for(int i=0; i<foodTruckData.size(); i++){
			if(counter == 0){
				currentPage++;
				System.out.println("\nFood Trucks Currently Open in San Francisco - Page " + currentPage + " of " + totalPages);
				System.out.println();
				System.out.printf(format, "NAME", "ADDRESS");

			}
			FoodTruck foodTruck = foodTruckData.get(i);
			System.out.printf(format, foodTruck.name, foodTruck.location);

			counter++;
			//if we hit 10 visible food truck entries, wait for user input before showing more
			if(counter == 10){
				waitForUserInput();
				//clear screen
				System.out.print("\033[H\033[2J");
				counter = 0;
			}
		}
	}

	//helper functions
	private static void waitForUserInput(){
		System.out.println("\nPress any key for more open food trucks.");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getDayOfWeek(){
		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
		String day = dateFormat.format(date);

		return day;
	}

	private static boolean isFoodTruckOpen(String startTime, String endTime) {
		DateFormat df = new SimpleDateFormat("HH:mm");
		String now = df.format(new Date());

		try{
			Date open = df.parse(startTime);
			Date close = df.parse(endTime);
			Date currentTime = df.parse(now);

			if(currentTime.after(open) && currentTime.before(close))
				return true;
			else
				return false;

		} catch (Exception e) {
			e.printStackTrace();;
		}

		return false;
	}

}

// to compile:
// $ javac -cp ./lib/gson-2.8.1.jar FoodTruckFinder.java FoodTruck.java
// to run:
// $java -cp .:./lib/gson-2.8.1.jar FoodTruckFinder
