package script;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class amaCollector {
		// Web Driver
		private static WebDriver driver = null;
		
		// pages to search, set to 2 as default
		private static int pageCount = 2;
		
		// store goods info
		private static List<String> goodsList = new ArrayList<String>();
		// store prices info
		private static List<String> priceList = new ArrayList<String>();
		
		static {
			// route for ChromeDriver, modify based on environment
			System.setProperty("webdriver.chrome.driver","F:\\WebDriver\\chromedriver97\\chromedriver.exe");
			driver = new ChromeDriver();
		}
		
		public static void main(String[] args) {
			Scanner userInput = new Scanner(System.in);
			System.out.print("Please enter keyword to search:");
			String key = userInput.nextLine();
			userInput.close();
			try {
				searchAndPrint(key);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(driver != null) {
//						driver.quit();
				}
			}
		}
		/**
		 * Search goods with given keyword
		 * then print all the info gathered from web page
		 * @param keyWord - used to search on web for goods info and price
		 */
		private static void searchAndPrint(String keyword) {
			if(keyword == null || keyword.length() == 0) {
				System.out.println("Please enter valid keyword!");
				return;
			}
			driver.get("https://www.amazon.com/");
			driver.findElement(By.id("twotabsearchtextbox")).sendKeys(keyword);
			driver.findElement(By.id("nav-search-submit-button")).click();
			for(int i = 0; i < pageCount; i++) {
				try {
					getGoods();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			for(int i = 0; i < goodsList.size() && i < priceList.size(); i++) {
				System.out.println("Items" + (i + 1) + ": " + goodsList.get(i) + "w/ price: " + priceList.get(i));
			}
			System.out.println("======= End =======");
		}
		
		
		
		/**
		 * Get goods info and price from the current web page 
		 */
		private static void getGoods() {
			sleep(1);
			((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
			sleep(1);
			List<WebElement> elementList = driver.findElements(By.xpath("//div[@class='s-main-slot s-result-list s-search-results sg-row']/div"));

			elementList.remove(0);
			elementList.remove(elementList.size() - 1);
			
			for(WebElement element : elementList) {

				try {
					String text = element.findElement(By.className("a-text-normal")).getText();
					String whole = element.findElement(By.className("a-price-whole")).getText();
					String fraction = element.findElement(By.className("a-price-fraction")).getText();
					StringBuffer price = new StringBuffer();
					if(whole != null || whole.length() != 0 || fraction != null || fraction.length() != 0) {
						
						price.append(whole);
						price.append(".");
						price.append(fraction);
						
					}
					String finalPrice = price.toString();
					if(text != null || text.length() != 0 || price != null || price.length() != 0) {
						goodsList.add(text);
						priceList.add(finalPrice);
					}
					
					
				} catch (Exception e) {
					continue;
				}
				
			}
			// 2022.01.08 update:
			// Amazon might change their web elements structure
			// Do not use class name when searching button 
//			driver.findElement(By.className("s-pagination-next")).click();
			
			// Search Next button with its text 
			driver.findElement(By.xpath("//*[text()='Next']")).click();
			
		}
		
		/**
		 * Sleep thread for num seconds
		 * @param num - sleep times
		 */
		private static void sleep(int num) {
			try {
				Thread.sleep(num * 1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
}
