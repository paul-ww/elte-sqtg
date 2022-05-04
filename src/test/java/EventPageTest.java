import java.util.Map;

import org.junit.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class EventPageTest {

    public WebDriver driver;
    private String sampleEventName = "My Event";
    private String sampleDateType = "Days of the Week";
    private String[] sampleWeekDays = new String[] { "Monday", "Tuesday", "Friday" };
    private String sampleTimeEarliest = "7:00 AM";
    private String sampleTimeLatest = "10:00 PM";
    private String sampleUser = "Langos";
    private String samplePassword = "Kurtoskalacs";
    private final Map<String, Map<String, String>> sampleAvailability = Map.of(
            "Monday", Map.of(
                    "from", "10:00 AM",
                    "to", "3:00 PM"),
            "Tuesday", Map.of(
                    "from", "1:00 PM",
                    "to", "5:00 PM"),
            "Friday", Map.of(
                    "from", "8:00 AM",
                    "to", "9:00 PM"));

    @Before
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void testGetEventPage() {
        LandingPage landingPage = new LandingPage(this.driver);
        EventPage eventPage = landingPage.createEvent(this.sampleEventName, this.sampleDateType, this.sampleWeekDays,
                this.sampleTimeEarliest, this.sampleTimeLatest);
        String expectedTitle = String.format("%s - When2meet", this.sampleEventName);
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.titleContains(expectedTitle));
        Assert.assertEquals(expectedTitle, eventPage.getPageTitle());
    }

    @Test
    public void testSetAvailability() {
        LandingPage landingPage = new LandingPage(this.driver);
        EventPage eventPage = landingPage.createEvent(this.sampleEventName, this.sampleDateType, this.sampleWeekDays,
                this.sampleTimeEarliest, this.sampleTimeLatest);
        eventPage.setCredentials(this.sampleUser, this.samplePassword);
        eventPage.setAvailability(this.sampleAvailability);
        Assert.assertEquals(Integer.valueOf(1), eventPage.getGroupSize());
    }

    @Test
    public void testMultiPersonAvailability() {
        // add the event from the first user
        LandingPage firstLandingPage = new LandingPage(this.driver);
        EventPage firstUserEventPage = firstLandingPage.createEvent(this.sampleEventName, this.sampleDateType,
                this.sampleWeekDays, this.sampleTimeEarliest, this.sampleTimeLatest);
        firstUserEventPage.setCredentials(this.sampleUser, this.samplePassword);
        firstUserEventPage.setAvailability(this.sampleAvailability);
        String firstUserEventPageUrl = firstUserEventPage.getUrl();

        // use the first user's event url and add availability of a second user
        EventPage secondUserEventPage = new EventPage(driver, firstUserEventPageUrl);
        secondUserEventPage.setCredentials("Gulash", "isVeryTasty");
        secondUserEventPage.setAvailability(Map.of(
                "Monday", Map.of(
                        "from", "11:00 AM",
                        "to", "2:00 PM")));
        Assert.assertEquals(firstUserEventPage.getEventName(), secondUserEventPage.getEventName());
        Assert.assertEquals(Integer.valueOf(2), secondUserEventPage.getGroupSize());
    }

    @After
    public void close() {
        if (driver != null) {
            driver.quit();
        }
    }

}