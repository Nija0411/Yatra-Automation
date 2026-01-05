package com.yatra.automation;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class YatraAutomationScript {
    public static void main(String[] args) {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");

        //Step1: Open Browser
        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        //Step2: Load the webpage
        driver.get("https://www.yatra.com");

        closePopupWindow(wait);
        clickOnDepartureDate(wait);

        WebElement currentMonthCalendor = selectTheMonthFromTheCalendor(wait, 0);
        WebElement nextMonthCalendor = selectTheMonthFromTheCalendor(wait, 1);

        String lowestPriceInCurrentMonth = getMeTheLowestPrice(wait, currentMonthCalendor);
        System.out.println("Lowest Price In the Current Month: " + lowestPriceInCurrentMonth);

        String lowestPriceInNextMonth = getMeTheLowestPrice(wait, nextMonthCalendor);
        System.out.println("Lowest Price In the Next Month: " + lowestPriceInNextMonth);

        compareTwoMonthsPrices(lowestPriceInCurrentMonth, lowestPriceInNextMonth);

        closeBrowser(driver);

    }

    public static void closePopupWindow(WebDriverWait wait) {
        By crossLocator = By.xpath("(//img[@alt=\"cross\"])[1]");

        try {
            WebElement crossWebElement = wait.until(ExpectedConditions.elementToBeClickable(crossLocator));
            crossWebElement.click();
        } catch (TimeoutException e) {
            System.out.println("Pop up not found");
        }
    }

    public static void clickOnDepartureDate(WebDriverWait wait) {
        By departureDateLocator = By.xpath("//div[@role=\"button\" and @aria-label=\"Departure Date inputbox\"]");

        WebElement departureDate = wait.until(ExpectedConditions.elementToBeClickable(departureDateLocator));
        departureDate.click();
    }

    public static WebElement selectTheMonthFromTheCalendor(WebDriverWait wait, int index) {
        By calendarMonthPickerLocator = By.xpath("//div[@class='react-datepicker__month-container']");
        List<WebElement> calendorMonthPickerWebElement = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(calendarMonthPickerLocator));

//        System.out.println("Total Months Present: " + calendorMonthPickerWebElement.size());

        WebElement monthWebElement = calendorMonthPickerWebElement.get(index);
        return monthWebElement;

    }

    public static String getMeTheLowestPrice(WebDriverWait wait, WebElement monthWebElement) {
        By priceLocator = By.xpath(".//span[contains(@class,'custom-day-content')]");
        wait.until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(monthWebElement, priceLocator));
        List<WebElement> currentPriceList = monthWebElement.findElements(priceLocator);

        //Print the dates
        int lowestPrice = Integer.MAX_VALUE;
        WebElement priceElement = null;
        for (WebElement price : currentPriceList) {
            String priceString = price.getText();
            if (priceString.length() > 0) {
                priceString = priceString.replace("₹", "").replace(",", "").trim();

                int priceInt = Integer.parseInt(priceString);

                if (priceInt < lowestPrice) {
                    lowestPrice = priceInt;
                    priceElement = price;
                }
            }
        }
//        System.out.println(lowestPrice);
        WebElement dateElement = priceElement.findElement(By.xpath(".//..//.."));
        String result = dateElement.getAttribute("aria-label") + "--- Price is Rs" + lowestPrice;
        return result;
    }

    public static void compareTwoMonthsPrices(String currentMonthPrice, String nextMonthPrice) {
        int currentMonthRsIndex = currentMonthPrice.indexOf("Rs");
        int nextMonthRsIndex = nextMonthPrice.indexOf("Rs");

        String currentPrice = currentMonthPrice.substring(currentMonthRsIndex + 2);
        String nextPrice = nextMonthPrice.substring(nextMonthRsIndex + 2);

        int current = Integer.parseInt(currentPrice);
        int next = Integer.parseInt(nextPrice);

        if (current < next) {
            System.out.println("Current Month Price is the lowest: " + current);
        } else if (current == next) {
            System.out.println("Both Prices are equal: " + current);
        } else {
            System.out.println("Next Month Price is the lowest: " + next);
        }
    }

    public static void closeBrowser(WebDriver driver) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        driver.quit();
    }
}
