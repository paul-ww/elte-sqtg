import org.junit.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


public class LandingPageTest {

    public WebDriver driver;
    private String sampleEventName = "My Event";
    private String sampleDateType = "Days of the Week";
    private String[] sampleWeekDays = new String[] {"Monday", "Tuesday"};
    private String sampleTimeEarliest = "7:00 AM";
    private String sampleTimeLatest = "10:00 PM";
    
    @Before
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void testGetLandingPage() {
        LandingPage landingPage = new LandingPage(this.driver);
        Assert.assertEquals(landingPage.getPageTitle(), "When2meet");
    }

    @Test
    public void testFormFilling() {
        LandingPage landingPage = new LandingPage(this.driver);
        landingPage.fillAllForms(this.sampleEventName, this.sampleDateType, this.sampleWeekDays, this.sampleTimeEarliest, this.sampleTimeLatest);
        Assert.assertEquals(landingPage.getSelectedDateType(), this.sampleDateType);
        Assert.assertEquals(landingPage.getSelectedTimeEarliest(), this.sampleTimeEarliest);
        Assert.assertEquals(landingPage.getSelectedTimeLatest(), this.sampleTimeLatest);
    }

    
    @Test
    public void testCreateEvent() {
        LandingPage landingPage = new LandingPage(this.driver);
        EventPage myEventPage = landingPage.createEvent(this.sampleEventName, this.sampleDateType, this.sampleWeekDays, this.sampleTimeEarliest, this.sampleTimeLatest);
               
        // SearchResultPage searchResultPage = mainPage.search("Students");
        // String bodyText = searchResultPage.getBodyText();
        // Assert.assertTrue(bodyText.contains("found"));
        // Assert.assertTrue(bodyText.contains("For Students"));
    }
    

    @After
    public void close() {
        if (driver != null) {
            driver.quit();
        }
    }
}
