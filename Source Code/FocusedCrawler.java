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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;

// Using Jsoup library for manipulating data in HTML docs
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FocusedCrawler {

	/**
	 * @param args
	 */
	public static String wikiPrefix = "https://en.wikipedia.org/wiki/";
//  Using HashSet to store crawled URLs. HashSets ensures no duplicate keys are added
	static HashSet<String> seenUrls = new HashSet<String>();
//  Queue stores all urls extracted from a document. Using queue makes sure the element added first is 
//  more important as the crawler starts from the first url added to queue
	static Queue<String> urlList;
	static String seedUrl, searchWord = "";
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		System.out.println("Enter the seed url and keyword");
		Scanner params = new Scanner(System.in);
		String[] input = params.nextLine().split(" ");
		String seedUrl = input[0].trim();
		String keyword = input[1].trim();
		FocusedCrawler fw = new FocusedCrawler(seedUrl, keyword);
		
			
		long startTimer = System.currentTimeMillis();
		// Keeps track of current level of depth
		int currentLevel = 1;
		//Keeps a count of Urls of the child node
		int nextLevelChildLinksCount = 0;
		// Gets the raw html of the seed url
		Document homePage = Jsoup.connect(seedUrl).get();
		HashSet<String> foundUrls = new HashSet<String>();
		// Extracting out all valid urls found from Home Page
		
		urlList = new LinkedList<String>(extractUrlFromDoc(homePage));
		seenUrls.add(seedUrl);
		
		foundUrls.add(seedUrl);
		
		int currentLevelLinksCount = urlList.size();
		while(currentLevel < 7 && !urlList.isEmpty() && seenUrls.size() < 1000)
		{
			String topLink = urlList.remove();
			
			if(!foundUrls.add(topLink))
				continue;
			
			Document topPage = Jsoup.connect(topLink).get();
			
			seenUrls.add(topLink);
			if(Math.random() < 0.5)
				System.out.println(foundUrls.size());
			
			HashSet<String> set = extractUrlFromDoc(topPage);
//			for(String s: set)
//			{
//				System.out.println("Hashset elements :" + s);
//			}
			urlList.addAll(set);
			
			
			
			nextLevelChildLinksCount = set.size();
			currentLevelLinksCount = currentLevelLinksCount - 1;
			
			System.out.println(currentLevel);
			if(currentLevelLinksCount == 0)
			{
				currentLevel = currentLevel + 1;
				currentLevelLinksCount = nextLevelChildLinksCount;
				nextLevelChildLinksCount = 0; 				
			}
		}			
			// Adding Politeness delay of 1 second between http requests
			Thread.sleep(1000);
			foundUrls.addAll(urlList);
		
		writeToFile(foundUrls);
		long endTimer = System.currentTimeMillis();
		long timeElapsed = endTimer - startTimer;
		System.out.println(String.format(" Total Running time: " + "%d min, %d secs ",
				TimeUnit.MILLISECONDS.toMinutes(timeElapsed),
				TimeUnit.MICROSECONDS.toSeconds(timeElapsed) -  
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeElapsed))));
	}
	

	public FocusedCrawler(String url, String keyword)
		{
		this.seedUrl = url;
		this.searchWord = keyword;
		}
	
	// Method to write all the urls to the .txt file
	public static void writeToFile(HashSet<String> urls)
	{
		try {
			PrintStream out = new PrintStream(new FileOutputStream("focused_crawler_output.txt"));
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
//				System.out.println(linkHref);
			}
			
		}	
	return docUrls;
	}
	
	// Method that checks whether given url follows the proper format
	public static boolean isValidUrl(String url) throws MalformedURLException, IOException
	{
		String path = url.replace(wikiPrefix, ""); 
		if ((url.startsWith(wikiPrefix)) && !url.contains("#") 
				&& !url.contains("/wiki/Main_Page") && !path.contains(":"))
			 return (findRelevantUrls(url));
		else 
			return false;
	}
	
	public static boolean findRelevantUrls(String url) throws MalformedURLException, IOException
	{
		String path = url.trim().replace(wikiPrefix, "");
		List<String> abc = new ArrayList<String>();
		List<String> xyz = new ArrayList<String>();
		if(path.contains("_"))
			abc = Arrays.asList(path.split("_"));
		else if (path.contains("-"))
			abc = Arrays.asList(path.split("-"));
		else
			xyz.add(path);
		Document doc = Jsoup.connect(url).get();
		String docTitle = doc.title();
		if(docTitle.contains("_"))
			abc = Arrays.asList(docTitle.split("_"));
		else if(docTitle.equals("-"))
			abc = Arrays.asList(docTitle.split("-"));
		else if(docTitle.equals(" "))
			xyz.add(docTitle);
//		String[] possibleMatch = (String[])ArrayUtils.addAll(checkPath, checkTitle);
		List<String> possMatch2 = new ArrayList<String>(abc);
		possMatch2.addAll(xyz);
		for(String word: possMatch2)
		{
			if(word.toLowerCase().startsWith(searchWord.toLowerCase()))
			{
//				System.out.println(url);
				return true;
			}
		}
		return false;
	}
}
