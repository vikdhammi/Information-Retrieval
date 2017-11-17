
Assignment 1

Setup:
	- The deliverables has two .java files:
	1) WikiCrawler.java
	   This file takes the default seed url as https://en.wikipedia.org/wiki/Tropical_cyclone (Hardcoded inside program)
	   To run this file from CLI: javac WikiCrawler.java
	                              java WikiCrawler
	   External java library used: Jsoup
	   
	2) FocusedCrawler.java
	   This file takes 2 input: seed url and keyword 
	   To run this file from CLI: javac FocusedCrawler.java https://en.wikipedia.org/wiki/Tropical_cyclone rain
	                              java FocusedCrawler
	   External java library used: Jsoup, common-lang3.jar for ArrayUtils


Maxmium Depth reached:
	1) WikiCrawler.java : Max depth level reached was 4. Since it is unfocused crawling the 1000 unique urls check is enforced first. 
	2) FocusedCrawler.java: Max depth level reached was 6. Since it has a very specific search, the crawler never reaches 1000 unique urls.
	

For FocusedCrawler, the program takes 2 arguments - seed and the keyword. All the conditions for a valid url still remain the same as in the previous crawler.
However additional conditions were added to narrow down the search results.The method findRelevantUrls(String url) performs this check.
The checks are performed on the text within url and anchor text.

for e.g https://en.wikipedia.org/wiki/Valdivian_temperate_rain_forests
from this url, the WikiPrefix is trimmed which gives us: Valdivian_temperate_rain_forests
The remaining url is desegmented wherever  "_" or "-" appears and adds them to a List of strings
String[] list = { Valdivian temperate rain forests }
If the remaining url does not contain "_" or "-", it is stored as is in the list.

Next, anchor text is extracted from the url using JSoup library. The text is seggregated using the same condition as above and store in the list.
The two lists are merged into one list and iterated for each element inside it. Every word is converted to lower case and is checked whether the word
starts with the lower cased keyword. If it matches, the url is added to the HashSet. The url is then added to the queue. The head element is poped out and 
all the urls of that page are crawled until either of the check is enforced. The final list is then added to a HashSet that has only unique entries.

References used:  JSoup cookbook for extracting url https://jsoup.org/cookbook/extracting-data/working-with-urls
				  StackOverflow article on writing elements to a text file.  https://stackoverflow.com/questions/12996199/howto-save-hashsetstring-to-txt