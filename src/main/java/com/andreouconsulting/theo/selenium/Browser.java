package com.andreouconsulting.theo.selenium;

/**
 * Different types of drivers supported by Selenium.
 * 
 * Make sure that you have placed the correct driver in your project's path.
 * 
 * @author theo@andreouconsulting.com
 *
 */
public enum Browser {
	/**
	 * Opera driver.
	 */
	OPERA("src/main/resources/operadriver.exe"),

	/**
	 * Firefox driver.
	 */
	FIREFOX("src/main/resources/geckodriver.exe"),

	/**
	 * Chrome driver.
	 */
	CHROME("src/main/resources/chromedriver.exe"),

	/**
	 * PhantomJS (the invisible) driver.
	 */
	PHANTOMJS("src/main/resources/phantomjs.exe");

	private final String path;

	Browser(String path) {
		this.path = path;
	}

	/**
	 * Returns the path where the web-driver should exist.
	 * 
	 * @return
	 */
	public String getPath() {
		return path;
	}

}
