package selenium.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.ChromeDriverManager;

public class Participants {
private static WebDriver driver;
	
	@BeforeClass
	public static void setUp() throws Exception 
	{
		ChromeDriverManager.getInstance().setup();
		driver = new ChromeDriver();
	}
	
	@AfterClass
	public static void  tearDown() throws Exception
	{
		driver.close();
		driver.quit();
	}

	
	

	@Test
	public void CheckParticipants() throws Exception
	{
		driver.get("http://www.checkbox.io/studies.html");
		
		// http://geekswithblogs.net/Aligned/archive/2014/10/16/selenium-and-timing-issues.aspx
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='dynamicStudies']/div[8]/div[2]/p/span[1]")));
		List<WebElement> spans = driver.findElements(By.xpath("//*[@id='dynamicStudies']/div[8]/div[2]/p/span[1]"));
		/*wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@class='backers']/span[.='55']")));
		List<WebElement> spans = driver.findElements(By.xpath("//span[@class='backers']/span[.='55']"));*/
		System.out.println(spans.get(0).getText());
		assertNotNull(spans);
		assertEquals(55, Integer.parseInt(spans.get(0).getText()));
		
	}
}
