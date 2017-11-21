import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class Mining_bot
{
    private static WebDriver driver;
    private WebElement oneOre;
    private long timount = 10;
    private double allowHpOre = 0.75;
    private boolean exit = false;

    @Test
    public void MiningAll()
    {
        System.setProperty("webdriver.chrome.driver", "WebDrivers\\chromedriver.exe");
        //System.setProperty("webdriver.ie.driver", "WebDrivers\\IEDriverServer.exe");

        driver = new ChromeDriver();
        Battle_bot b_bot = new Battle_bot();
        //WebDriver driver = new InternetExplorerDriver();
        String Path = "https://eternitytower.net/mining";
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(3, SECONDS);
        driver.get(Path);

        if (isElementPresent(By.cssSelector("div.at-error.alert.alert-danger > div"), driver))
        {
            if (driver.findElement(By.cssSelector("div.at-error.alert.alert-danger > div")).getText().equals("Must be logged in"))
            {
                WebElement loginField = driver.findElement(By.id("at-field-username_and_email"));
                WebElement passField = driver.findElement(By.id("at-field-password"));

                loginField.sendKeys("Deventur");
                passField.sendKeys("qazxcv");

                WebElement signBtn = driver.findElement(By.id("at-btn"));
                signBtn.click();

            }//div.d-flex.align-items-center

        }

        if (isElementPresent(By.cssSelector("a.nav-link.equipmentLink"), driver))
        {
            WebElement eqTab = driver.findElement(By.cssSelector("a.nav-link.equipmentLink"));
            eqTab.click();

            WebElement pick = driver.findElement(By.cssSelector("div.d-flex.flex-column.ml-3 > div:nth-child(1)"));
            double dmgPick = Double.parseDouble(pick.getText().substring(0, pick.getText().indexOf("\n")));
            //minePitLink
            WebElement mineTab = driver.findElement(By.cssSelector("a.nav-link.minePitLink"));
            mineTab.click();

            driver.manage().timeouts().implicitlyWait(500, MILLISECONDS);
            if(isElementPresent(By.cssSelector("img.minimize-icon"),driver))
            {
                driver.findElement(By.cssSelector("img.minimize-icon")).click();
            }
            while (true)
            {
                try
                {
                    WebElement enMiming = driver.findElement(By.cssSelector("div.d-flex.flex-column.mb-3 > div.d-flex"));
                    String en = enMiming.getText();
                    double curEnergy = Double.parseDouble(en.split(" / ")[0]);
                    double fullEnergy = Double.parseDouble(en.split(" / ")[1]);

                    if (curEnergy <= 1.0)
                        try {
                            sleep(this.timount * 60000);
//                            b_bot.plantation(driver, "dragonfruitSeed");//идем ростить.
//                            driver.get("https://eternitytower.net/mining");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    List<WebElement> ore = driver.findElements(By.cssSelector("div.icon-box.mine-space-container"));
                    for (WebElement oneOre : ore) {
                        try {
                            if (isChildElementPresent(By.cssSelector("img.ore-icon"), oneOre)) {
                                WebElement img = oneOre.findElement(By.cssSelector("img.ore-icon"));
                                String srcImg = img.getAttribute("src").toLowerCase();

                                String oreHP = oneOre.findElement(By.cssSelector("span")).getText();
                                //Проверяем ХП
                                Double curOreHP = Double.parseDouble(oreHP.split(" / ")[0].replace(".", "").replace("k", "00"));
                                Double fullOreHP = Double.parseDouble(oreHP.split(" / ")[1].replace(".", "").replace("k", "00"));
                                //Если это GEM, то копаем их несчадно
                                if (srcImg.contains("jade") || srcImg.contains("sapphire") || srcImg.contains("lapis") || srcImg.contains("ruby") || srcImg.contains("emerald") || srcImg.contains("gem")) {

                                    Double needClickCnt = curOreHP / dmgPick;
                                    while (needClickCnt >= 0.1) {
                                        enMiming = driver.findElement(By.cssSelector("div.d-flex.flex-column.mb-3 > div.d-flex"));
                                        en = enMiming.getText();
                                        curEnergy = Double.parseDouble(en.split(" / ")[0]);
                                        if (curEnergy < 1) break;
                                        oneOre.click();
                                        needClickCnt--;
                                    }
                                }
                                //Если текущее хп равно или меньше половины, копаем
                                else if (curOreHP <= (fullOreHP * allowHpOre)) {
                                    oneOre.click();
                                }
                            /*
                            * else if (sType == "jade")     sType = "GEM (jade)";
                            else if (sType == "lapis")    sType = "GEM (lapis)";
                            else if (sType == "sapphire") sType = "GEM (sapphire)";
                            else if (sType == "ruby")     sType = "GEM (ruby)";
                            else if (sType == "emerald")  sType = "GEM (emerald)";
                            * */
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
//                            driver.close();
                        }

                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                if (exit) break;


            }
        }




// #div.at-error.alert.alert-danger > div
//        WebElement fildSerch = driver.findElement(By.cssSelector("#d-flex flex-column mb-3"));
//
//
//        driver.manage().timeouts().implicitlyWait(60, SECONDS);
//
//        WebElement firstLink = driver.findElement(By.cssSelector("#rso > div.g > div > div > h3 > a"));
//        firstLink.click();

        driver.close();
    }

    private boolean isElementPresent(By by, WebDriver driver) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean isChildElementPresent(By by, WebElement element) {
        try {
            element.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    @After
    public void tearDown()  {
        driver.quit();
    }

}
