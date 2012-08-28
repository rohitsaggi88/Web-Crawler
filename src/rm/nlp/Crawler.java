package rm.nlp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedHashSet;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * 
 * @author rohitm 
 * Crawler which takes seed URL and processes queue of URLs
 */

public class Crawler {

	private final String seed;
	private LinkedHashSet<String> visitedURLs;
	private RobotsParser robotsParser;
	
	/**
	 * 
	 * @param url Seed URL for crawling
	 */
	public Crawler(String url){
		seed = url;
		visitedURLs = new LinkedHashSet<String>();
		robotsParser = RobotsParser.getInstance();
		
		// Add the seed URL to list of URLs already visited
		visitedURLs.add(getSeed());
	}

	public String getSeed() {
		return seed;
	}

	/**
	 * Method starting the crawling operation
	 * 
	 * @throws IOException
	 */
	public void beginCrawl() throws IOException, MalformedURLException {
		if(!isValidURL(getSeed()))
			throw new MalformedURLException();
		else if (hasRobotsFile(getSeed())){
			processRobotsFile(robotsParser, getSeed());
		} else {
			Document doc = Jsoup.connect(getSeed()).get();
			System.out.println(doc.html());
		}	
	}
	
	/**
	 * Check if URL has HTTP Protocol
	 * @param url 
	 * @return 
	 */
	private static boolean isValidURL(String url) {
		// Check protocol for URL is http
		if(url.matches("^http:.*")) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean hasRobotsFile(String url) throws IOException {
		Connection.Response response = Jsoup.connect(url + "/robots.txt").execute();
		if(response.statusCode() == 200) {
			return true;
		} else {
			return false;
		}
	}
	
	// Logic should go into RobotsParser
	private void processRobotsFile(RobotsParser robotsParser, String url) throws IOException{
		Document doc = Jsoup.connect(url + "/robots.txt").get();
		System.out.println(doc.body().text());
		
		robotsParser.setRobotsTxt(doc.body().text());
	}
	
	public static void main(String args[]) {
		try {
			Crawler crawler = new Crawler("http://google.com");
			crawler.beginCrawl();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
