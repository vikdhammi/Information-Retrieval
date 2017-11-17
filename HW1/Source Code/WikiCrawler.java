/**
 * 
 */

/**
 * @author vikra
 *
 */

import java.io.*;
import java.net.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

// Using Jsoup library for manipulating data in HTML docs
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikiCrawler {

	/**
	 * @param args
	 */
	// Wikipedia Prefix
	public static String wikiPrefix = "https://en.wikipedia.org/wiki";
	static String seedUrl = "https://en.wikipedia.org/wiki/Tropical_cyclone";
//  Using HashSet to store crawled URLs. HashSets ensures no duplicate keys are added
	static HashSet<String> seenUrls = new HashSet<String>();
//  Queue stores all urls extracted from a document. Using queue makes sure the element added first is 
//  more important as the crawler starts from the first url added to queue
	static Queue<String> urlList;
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		// Keeps track of current level of depth
		int currentLevel = 1;
		//Keeps a count of Urls of the child node
		int nextLevelChildLinksCount = 0;
		// Gets the raw html of the seed url
		
		long startTimer = System.currentTimeMillis();
		Document homePage = Jsoup.connect(seedUrl).get();
		 HashSet<String> foundUrls = new HashSet<String>();
		// Extracting out all valid urls found from Home Page
		foundUrls = extractUrlFromDoc(homePage);
		urlList = new LinkedList<String>(foundUrls);
		seenUrls.add(seedUrl);
		
		foundUrls.add(seedUrl);
		
		int currentLevelLinksCount = urlList.size();
		
		/* Conditions to Crawl more URLS
			- Queue has more links in it and is not exhausted 
			- Total valid urls crawled has count less than 1000
			- The depth level remains less than 6
		*/
		while(currentLevel < 7 && !urlList.isEmpty() && seenUrls.size() < 1000)
		{
				String topLink = urlList.remove();

				Document topPage = Jsoup.connect(topLink).get();
				
				seenUrls.add(topLink);
				foundUrls.add(topLink);
				
				urlList.addAll(extractUrlFromDoc(topPage));
				
				nextLevelChildLinksCount = extractUrlFromDoc(topPage).size();
				// Reduce the counter of links for current level after current page urls have extracted
				// in the queue
				currentLevelLinksCount = currentLevelLinksCount - 1;
				
//				System.out.println(currentLevel);
				
				// When currentlevel counter =0, the depth increases by 1 level
				if(currentLevelLinksCount == 0)
				{
					// Increment the depth level by 1
					currentLevel = currentLevel + 1;
					currentLevelLinksCount = nextLevelChildLinksCount;
					// Reset next level links' to 0
					nextLevelChildLinksCount = 0; 				
				}
				// Adding Politeness delay of 1 second between http requests
				//Thread.sleep(1000);
			}		
				 
				
			
			// Writes the output urls to a text file
			writeToFile(foundUrls);
			// Computing the running time for program
			long endTimer = System.currentTimeMillis();
			long timeElapsed = endTimer - startTimer;
			System.out.println(String.format(" Total Running time: " + "%d min, %d secs ",
					TimeUnit.MILLISECONDS.toMinutes(timeElapsed),
					TimeUnit.MICROSECONDS.toSeconds(timeElapsed) -  
					TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeElapsed))));
	}
	
	// Method to write all the urls to the .txt file
	public static void writeToFile(HashSet<String> urls)
	{
		try {
			PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
			Iterator hashUrls = urls.iterator();
			while(hashUrls.hasNext())
			{
				out.println(hashUrls.next());
			}
			out.flush();
			out.close();
		}
		catch(Exception exp)
		{
			exp.printStackTrace();
		}
	}
	
	// Method to extract all valid urls from the given Document
	public static HashSet<String> extractUrlFromDoc(Document doc) throws IOException
	{		
		HashSet<String> docUrls = new HashSet<String>();
		String linkHref = "";
		Elements links = doc.select("a");
		
		for(Element link : links) {
			linkHref = link.absUrl("href");
			if(isValidUrl(linkHref))
			{
				docUrls.add(linkHref);
			//	System.out.println(linkHref);
			}
		}	
		return docUrls;
	}
	
	// Method that checks whether given url follows the proper format
	public static boolean isValidUrl(String url) throws MalformedURLException
	{
		/* Conditions to be checked for a Valid URL 
			1.Should not contain #
			2.Has the prefix -  https://en.wikipedia.org/wiki
			3.Does not navigate to Main Wiki page
			4.Path should not be administrative url (containing :)
			5.Should be only Wiki pages
			6."en" in wikiPrefix ensures it is an English article
		*/
		String path = url.replace(wikiPrefix, "");
		if ((!url.startsWith(wikiPrefix)) || url.contains("#") 
				|| url.contains("/wiki/Main_Page") || path.contains(":"))
			return false;
		else 
			return true;
	}
}
