import org.junit.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


public class FirstSeleniumTest {
    public WebDriver driver;
    
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
    public void testCreateEvent() {
        LandingPage landingPage = new LandingPage(this.driver);
        EventPage myEventPage = landingPage.createEvent("Sample Event", "Days of the Week", new String[] {"Monday", "Tuesday"}, "7:00 AM", "10:00 PM");
               
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
