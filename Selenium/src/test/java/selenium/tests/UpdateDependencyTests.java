package selenium.tests;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.ChromeDriverManager;

public class UpdateDependencyTests {

	public static final String USERNAME = "danwrice";
	public static final String ACCESS_KEY = "00c68c13-cc35-446f-8403-3d49531e4c2b";
	public static final String URL = "https://" + USERNAME + ":" + ACCESS_KEY + "@ondemand.saucelabs.com:443/wd/hub";
	private static WebDriver driver;
		
		@Before
		public void setUp() throws Exception 
		{
			DesiredCapabilities caps = DesiredCapabilities.chrome();
			ChromeDriverManager.getInstance().setup();
			driver = new RemoteWebDriver(new URL(URL), caps);
		}
		
		@After
		public void  tearDown() throws Exception
		{
			driver.close();
			driver.quit();
		}
		@Test
		public void selectValidDependency()
		{
			driver.get("https://csc510testingslackbot.slack.com/");

			// Wait until page loads and we can see a sign in button.
			WebDriverWait wait = new WebDriverWait(driver, 30);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("signin_btn")));

			// Find email and password fields.
			WebElement email = driver.findElement(By.id("email"));
			WebElement pw = driver.findElement(By.id("password"));

			// Type in our test user login info.
			email.sendKeys("buildslacker@gmail.com");
			pw.sendKeys("Buildslacker123");

			// Click
			WebElement signin = driver.findElement(By.id("signin_btn"));
			signin.click();

			// Wait until we go to general channel.
			wait.until(ExpectedConditions.titleContains("general"));

			// Switch to #bots channel and wait for it to load.
			driver.get("https://csc510testingslackbot.slack.com/messages/general");
			wait.until(ExpectedConditions.titleContains("general"));

			// Type something
			WebElement messageBot = driver.findElement(By.id("message-input"));
			messageBot.sendKeys("@bsbot2 check for updates");
			messageBot.sendKeys(Keys.RETURN);

			//wait.withTimeout(3, TimeUnit.SECONDS).ignoring(StaleElementReferenceException.class);
			try {
				TimeUnit.SECONDS.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			List<WebElement> msg = driver.findElements(By.xpath("//span[@class='message_body']"));
			
			assertEquals("Your project can be updated to the following dependencies:\n"
					+"1. io.dropwizard.metrics:metrics-core:3.1.0:3.1.2\n"+
					"3. junit:junit:3.8.1:4.12", msg.get(msg.size() -2).getText());
			
			messageBot.sendKeys("3");
			messageBot.sendKeys(Keys.RETURN);

			try {
				TimeUnit.SECONDS.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//wait.withTimeout(3, TimeUnit.SECONDS).ignoring(StaleElementReferenceException.class);
			msg = driver.findElements(By.xpath("//span[@class='message_body']"));
			int i= msg.size();
			assertEquals("Update Successful", msg.get(i-1).getText());
			
		}
		
		@Test
		public void selectInvalidDependency()
		{
			driver.get("https://csc510testingslackbot.slack.com/");

			// Wait until page loads and we can see a sign in button.
			WebDriverWait wait = new WebDriverWait(driver, 30);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("signin_btn")));

			// Find email and password fields.
			WebElement email = driver.findElement(By.id("email"));
			WebElement pw = driver.findElement(By.id("password"));

			// Type in our test user login info.
			email.sendKeys("buildslacker@gmail.com");
			pw.sendKeys("Buildslacker123");

			// Click
			WebElement signin = driver.findElement(By.id("signin_btn"));
			signin.click();

			// Wait until we go to general channel.
			wait.until(ExpectedConditions.titleContains("general"));

			// Switch to #bots channel and wait for it to load.
			driver.get("https://csc510testingslackbot.slack.com/messages/general");
			wait.until(ExpectedConditions.titleContains("general"));

			// Type something
			WebElement messageBot = driver.findElement(By.id("message-input"));
			messageBot.sendKeys("@bsbot2 check for updates");
			messageBot.sendKeys(Keys.RETURN);

			//wait.withTimeout(3, TimeUnit.SECONDS).ignoring(StaleElementReferenceException.class);
			try {
				TimeUnit.SECONDS.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			/**
			 * This one will also be a flaky test because it will actually treat the dependency
			 * as having been updated during the first test.
			 */
			List<WebElement> msg = driver.findElements(By.xpath("//span[@class='message_body']"));
			
			assertEquals("Your project can be updated to the following dependencies:\n"
					+"1. io.dropwizard.metrics:metrics-core:3.1.0:3.1.2\n"+
					"3. junit:junit:3.8.1:4.12", msg.get(msg.size() -2).getText());
			
			messageBot.sendKeys("2");
			messageBot.sendKeys(Keys.RETURN);

			try {
				TimeUnit.SECONDS.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//wait.withTimeout(3, TimeUnit.SECONDS).ignoring(StaleElementReferenceException.class);
			msg = driver.findElements(By.xpath("//span[@class='message_body']"));
			int i= msg.size();
			assertEquals("Invalid selection. That dependency cannot be updated.", msg.get(i-1).getText());
			
		}
	}
