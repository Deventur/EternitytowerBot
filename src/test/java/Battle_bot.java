import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.*;

import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class Battle_bot {

    private static final Integer floorNum = 6;
    private static final Integer roomNum = 2;
    public static WebDriver driver;
    private static boolean exit = false;
    public static String food = "dragonfruit";
    public static String seed = "dragonfruitSeed";
    private ArrayList<String> tabs;
    @Test
    public void Battle()
    {
        System.setProperty("webdriver.chrome.driver", "WebDrivers\\chromedriver.exe");
        //System.setProperty("webdriver.ie.driver", "WebDrivers\\IEDriverServer.exe");

        driver = new ChromeDriver();
        //WebDriver driver = new InternetExplorerDriver();
        String Path = "https://eternitytower.net/combat";
        driver.manage().window().maximize();
        driver.get(Path);
        driver.manage().timeouts().implicitlyWait(30, SECONDS);

        if (isElementPresent(By.cssSelector("div.at-error.alert.alert-danger > div"), driver))
        {
            driver.manage().timeouts().implicitlyWait(3, SECONDS);
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

        ((JavascriptExecutor)driver).executeScript("window.open()");
        tabs = new ArrayList<String> (driver.getWindowHandles());
        driver.switchTo().window(tabs.get(1)); //switches to new tab
        driver.get("https://eternitytower.net/farming");
        driver.switchTo().window(tabs.get(0)); // switch back to main screen

        try {
            if (isElementPresent(By.cssSelector("li.nav-item.towerTabLink"), driver)) {
                while (true) {
                    try
                    {
                        WebElement eqTab = driver.findElement(By.cssSelector("li.nav-item.towerTabLink"));
                        eqTab.click();//мы во вкладке Башня
                        enterToBattle(floorNum, roomNum);
                        Map<String, Integer> resaltBattle = battle();
                        if (resaltBattle!=null) {
                            if (resaltBattle.get("Health") <= resaltBattle.get("FullHealth") / 2
                                    || resaltBattle.get("Energy") <= resaltBattle.get("FullEnergy") / 2) {
                                if (!isDisplayedElement(By.cssSelector("div.buff-icon-container"))) {
                                    eat();
                                }

                                try {
                                    sleep(15000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
//                        sleep(21*60*1000);//Превращаю бота в выращивателя.
                        plantation(driver, seed);
                        driver.switchTo().window(tabs.get(0));
                        if (exit) break;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        if (!isElementPresent(By.cssSelector("li.nav-item.towerTabLink"))){
                            sleep(20000);
                        }
                    }
                }
//            System.out.println(resaltBattle.get("Health"));


                //TODO после повторяем все заново.
                //TODO Добавить функционал поедания еды, если HP или EN ниже порога
                /**Так же надо добавить функционал посадки новой еды, которую надо проверять после каждой битвы, если выросла - собираем и сажаем заново, если нет - то просто идем в следующую битву.
                 *Если нет еды, то ждем 10 минут, и только после этого идем в битву.
                 * В идеале поход в битву надо вынести в отдельный метод, как и посадку/сбор растний
                 * div.d-flex.battle-unit-container.flex-column.align-items-center.flex-wrap.justify-content-center.px-2
                 */

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        driver.close();
    }

    private WebElement checkSeed(String seedName) {
        List<WebElement> seeds = driver.findElements(By.cssSelector("div.item-icon-container.item.small"));
        if (seeds.size() > 0)
        {   //Пробегам по всем семенам, и ищем то, которое хотим сажать
            for (WebElement s : seeds)
            {
                WebElement img = s.findElement(By.cssSelector("img"));
                //То, что хотим сажать живет в seedName
                if(img.getAttribute("src").contains(seedName))
                {
                    //Запоминаем
                    return s;
                }
            }
        }
        return null;
    }

    public void plantation(WebDriver driver, String seed) {
        try {
            if (this.driver == null) this.driver = driver; //HACK!
            driver.switchTo().window(tabs.get(1));
            if (isDisplayedElement(By.cssSelector("li.nav-item.plotsLink"))) {
                driver.findElement(By.cssSelector("li.nav-item.plotsLink")).click();

                List<WebElement> beds = driver.findElements(By.cssSelector("div.farm-space-container.drop-target"));

                //Пробегам по всем грядкам
                for (WebElement curBed : beds) {
                    WebElement img = curBed.findElement(By.cssSelector("img"));
                    //Если грядка не пустая, а ростение выросло
                    if (!img.getAttribute("src").contains("emptyFarmSpace") && !img.getAttribute("src").contains("sapling")) {
                        //То собираем его
                        curBed.click();
                        try {
                            sleep(1000);
                            //Если у нас есть семена, и грядка не занята, то сажаем на нее семечко
                            if ((checkSeed(seed) != null && !Objects.equals(checkSeed(this.seed), null))) {
                                checkSeed(seed).click();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (!img.getAttribute("src").contains("sapling")) {
                        //Если у нас есть семена, и грядка не занята, то сажаем на нее семечко
                        if ((checkSeed(this.seed) != null && !Objects.equals(checkSeed(this.seed), null))) {
                            checkSeed(this.seed).click();
                        }
                    }

                }
            }
            driver.switchTo().window(tabs.get(0));
            //driver.get("https://eternitytower.net/combat");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void eat() {
        try {
            if (isDisplayedElement(By.cssSelector("li.nav-item.equipmentTabLink"))) {
                driver.findElement(By.cssSelector("li.nav-item.equipmentTabLink")).click(); //Мы в меню снаряжения!
                List<WebElement> items = driver.findElements(By.cssSelector("div.item-icon-container.item.small"));
                for (WebElement item : items) {
                    WebElement img = item.findElement(By.cssSelector("img"));
                    if (img.getAttribute("src").contains(food)) {
                        item.click();
                        break;
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private boolean isDisplayedElement(By by)
    {
        if (isElementPresent(by) && driver.findElement(by).isDisplayed())
        {
            return true;
        }
        return false;
    }

    private Map<String,Integer> battle(){

        //TODO Надо исправить!
        if(driver.findElements(By.cssSelector("div.d-flex.battle-unit-container.flex-column.align-items-center.flex-wrap.justify-content-center.px-2")).size()>=2)
        {
            //div.ability-icon-container
            List<WebElement> abilitys = driver.findElements(By.cssSelector("div.ability-icon-container"));
            driver.manage().timeouts().implicitlyWait(300, MILLISECONDS);
            Integer attempts = 0;
            while (!isDisplayedResaltBattle() && attempts < 10)
            {
                try {
                    abilitys.get(2).click();
                    abilitys.get(3).click();
                    abilitys.get(4).click();
                    sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                    attempts++;
                }
            }

            //strong.text-success
            //strong.text-danger
        }
        try
        {
            WebElement curEn = driver.findElement(By.cssSelector("div.d-flex.flex-column.my-3"));
            List<WebElement> curState = driver.findElements(By.cssSelector("div.d-flex.flex-column.m-3"));


            Integer curPersonHP = Integer.parseInt(curState.get(0).getText().split(" / ")[0].replace(".", "").replace("k", "00"));
            Integer fullPersonHP = Integer.parseInt(curState.get(0).getText().split(" / ")[1].replace(".", "").replace("k", "00"));
            Integer curPersonEnergy = Integer.parseInt(curEn.getText().split(" / ")[0]);
            Integer fullPersonEnergy = Integer.parseInt(curEn.getText().split(" / ")[1]);
            Map<String,Integer> curPersonState = new HashMap<>();
            curPersonState.put("Health", curPersonHP);
            curPersonState.put("FullHealth", fullPersonHP);
            curPersonState.put("Energy", curPersonEnergy);
            curPersonState.put("FullEnergy", fullPersonEnergy);
            driver.manage().timeouts().implicitlyWait(3, SECONDS);
            return curPersonState;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private void enterToBattle(int floor, int room){
        try
        {
            RemoteWebElement floorBtn = (RemoteWebElement) driver.findElement(By.cssSelector("div.dropdown.mx-3"));
            if (!floorBtn.findElement(By.cssSelector("button.btn")).getText().contains(floorNum.toString())) {
                floorBtn.click();

                if (isDisplayedElement(By.cssSelector("a.dropdown-item.select-floor"))) {
                    List<WebElement> floors = driver.findElements(By.cssSelector("a.dropdown-item.select-floor"));
                    floors.get(floor - 1).click();
                    sleep(500);//Мы на нужном этаже
                }
            }
            //Получаем все комнаты (в виде таблицы)
            List<WebElement> tableRooms = driver.findElement(By.cssSelector("table.table.table-responsive"))
                    .findElement(By.cssSelector("tbody"))
                    .findElements(By.cssSelector("tr"));
            tableRooms.get(room - 1).click();
            sleep(3500);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private boolean isDisplayedResaltBattle(){
        if (isElementPresent(By.cssSelector("strong.text-success"))){
            return driver.findElement(By.cssSelector("strong.text-success")).isDisplayed();
        }
        else if(isElementPresent(By.cssSelector("strong.text-danger"))){
            return driver.findElement(By.cssSelector("strong.text-danger")).isDisplayed();
        }
        else{
            return false;
        }

    }

    private boolean isElementPresent(By by, WebDriver driver) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean isChildElementPresent(By by, RemoteWebElement element) {
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
