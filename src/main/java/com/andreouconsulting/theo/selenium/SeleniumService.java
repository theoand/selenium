package com.andreouconsulting.theo.selenium;

import static com.andreouconsulting.theo.selenium.Browser.CHROME;
import static com.andreouconsulting.theo.selenium.Browser.FIREFOX;
import static com.andreouconsulting.theo.selenium.Browser.OPERA;
import static com.andreouconsulting.theo.selenium.Browser.PHANTOMJS;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.replacePattern;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.opera.OperaDriverService;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;

/**
 * Service class that provides ability to set-up a browser and execute actions on the screen.
 * <p>
 * It also takes screenshot after each action if it is enabled.  
 * 
 * @author theo@andreouconsulting.com
 *
 */
public class SeleniumService {

	private static final Logger logger = LogManager.getLogger(SeleniumService.class);
	private static final String SELENIUM_SCREENSHOTS_URL = "./selenium_screenshots/%s_%s.png";
	private static final String XPATH_FOR_ATTRIBUTE_MATCHING = ".//*[contains(concat(' ',normalize-space(@%s),' '),'%s')" + "]";
	private final boolean takeScreenshots;
	private final WebDriver driver;
	
	
	/**
	 * Setup the service. This defaults the ability to take screenshots to false. 
	 * 
	 * @param browser
	 * @throws UnsupportedException
	 * @throws IOException
	 */
	public SeleniumService(Browser browser) throws UnsupportedException, IOException {
		this(browser,false);
	}
	
	
	/**
	 * Setup the service
	 * @param browser
	 * @param takeScreenshots - override the ability to take screenshots
	 * @throws UnsupportedException
	 * @throws IOException
	 */
	public SeleniumService(Browser browser, boolean takeScreenshots) throws UnsupportedException, IOException {
		switch (browser) {
		case CHROME:
			driver = createChromeDriver();
			break;
		case FIREFOX:
			driver = createFirefoxDriver();
			break;
		case OPERA:
			driver = createOperaDriver();
			break;
		case PHANTOMJS:
			driver = createPhantomJSDriver();
			break;
		default:
			throw new UnsupportedException("The provided driver is not supported.");
		}
		this.takeScreenshots = takeScreenshots;
	}
	
	
	/**
     * Quits this driver, closing every associated window.
     * <p>
     * Make sure to call this as the last action of your test. 
	 */
	public void terminate() {
		driver.close();
		driver.quit();
		logger.info("Terminating web driver...");
	}

	
	/**
	 * Returns the driver currently installed.
	 * 
	 * @return
	 */
	public WebDriver getDriver() {
		return this.driver;
	}
	

