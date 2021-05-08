package beatcovid.scrapper.whatsapp;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Hello world!
 *
 */
public class WhatsappBot 
{
    public static void main( String[] args ) throws FileNotFoundException, IOException
    {
    	Properties prop = new Properties();
		prop.load(new FileInputStream("Resources/xpaths.properties"));
		
		// Opening Whatsapp Web and logging in
		System.setProperty("webdriver.chrome.driver", "E:/Selenium Drivers/chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		WebDriverWait longExpWait = new WebDriverWait(driver, 120);
		WebDriverWait shortExpWait = new WebDriverWait(driver, 30);

		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		driver.get("https://web.whatsapp.com/");
		longExpWait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(prop.getProperty("QRCode"))));
		
		// Finding unread message chats
		List<WebElement> unreadChats = driver.findElements(By.xpath(prop.getProperty("unread")));
		
		//Traversing through all the unread chats one by one and fetching the messages
		for(WebElement unreadChat: unreadChats) {
			unreadChat.click();
			int unreadCount = Integer.parseInt(unreadChat.getText());
			String chatName = driver.findElement(By.xpath(prop.getProperty("chatName"))).getText();
			System.out.println(chatName);
			// All the messages might not be in view so bring all of them in view port first
			List<WebElement> unreadMessages = driver.findElements(By.xpath(prop.getProperty("newUnreadMessages")));
			while(unreadMessages.size() < unreadCount) {
				Coordinates cor = ((Locatable) unreadMessages.get(unreadMessages.size() - 1)).getCoordinates();
				cor.inViewPort();
				unreadMessages = driver.findElements(By.xpath(prop.getProperty("newUnreadMessages")));
			}
			
			for(WebElement unreadMessage: unreadMessages) {
				String text = unreadMessage.getText();
				if(text.contains("Read more")){
					unreadMessage.findElement(By.xpath(prop.getProperty("readMore"))).click();
					text = unreadMessage.getText();
				}
				List<WebElement> images = unreadMessage.findElements(By.xpath(prop.getProperty("image")));
				for(WebElement image: images) {
					String imageText = text.length() > 200?text.substring(0, 200):text;
					//downloadImage(image.getAttribute("src").substring(5), chatName + "_" + imageText);
				}
				System.out.println(text);
			}
			
		}
		
		logout(driver, prop);
		
		driver.quit();
	
    }
    public static void downloadImage(String src, String filename) {
    	BufferedImage bufferedImage = null;
    	File outputfile = new File("Resources/images/"+filename+".jpeg");
    	try {
			bufferedImage = ImageIO.read(new URL(src));
			ImageIO.write(bufferedImage, "jpeg", outputfile);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    }
    public static void logout(WebDriver driver, Properties prop) {
    	driver.findElement(By.xpath(prop.getProperty("mainMenu"))).click();
    	driver.findElement(By.xpath(prop.getProperty("logout"))).click();
    }
}
