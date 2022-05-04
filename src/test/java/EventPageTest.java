import java.util.Map;

import org.junit.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


public class EventPageTest {

    public WebDriver driver;
    private String sampleEventName = "My Event";
    private String sampleDateType = "Days of the Week";
    private String[] sampleWeekDays = new String[] {"Monday", "Tuesday"};
    private String sampleTimeEarliest = "7:00 AM";
    private String sampleTimeLatest = "10:00 PM";
    private String sampleUser = "Langos";
    private String samplePassword = "Kurtoskalacs";
    private final Map<String, Map<String, String>> sampleAvailability = Map.of(
        "Monday", Map.of(
            "from", "10:00 AM",
            "to", "3:00 PM"
        ),
        "Tuesday", Map.of(
            "from", "1:00 PM",
            "to", "17:00 PM"
        )
    );
    
    @Before
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void testGetEventPage() {
        LandingPage landingPage = new LandingPage(this.driver);
        EventPage eventPage = landingPage.createEvent(this.sampleEventName, this.sampleDateType, this.sampleWeekDays, this.sampleTimeEarliest, this.sampleTimeLatest);
        Assert.assertEquals(String.format("%s - When2meet", this.sampleEventName), eventPage.getPageTitle());
    }

    @Test
    public void testSetAvailability() {
        LandingPage landingPage = new LandingPage(this.driver);
        EventPage eventPage = landingPage.createEvent(this.sampleEventName, this.sampleDateType, this.sampleWeekDays, this.sampleTimeEarliest, this.sampleTimeLatest);
        eventPage.setCredentials(this.sampleUser, this.samplePassword);
        eventPage.selectDaysAndTimes(this.sampleAvailability);
    }


    @After
    public void close() {
        if (driver != null) {
            driver.quit();
        }
    }

}