	private OperaDriver createOperaDriver() {
		OperaDriverService service = new OperaDriverService.Builder()
				.usingDriverExecutable(new File(OPERA.getPath()))
				.usingAnyFreePort()
				.build();
		logger.info("Setting-up Opera web-driver..");
		return new OperaDriver(service);
	}
	
	
	private ChromeDriver createChromeDriver() throws IOException{
		ChromeDriverService service = new ChromeDriverService.Builder()
				.usingDriverExecutable(new File(CHROME.getPath()))
				.usingAnyFreePort()
				.build();
		logger.info("Setting-up Chrome web-driver..");
		return new ChromeDriver(service);
	}

	
	private PhantomJSDriver createPhantomJSDriver() {
		DesiredCapabilities caps = new DesiredCapabilities();
		caps.setJavascriptEnabled(true);
		caps.setCapability("takesScreenshot", true);
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, PHANTOMJS.getPath());
		logger.info("Setting-up PhantomJS web-driver..");
		return new PhantomJSDriver(caps);
	}
	

	private FirefoxDriver createFirefoxDriver() {
		System.setProperty("webdriver.gecko.driver",FIREFOX.getPath());
		logger.info("Setting-up Firefox web-driver..");
		return new FirefoxDriver();
	}

	
	/**
	 * Navigates to the provided URL.
	 * 
	 * @param url
	 */
	public void navigateTo(String url) {
		logger.info("Navigating to url: " + url);
		driver.navigate().to(url);
		if(takeScreenshots)
			takeScreenshot(replaceUrlPattern(driver.getCurrentUrl()), "navigateTo");
	}

	
	/**
	 * Searches for an element with the provided xpath.
	 * 
	 * @param xpath
	 * @return
	 */
	public WebElement findElementWithXpath(String xpath) {
		return driver.findElement(ByXPath.xpath(xpath));
	}
	
	
	/**
	 * Searches for elements with the provided attributed that matches the word provided.
	 * 
	 * @param xpath
	 * @return
	 */
	public List<WebElement> findElementsWithXpath(String xpath) {
		return driver.findElements(ByXPath.xpath(xpath));
	}
	
	
	/**
	 * Searches for an element with the provided attributed that matches the word provided.
	 * 
	 * @param attribute
	 * @param word
	 * @return
	 */
	public WebElement findElementByAtrribute(String attribute, String word) {
		return findElementWithXpath(format(XPATH_FOR_ATTRIBUTE_MATCHING,attribute, word));
	}


	/**
	 * Searches for an element with the provided "id".
	 * 
	 * @return 
	 */
	public WebElement findElementById(String id) {
		return findElementByAtrribute("id", id);		
	}
	
	
	/**
	 * Enters the provided text to the element with the matching id.
	 * 
	 * @param id
	 * @param text
	 */
	public void enterTextToFieldWithId(String id, String text){
		enterTextToFieldWithAttribute("id", id, text);
	}
	
	
	/**
	 * Enters the provided text to the element with the matching xpath.
	 * 
	 * @param xpath
	 * @param text
	 */
	public void enterTextToFieldWithXpath(String xpath, String text){
		findElementWithXpath(xpath).sendKeys(text);
		if(takeScreenshots)
			takeScreenshot(replaceUrlPattern(driver.getCurrentUrl()), "enterTextToField");
	}
	
	/**
	 * Enters the provided text to the element with the matching attribute.
	 * 
	 * @param id
	 * @param text
	 */
	public void enterTextToFieldWithAttribute(String attribute, String word, String text){
		findElementByAtrribute(attribute, word).sendKeys(text);
		if(takeScreenshots)
			takeScreenshot(replaceUrlPattern(driver.getCurrentUrl()), "enterTextToField");
	}


	/**
	 * Clicks the element with the matching id.
	 * 
	 * @param id
	 */
	public void clickButtonWithId(String id) {
		clickButtonWithAttribute("id", id);
	}
	
	
	/**
	 * Clicks the element with the matching attribute. 
	 * 
	 * @param attribute
	 * @param word
	 */
	public void clickButtonWithAttribute(String attribute, String word) {
		findElementByAtrribute(attribute, word).click();
		if(takeScreenshots)
			takeScreenshot(replaceUrlPattern(driver.getCurrentUrl()), "clickButton");
	}
	
	
	/**
	 * Clicks the element with the matching xpath. 
	 * 
	 * @param xpath
	 */
	public void clickButtonWithXpath(String xpath) {
		findElementWithXpath(xpath).click();
		if(takeScreenshots)
			takeScreenshot(replaceUrlPattern(driver.getCurrentUrl()), "clickButton");
	}
	
	
	/**
	 * Select item with matching text from dropdrown with the matching attribute. 
	 * 
	 * @param attribute
	 * @param word
	 * @param value - the text that is shown on the dropdown
	 */
	public void selectByTextFromInputWithAttribute(String attribute, String word, String value) {
		Select select = new Select(findElementByAtrribute(attribute, word));
		select.selectByVisibleText(value);
		if(takeScreenshots)
			takeScreenshot(replaceUrlPattern(driver.getCurrentUrl()), "selectByTextFromInput");
	}


	/**
	 * Select item with matching text from dropdrown with the matching id. 
	 * 
	 * @param attribute
	 * @param word
	 * @param value - the text that is shown on the dropdown
	 */
	public void selectByTextFromInputWithId(String word, String value){
		selectByTextFromInputWithAttribute("id", word, value);
	}
	
	
	/**
	 * Waits for page with provided the matching attribute to load.
	 * 
	 * @param title
	 * @throws Exception
	 */
	public void waitForPageToLoad(String attribute, String word) throws Exception {
		waitForPageToLoadWithXpath(String.format(XPATH_FOR_ATTRIBUTE_MATCHING,attribute, word));
	}
	
	
	/**
	 * Waits for page with provided the matching attribute to load.
	 * 
	 * @param title
	 * @throws Exception
	 */
	public void waitForPageToLoadWithXpath(String xpath) throws Exception {
	    (new WebDriverWait(driver, 30)).until(new Predicate<WebDriver>() {
	        @Override
	        public boolean apply(WebDriver driver) {
	        	WebElement obj = findElementWithXpath(xpath);
	            if (obj == null) {
	                return false;
	            }
	            return true;
	        }
	    });
	}
	
	
	private void takeScreenshot(String url, String action) {
		File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			// now copy the  screenshot to desired location using copyFile
			FileUtils.copyFile(src, new File(String.format(SELENIUM_SCREENSHOTS_URL, url, action)));
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
	
	
	private String replaceUrlPattern(String url) {
		return replacePattern(url,"/|:|&|=|\\?","");
	}
}

