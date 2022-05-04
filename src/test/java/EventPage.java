import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

class EventPage extends PageBase {

    private By divEventNameBy = By.xpath("//div[@id='NewEventNameDiv']");
    private By inputUsernameBy = By.xpath("//input[@id='name']");
    private By inputPasswordBy = By.xpath("//input[@id='password']");
    private By buttonSubmitCredentialsBy = By.xpath("//input[@value='Sign In']");
    private By divMinAvailableBy = By.xpath("//div[@id='MinAvailable']");
    private By divMaxAvailableBy = By.xpath("//div[@id='MaxAvailable']");
    private By groupGridBy = By.xpath("//div[@id='GroupGridSlots']");
    private By userGridBy = By.xpath("//div[@id='YouGridSlots']");
    
    public EventPage(WebDriver driver) {
        super(driver);
    }

    private void setUsername(String username) {
        this.waitAndReturnElement(this.inputUsernameBy).sendKeys(username);
    }

    private void setPassword(String password) {
        this.waitAndReturnElement(this.inputPasswordBy).sendKeys(password);
    }

    private Map<String, Map<String, WebElement>> buildDayTimeToDivMap() {
        Map<String, Map<String, WebElement>> dayTimeToDivMap = new HashMap<String, Map<String, WebElement>>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:m a");
        List<WebElement> allDivs = this.waitAndReturnElement(userGridBy).findElements(By.xpath("//div//div[starts-with(@id, 'YouTime')]"));
        for (WebElement div : allDivs) {
            String timestamp = div.getAttribute("data-time");
            LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(timestamp)), ZoneOffset.UTC);
            String weekday = date.getDayOfWeek().toString();
            String time = date.format(formatter);
            dayTimeToDivMap.put(
                weekday, Map.of(time, div)
            );
        }
        return dayTimeToDivMap;
    }

    private WebElement[] getStartEndElements(Map<String, Map<String, WebElement>> dayTimeToDivMap, String weekday, String timeEarliest, String timeLatest) {
        WebElement divStart = dayTimeToDivMap.get(weekday).get(timeEarliest);
        WebElement divEnd = dayTimeToDivMap.get(weekday).get(timeLatest);
        return new WebElement[] {divStart, divEnd};
    }

    private void makeSelection(WebElement[] divsToSelectBetween) {
        WebElement divStart = divsToSelectBetween[0];
        WebElement divEnd = divsToSelectBetween[1];
        Actions act = new Actions(this.driver);
        act.dragAndDrop(divStart, divEnd).build().perform();
    }

    public void selectDaysAndTimes(Map<String, Map<String, String>> availability) {
        Map<String, Map<String, WebElement>> dayTimeToDivMap = this.buildDayTimeToDivMap();
        availability.forEach((weekday, times) -> makeSelection(getStartEndElements(dayTimeToDivMap, weekday, times.get("from"), times.get("to"))));
    }

    public void setCredentials(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
        this.waitAndReturnElement(this.buttonSubmitCredentialsBy).click();
    }

    public String getEventName() {
        return this.waitAndReturnElement(this.divEventNameBy).getText();
    }

    public String getMinAvailability() {
        return this.waitAndReturnElement(this.divMinAvailableBy).getText().split("/")[0];
    }

    public String getMaxAvailability() {
        return this.waitAndReturnElement(this.divMaxAvailableBy).getText().split("/")[0];
    }

    public String getGroupSize() {
        return this.waitAndReturnElement(this.divMaxAvailableBy).getText().split("/")[1];
    }


}