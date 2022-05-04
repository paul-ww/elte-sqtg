import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

class EventPage extends PageBase {

    private By divEventNameBy = By.xpath("//div[@id='NewEventNameDiv']");
    private By inputUsernameBy = By.xpath("//input[@id='name']");
    private By inputPasswordBy = By.xpath("//input[@id='password']");
    private By buttonSubmitCredentialsBy = By.xpath("//input[@value='Sign In']");
    private By divMinAvailableBy = By.xpath("//div[@id='MinAvailable']");
    private By divMaxAvailableBy = By.xpath("//div[@id='MaxAvailable']");
    private By userGridBy = By.xpath("//div[@id='YouGridSlots']");

    public EventPage(WebDriver driver) {
        super(driver);
    }

    public EventPage(WebDriver driver, String url) {
        super(driver);
        this.driver.get(url);
    }

    private void setUsername(String username) {
        this.waitAndReturnElement(this.inputUsernameBy).sendKeys(username);
    }

    private void setPassword(String password) {
        this.waitAndReturnElement(this.inputPasswordBy).sendKeys(password);
    }

    private Map<String, Map<String, WebElement>> buildDayTimeToDivMap() {
        Map<String, Map<String, WebElement>> dayTimeToDivMap = new HashMap<String, Map<String, WebElement>>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US);
        WebElement userGrid = this.waitAndReturnElement(userGridBy);
        List<WebElement> allDivs = userGrid.findElements(By.xpath("//div//div[starts-with(@id, 'YouTime')]"));
        for (WebElement div : allDivs) {
            String timestamp = div.getAttribute("data-time");
            LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(timestamp)),
                    ZoneOffset.UTC);
            String weekday = date.getDayOfWeek().toString().toLowerCase();
            weekday = weekday.substring(0, 1).toUpperCase() + weekday.substring(1);
            String time = date.format(formatter);

            Map<String, WebElement> innerMap = dayTimeToDivMap.get(weekday);
            if (innerMap == null) {
                dayTimeToDivMap.put(weekday, innerMap = new HashMap<String, WebElement>());
            } else {
                innerMap.put(time, div);
            }
        }
        return dayTimeToDivMap;
    }

    private WebElement[] getStartEndElements(Map<String, Map<String, WebElement>> dayTimeToDivMap, String weekday,
            String timeEarliest, String timeLatest) {
        WebElement divStart = dayTimeToDivMap.get(weekday).get(timeEarliest);
        WebElement divEnd = dayTimeToDivMap.get(weekday).get(timeLatest);
        return new WebElement[] { divStart, divEnd };
    }

    private void makeSelection(WebElement[] divsToSelectBetween) {
        WebDriverWait wait = new WebDriverWait(driver, 5);
        WebElement divStart = wait.until(ExpectedConditions.visibilityOf(divsToSelectBetween[0]));
        WebElement divEnd = wait.until(ExpectedConditions.visibilityOf(divsToSelectBetween[1]));
        Actions act = new Actions(this.driver);
        act.dragAndDrop(divStart, divEnd).build().perform();
    }

    public void setAvailability(Map<String, Map<String, String>> availability) {
        Map<String, Map<String, WebElement>> dayTimeToDivMap = this.buildDayTimeToDivMap();
        availability.forEach((weekday, times) -> makeSelection(
                getStartEndElements(dayTimeToDivMap, weekday, times.get("from"), times.get("to"))));
    }

    public void setCredentials(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
        this.waitAndReturnElement(this.buttonSubmitCredentialsBy).click();
    }

    public String getEventName() {
        return this.waitAndReturnElement(this.divEventNameBy).getText().split("\n")[0];
    }

    public Integer getMinAvailability() {
        return Integer.parseInt(this.waitAndReturnElement(this.divMinAvailableBy).getText().split("/")[0]);
    }

    public Integer getMaxAvailability() {
        return Integer.parseInt(this.waitAndReturnElement(this.divMaxAvailableBy).getText().split("/")[0]);
    }

    public Integer getGroupSize() {
        return Integer.parseInt(this.waitAndReturnElement(this.divMaxAvailableBy).getText().split("/")[1]);
    }

}