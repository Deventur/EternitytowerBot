import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.TestCase.assertTrue;

public class Mine_bot
{
    private static WebDriver driver;
    private WebElement oneOre; //Возможно вообще не нужно.
    private long timeout = 10; //таймаут ожидания отката
    private double allowHpOre = 0.75; //Допустимый процет ХП у руды, чтоб ее начать вскаповать.
    private boolean exit = false; //Вспомогательная переменная, чтоб можно было прервать бесконечный цыкл.
    private static final Integer floorNum = 6; //на какой этаж идем
    private static final Integer roomNum = 2; //на в какую комнату
    public static String food = "dragonfruit"; //Что едим
    public static String seed = "dragonfruitSeed"; //Что сажаем.
    private ArrayList<String> tabs; //Возможно вообще не нужно.

    @Before
    public void Login()
    {
        System.setProperty("webdriver.chrome.driver", "WebDrivers\\chromedriver.exe");

        driver = new ChromeDriver();
        //String Path = "https://eternitytower.net/signin";
        String Path = "https://eternitytower.net/mining";
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(3, SECONDS);
        driver.get(Path);

        if (isDisplayedElement(By.cssSelector("div.at-form")) || isDisplayedElement(By.cssSelector("div.at-error.alert.alert-danger > div")))
        {

            WebElement loginField = driver.findElement(By.id("at-field-username_and_email"));
            WebElement passField = driver.findElement(By.id("at-field-password"));

            loginField.sendKeys("Deventur");
            passField.sendKeys("qazxc");

            WebElement signBtn = driver.findElement(By.id("at-btn"));
            signBtn.click();
            driver.manage().timeouts().implicitlyWait(30, SECONDS);

        }

        if (isDisplayedElement(By.cssSelector("form.form-inline.my-2.my-lg-0 > div.dropdown.ml-2")))
        {
            System.out.println("Вход выполнен успешно!");
        }
        else
        {
            System.out.println("Выполнить вход НЕ удалось!");
            assertTrue("Выполнить вход НЕ удалось!",false);
        }
    }

    @Test
    public void MiningAll()
    {
        String Path = "https://eternitytower.net/mining";
        driver.get(Path);

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
                            sleep(this.timeout * 60000);
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
        driver.close();
    }

    @Test
    public void Battle()
    {
        String Path = "https://eternitytower.net/combat";
        driver.get(Path);
        driver.manage().timeouts().implicitlyWait(30, SECONDS);

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
                        driver.get("https://eternitytower.net/combat");
                        //driver.switchTo().window(tabs.get(0));
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

    private void plantation(WebDriver driver, String seed) {
        try {
            if (this.driver == null) this.driver = driver; //HACK!
            driver.get("https://eternitytower.net/farming");
//            driver.switchTo().window(tabs.get(1));
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


    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
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
