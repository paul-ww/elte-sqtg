import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.By;

class LandingPage extends PageBase {

    private final Map<String, String> weekDayMap = Map.of(
            "Sunday", "Day-6-1",
            "Monday", "Day-6-2",
            "Tuesday", "Day-6-3",
            "Wednesday", "Day-6-4",
            "Thursday", "Day-6-5",
            "Friday", "Day-6-6",
            "Saturday", "Day-6-7");
    private By selectDateTypeBy = By.xpath("//select[@id='DateTypes']");
    private By selectTimeEarliestBy = By.xpath("//select[@name='NoEarlierThan']");
    private By selectTimeLatestBy = By.xpath("//select[@name='NoLaterThan']");
    private By inputEventNameBy = By.xpath("//input[@id='NewEventName']");
    private By buttonCreateEventBy = By.xpath("//input[@value='Create Event']");
    private String validTimeStringFormat = "\\d{1,2}:\\d{2} [AP]M";
    private String expectedDaysOfTheWeek = "Days of the Week";
    private String expectedSpecificDates = "Specific Dates";

    public LandingPage(WebDriver driver) {
        super(driver);
        this.driver.get("https://www.when2meet.com/");
    }

    private String getSelection(By selectorBy) {
        Select selector = new Select(this.waitAndReturnElement(selectorBy));
        return selector.getFirstSelectedOption().getText();
    }

    public String getSelectedTimeEarliest() {
        return this.getSelection(this.selectTimeEarliestBy);
    }

    public String getSelectedTimeLatest() {
        return this.getSelection(this.selectTimeLatestBy);
    }

    public String getSelectedDateType() {
        return this.getSelection(this.selectDateTypeBy);
    }

    private void setSelection(By selectorBy, String visibleText) {
        Select selector = new Select(this.waitAndReturnElement(selectorBy));
        selector.selectByVisibleText(visibleText);
    }

    private void selectWeekDay(String weekDayId) {
        String weekDayXpath = String.format("//div[@id='%s']", weekDayId);
        By weekDayBy = By.xpath(weekDayXpath);
        WebElement weekDayElement = this.waitAndReturnElement(weekDayBy);
        weekDayElement.click();
    }

    public void fillAllForms(String eventName, String dateTypeText, String[] weekDays, String timeEarliestText,
            String timeLatestText) {
        if (dateTypeText.equals(this.expectedSpecificDates)) {
            throw new UnsupportedOperationException("Specific dates are not yet implemented.");
        } else if (dateTypeText.equals(this.expectedDaysOfTheWeek)) {
            if (timeEarliestText.matches(this.validTimeStringFormat)
                    & timeLatestText.matches(this.validTimeStringFormat)) {
                this.setSelection(this.selectTimeEarliestBy, timeEarliestText);
                this.setSelection(this.selectTimeLatestBy, timeLatestText);
                this.setSelection(this.selectDateTypeBy, dateTypeText);
                WebElement inputEventName = this.waitAndReturnElement(this.inputEventNameBy);
                inputEventName.clear();
                inputEventName.sendKeys(eventName);
                for (String weekDay : weekDays) {
                    this.selectWeekDay(this.weekDayMap.get(weekDay));
                }
            } else {
                throw new IllegalArgumentException("Invalid time format entered, use e.g. 12:00 AM");
            }

        }
    }

    public EventPage createEvent(String eventName, String dateTypeText, String[] weekDays, String timeEarliestText,
            String timeLatestText) {
        this.fillAllForms(eventName, dateTypeText, weekDays, timeEarliestText, timeLatestText);
        this.waitAndReturnElement(this.buttonCreateEventBy).click();
        return new EventPage(this.driver);
    }
}
