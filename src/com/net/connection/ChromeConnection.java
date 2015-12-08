package com.net.connection;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.Sleeper;

public class ChromeConnection {
	public static void main(String[] args) {
		
		System.setProperty("webdriver.firefox.bin", "D:/Program Files (x86)/Mozilla firefox/firefox.exe");
		
		WebDriver driver = new FirefoxDriver();
		
		driver.get("https://kyfw.12306.cn/otn/login/init");
		
		WebElement name = driver.findElement(By.id("username"));
		name.sendKeys("873059043@qq.com");
		
		WebElement passwd = driver.findElement(By.id("password"));
		passwd.sendKeys("china2013");
		
		Sleeper(8000);
		
		WebElement login = driver.findElement(By.id("loginSub"));
		login.click();
		
		driver.get("https://kyfw.12306.cn/otn/leftTicket/init");
		
		driver.manage().window().maximize();
		
		WebElement startbox = driver.findElement(By.id("fromStationText"));
		startbox.click();
		startbox.sendKeys("上海\n");
		
		
		WebElement endbox = driver.findElement(By.id("toStationText"));
		endbox.click();
		endbox.sendKeys("南阳\n");		
		
		((JavascriptExecutor)driver).executeScript("document.getElementById(\"train_date\").value=\"2015-12-02\"");
		
		WebElement box = driver.findElement(By.id("query_ticket"));
		
		for(int i=0;i<2;i++){
			Sleeper(2000);
			box.click();
		}
		
		((JavascriptExecutor)driver).executeScript("document.getElementById(\"checkbox_14ksF8TbKJ\").click()");

		((JavascriptExecutor)driver).executeScript("document.getElementsByClassName(\"btn72\")[0].click()");
		
//		driver.close();
	}

	private static void Sleeper(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
