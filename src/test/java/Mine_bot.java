import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.*;

import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.TestCase.assertTrue;

public class Mine_bot
{
    private static WebDriver driver;
    private WebElement oneOre; //Возможно вообще не нужно.
    private long timeout = 3; //таймаут ожидания в минутах
    private double allowHpOre = 0.666; //Допустимый процет ХП у руды, чтоб ее начать вскаповать.
    private boolean exit = false; //Вспомогательная переменная, чтоб можно было прервать бесконечный цыкл.
    private Integer floorNum = 7; //на какой этаж идем
    private Integer roomNum = 4; //в какую комнату
    private static String food = "banana"; //Что едим

    private ArrayList<String> tabs; //Возможно вообще не нужно.
    private String login = "Deventur";
    private String pass = "qazxcv";
    //Зерна которые хотим сажать в проядке их приоритета.
    private static ArrayList<String> seed =  new ArrayList<String>()
                                                                    {{
                                                                        add("basilSeed");
                                                                        add("endiveSeed");
                                                                        add("juniperSeed");
                                                                        add("pinkRoseSeed");
                                                                        add("letticeSeed");
                                                                        add("bananaSeed");
                                                                        add("lemonSeed");
                                                                        add("feverfewSeed");
                                                                        add("hydrangeaSeed");
                                                                        add("cardoonSeed");
                                                                        add("dragonfruitSeed");
                                                                        add("chilliSeed");
                                                                        add("bambooSeed");
                                                                        add("pineappleSeed");
                                                                    }};
    private static ArrayList<String> itemsForCraft =  new ArrayList<String>()
                                                                    {{
                                                                        add("carbon_spear");
                                                                        add("polished_titanium");
                                                                        add("polished_steel");
                                                                        add("polished_gold");
                                                                        //add("carbon_sculpture");
                                                                        add("polished_iron");
                                                                        add("polished_tin");
                                                                        add("copper_sculpture");
                                                                        add("iron_pylon");
                                                                        add("tin_pylon");
                                                                    }};

    @Before
    public void Login()
    {

        System.setProperty("webdriver.chrome.driver", "WebDrivers\\chromedriver.exe");

        driver = new ChromeDriver();
        String Path = "https://eternitytower.net/signin";
        //String Path = "https://eternitytower.net/mining";
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(3, SECONDS);
        driver.get(Path);

        if (isDisplayedElement(By.cssSelector("div.at-form")) || isDisplayedElement(By.cssSelector("div.at-error.alert.alert-danger > div")))
        {

            WebElement loginField = driver.findElement(By.id("at-field-username_and_email"));
            WebElement passField = driver.findElement(By.id("at-field-password"));

            loginField.sendKeys(this.login);
            passField.sendKeys(this.pass);

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
    public void Platation()
    {
        try {
            String Path = "https://eternitytower.net/farming";
            driver.get(Path);
            Date oldDate = new Date(); //старое время в миллисекундах
            Date newDate = oldDate;
            while (true) {
                sleep(20000);
                newDate = new Date();
                if ((newDate.getTime() - oldDate.getTime()) / (1000) >= (40)){
                    oldDate = newDate;
                    plantation(seed);
                }
                if (exit) break;
            }
            driver.close();
        }
        catch (Exception e) {e.printStackTrace();}
    }

    @Test
    public void Eating()
    {
        Date oldDate = new Date(); //старое время в миллисекундах
        Date newDate;
        while (true) {
            try {
                newDate = new Date();
                //тут надо задать время в которое он (бот) будет проверять жратву
                if ((newDate.getTime() - oldDate.getTime())/(1000)>= (20*60))
                {
                    //Проверяем на вкладке еквипмента ли мы
                    if(isDisplayedElement(By.cssSelector("li.nav-item.equipmentTabLink a.active")))
                    {
                        //Проверяем ХПшки
                        WebElement personHP = driver.findElement(By.xpath("//h1[text()[contains(.,'My')]]/../../div/div/div/div[contains(@class,'justify-content-center')]"));
                        int curPersonHP = Integer.parseInt(personHP.getText().split(" / ")[0].replace(".", "").replace("k", "00"));
                        int fullPersonHP = Integer.parseInt(personHP.getText().split(" / ")[1].replace(".", "").replace("k", "00"));
                        if (curPersonHP <= fullPersonHP*0.5) {
                            //Проверяем не жрем ли мы что-то, тут может быть баг с боевым бафом (который глобал), может надо убрать.
                            if (!isDisplayedElement(By.cssSelector("div.buff-icon-container"))) {
                                eat("lemon");//тут указать что жрать
                                oldDate = newDate;
                            }
                        }
                    }
                }
                if (exit)  break;
                sleep(20000);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void MiningAll()
    {

        Date oldDate = new Date(); //старое время в миллисекундах
        Date newDate;

        String Path = "https://eternitytower.net/mining";
        driver.get(Path);
        driver.manage().timeouts().implicitlyWait(7, SECONDS);

        if (isElementPresent(By.cssSelector("a.nav-link.equipmentLink"), driver))
        {

            double dmgPick = this.getDngPick();
            if(isElementPresent(By.cssSelector("img.minimize-icon"),driver))
            {
                driver.findElement(By.cssSelector("img.minimize-icon")).click();
            }
            while (true)
            {
                try
                {
                    WebElement mineTab = driver.findElement(By.cssSelector("a.nav-link.minePitLink"));
                    mineTab.click();
                    double curEnergy = miningOresAndGetCurEnergyPick(dmgPick);
                    newDate = new Date();

                    if (newDate.getTime() - oldDate.getTime()>= (this.timeout*60_000))
                    {
                        oldDate = newDate;
                        crafting(itemsForCraft);
                        driver.get("https://eternitytower.net/mining");
                    }
                    else if(curEnergy <= 1.0)
                    {
                        sleep(60_000);
                    }
                    //тут надо задать время в которое он (бот) будет проверять жратву

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
    public void PassiveBattle()
    {
        String Path = "https://eternitytower.net/combat";
        driver.get(Path);
        driver.manage().timeouts().implicitlyWait(30, SECONDS);

        try {
            if (isDisplayedElement(By.cssSelector("li.nav-item.abilitiesTabLink"))) {
                while (true) {
                    try
                    {
                        WebElement abilitiesTab = driver.findElement(By.cssSelector("li.nav-item.abilitiesTabLink"));
                        abilitiesTab.click();//мы во вкладке Башня
                        Map<String, Integer> resaltBattle = pasiveBattle();
                        if (resaltBattle!=null) {
                            if (resaltBattle.get("Health") <= 50)
                            {
                                if (!isDisplayedElement(By.cssSelector("div.buff-icon-container"))) {
                                    eat("lettice");
                                }
                            }
                            else if (resaltBattle.get("Energy") <= resaltBattle.get("FullEnergy") * 0.2)
                            {
                                if (!isDisplayedElement(By.cssSelector("div.buff-icon-container"))) {
                                    eat("lemon");
                                }
                            }
                        }
                        abilitiesTab.click();//мы во вкладке Башня
                        //sleep(70*1000); //ждем 70 секунд
                        if (exit) break;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
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


    @Test
    public void DemonsBattle()
    {
        String Path = "https://eternitytower.net/combat";
        driver.get(Path);
        driver.manage().timeouts().implicitlyWait(30, SECONDS);

        try {
            if (isDisplayedElement(By.cssSelector("li.nav-item.towerTabLink"))) {
                while (true) {
                    try
                    {
                        WebElement towerTab = driver.findElement(By.cssSelector("li.nav-item.towerTabLink"));
                        towerTab.click();//мы во вкладке Башня
                        enterToBattle(floorNum, roomNum);
                        Map<String, Integer> resaltBattle = demonsBattle();
                        if (resaltBattle!=null) {
                            if (resaltBattle.get("Health") <= 50)
                            {
                                if (!isDisplayedElement(By.cssSelector("div.buff-icon-container"))) {
                                    eat(food);
                                }
                            }
                            else if (resaltBattle.get("Energy") <= resaltBattle.get("FullEnergy") * 0.1)
                            {
                                if (!isDisplayedElement(By.cssSelector("div.buff-icon-container"))) {
                                    eat("lemon");
                                }
                            }
                        }
                        WebElement abilitiesTab = driver.findElement(By.cssSelector("li.nav-item.abilitiesTabLink"));
                        abilitiesTab.click();//мы во вкладке Башня
                        sleep(70*1000); //ждем 70 секунд
//                        sleep(21*60*1000);//Превращаю бота в выращивателя.
//                        plantation(seed);
//                        driver.get("https://eternitytower.net/combat");
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
                            if (resaltBattle.get("Health") <= resaltBattle.get("FullHealth") * 0.3)
                            {
                                if (!isDisplayedElement(By.cssSelector("div.buff-icon-container"))) {
                                    eat(food);
                                }

                                try {
                                    sleep(15000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            else if (resaltBattle.get("Energy") <= resaltBattle.get("FullEnergy") * 0.1)
                            {
                                if (!isDisplayedElement(By.cssSelector("div.buff-icon-container"))) {
                                    eat("lemon");
                                }

                                try {
                                    sleep(100_000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
//                        sleep(21*60*1000);//Превращаю бота в выращивателя.
//                        plantation(seed);
//                        driver.get("https://eternitytower.net/combat");
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

    private void adventuresBattle(ArrayList<AdventurePriority> adventurePriorities )
    {
        String Path = "https://eternitytower.net/combat";
        driver.get(Path);
        driver.manage().timeouts().implicitlyWait(30, SECONDS);
        if (isElementPresent(By.cssSelector("li.nav-item.adventuresTabLink"))) {
            WebElement eqTab = driver.findElement(By.cssSelector("li.nav-item.adventuresTabLink"));
            eqTab.click();//мы во вкладке Adventures

            //Получить текущие адвенчуры и расставляем им приортитеты:
            ArrayList<Adventure> activeAdventures = new ArrayList<>();
            List<WebElement> actAdventures = driver.findElements(By.xpath("//../button[contains(@class, 'cancel-adventure-btn')]/../.."));
            actAdventures.forEach(adv-> {
                String timeType = adv.getText().split("\n")[1];
                String stateType = adv.getText().split("\n")[2];
                Integer priority = adventurePriorities.indexOf(new AdventurePriority(timeType,stateType));
                activeAdventures.add(new Adventure(adv, timeType, stateType, priority));
            });

            //Фильтруем доступные адвенчуры:
            List<WebElement> sAdventures = driver.findElements(By.cssSelector("div.adventure-item-container.inactive-adventure"));

            ArrayList<Adventure> availableAdventures = new ArrayList<>();
            sAdventures.forEach(adv -> {
                String timeType = adv.getText().split("\n")[1];
                String stateType = adv.getText().split("\n")[2];
                Integer priority = adventurePriorities.indexOf(new AdventurePriority(timeType,stateType));
                availableAdventures.add(new Adventure(adv, timeType, stateType, priority));
            });


            /*Тут надо пройтись по кадому активному, и если его приоритет ниже,
            * чем есть доступный, то его отменяем, и запускаем приоритетный неактивный адвенчур.
            * Тот который остановили - заменяем тем, который запустили.
            * */
            System.out.println();
        }



    }

    private Double miningOresAndGetCurEnergyPick(Double dmgPick)
    {
        driver.manage().timeouts().implicitlyWait(500, MILLISECONDS);
        WebElement enMiming = driver.findElement(By.cssSelector("div.d-flex.flex-column.mb-3 > div.d-flex"));
        String en = enMiming.getText();
        double curEnergy = Double.parseDouble(en.split(" / ")[0]);
        //double fullEnergy = Double.parseDouble(en.split(" / ")[1]);

        if (curEnergy <= 1.0)
            return curEnergy;

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
                    if (curOreHP <= (dmgPick * 2) || curOreHP <= (fullOreHP * 0.05) || srcImg.contains("jade") || srcImg.contains("sapphire") || srcImg.contains("lapis") || srcImg.contains("ruby") || srcImg.contains("emerald") || srcImg.contains("gem")) {

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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        en = driver.findElement(By.cssSelector("div.d-flex.flex-column.mb-3 > div.d-flex")).getText();
        curEnergy = Double.parseDouble(en.split(" / ")[0]);

        return curEnergy;
    }

    private Double getDngPick()
    {
        try {
            WebElement eqTab = driver.findElement(By.cssSelector("a.nav-link.equipmentLink"));
            eqTab.click();
            sleep(2000);

            WebElement pick = driver.findElement(By.cssSelector("div.d-flex.flex-column.ml-3 > div:nth-child(1)"));
            double dmgPick = Double.parseDouble(pick.getText().substring(0, pick.getText().indexOf("\n")));
            //minePitLink
            WebElement mineTab = driver.findElement(By.cssSelector("a.nav-link.minePitLink"));
            mineTab.click();

            return dmgPick;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 50.0;
        }
    }

    private void crafting(ArrayList<String> itemsForCraft)
    {
        try {
            driver.manage().timeouts().implicitlyWait(15, SECONDS);
            driver.get("https://eternitytower.net/crafting");
//            driver.switchTo().window(tabs.get(1));
            if (isDisplayedElement(By.cssSelector("li.nav-item.crafting-filter"))){

                driver.findElement(By.cssSelector("li.nav-item.crafting-filter[data-filter='all']")).click();

                driver.manage().timeouts().implicitlyWait(1, SECONDS);

//                if(isDisplayedElement(By.cssSelector("div.my-3>div.d-flex.flex-row img"))) {
                    List<WebElement> craftingItems = driver.findElements(By.cssSelector("div.my-3>div.d-flex.flex-row img"));
                    //если сейчас крафтим больше 4 предметов, то выходим
                    if (craftingItems.size() > 4)
                        return;

                    Actions actions = new Actions(driver);
                    List<WebElement> recipes = driver.findElements(By.cssSelector("div.recipe-container"));
                    boolean craftingFlag = false;
                    for (String item : itemsForCraft) {
                        //List<WebElement> re = recipes.stream().filter(x->x.getAttribute("data-recipe")==item).collect(Collectors.toList());
                        for (WebElement re : recipes) {
                            if (re.getAttribute("data-recipe").contains(item)) {
                                if (isChildElementPresent(By.cssSelector("div div span.text-success"), re)) //проверка на то, что мы можем его сделать.
                                {
                                    WebElement element = re.findElement(By.cssSelector("div.quick-craft"));
                                    //Проучаем кол-во сколько можем скрафтить, и размер пачки
                                    Double itemCount = Double.parseDouble(re.findElement(By.cssSelector("div div span.text-success")).getText());
                                    if (itemCount <= 4.0) continue;
                                    Double dataAmount = Double.parseDouble(element.getAttribute("data-amount"));
                                    Double craftCount = itemCount / dataAmount;
                                    //Если их отношение меньше 1, то пропускаем!
                                    if (craftCount < 1.0)
                                        continue;
                                    else if (craftCount > (double) (5 - craftingItems.size()))
                                        craftCount = (double) (5 - craftingItems.size());
                                    else
                                        craftCount = Math.ceil(itemCount / dataAmount);

                                    for (int i = 0; i < craftCount+1; i++) {
                                        actions.moveToElement(element, 10, 10)
                                                .click()
                                                .build()
                                                .perform();
                                        sleep(300);
                                    }
                                    craftingFlag = true;
                                    break;
                                }
                            }
                        }
                        if (craftingFlag) break;

                    }
//                }

                //тут надо сделать пройтись по всему списку itemForCraft, и как только находим один из рецептов, кликаем по нему (div.quick-craft).
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private WebElement checkSeed(ArrayList<String> seedName) {
        List<WebElement> seeds = driver.findElements(By.cssSelector("div.item-icon-container.item.small"));
        if (seeds.size() > 0)
        {   //Пробегам по всем семенам, и ищем то, которое хотим сажать
            HashMap<String, WebElement> necessaryGrain = new HashMap();
            for (WebElement s : seeds)
            {
                WebElement img = s.findElement(By.cssSelector("img"));
                String curNameSeed = img.getAttribute("src");
                curNameSeed = curNameSeed.substring(curNameSeed.lastIndexOf("/")+1,curNameSeed.lastIndexOf(".svg"));
                //То, что хотим сажать живет в seedName
                if(seedName.contains(curNameSeed))
                {
                    //Запоминаем
                    //return s;
                    necessaryGrain.put(curNameSeed, s);
                }
            }
            //Перебираем все желаемые зерна по порядку
            for(String sn : seedName)
            {
                if (necessaryGrain.containsKey(sn))
                {
                    //как только находим то зерно, которое хотим сажать - возвращаем его WebElement.
                    return necessaryGrain.get(sn);
                }
            }
        }
        return null;
    }

    private void plantation(ArrayList<String> seed) {
        try {
            driver.manage().timeouts().implicitlyWait(15, SECONDS);
            if (!driver.getCurrentUrl().contains("farming")) {
                driver.get("https://eternitytower.net/farming");
            }
//            driver.switchTo().window(tabs.get(1));
            if (isDisplayedElement(By.cssSelector("li.nav-item.plotsLink"))) {
                driver.findElement(By.cssSelector("li.nav-item.plotsLink")).click();

                List<WebElement> beds = driver.findElements(By.cssSelector("div.farm-space-container.drop-target"));

                //Пробегам по всем грядкам
                for (WebElement curBed : beds) {
                    WebElement img = curBed.findElement(By.cssSelector("img"));
                    WebElement plantedSeed = checkSeed(seed);
                    //Если грядка не пустая, а ростение выросло
                    if (!img.getAttribute("src").contains("emptyFarmSpace") && !img.getAttribute("src").contains("sapling")) {
                        //То собираем его
                        curBed.click();
                        try {
                            sleep(1000);

                            //Если у нас есть семена, и грядка не занята, то сажаем на нее семечко
                            if ((plantedSeed != null && !Objects.equals(plantedSeed, null))) {
                                plantedSeed.click();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (!img.getAttribute("src").contains("sapling")) {
                        //Если у нас есть семена, и грядка не занята, то сажаем на нее семечко
                        if ((plantedSeed != null && !Objects.equals(plantedSeed, null))) {
                            plantedSeed.click();
                        }
                    }

                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void eat(String food) {
        try {
            if (isDisplayedElement(By.cssSelector("li.nav-item.equipmentTabLink"))) {
                WebElement equipmentTab = driver.findElement(By.cssSelector("li.nav-item.equipmentTabLink"));
                if(isDisplayedElement(By.cssSelector("li.nav-item.equipmentTabLink a.active")))
                {
                    //equipmentTab.click(); //Мы в меню снаряжения!
                    WebElement item = driver.findElement(By.xpath("//div/img[contains(@src, '" + food + ".svg')]"));
                    item.click();
                }
                else
                {
                    equipmentTab.click(); //Мы в меню снаряжения!
                    WebElement item = driver.findElement(By.xpath("//div/img[contains(@src, '" + food + ".svg')]"));
                    item.click();
                }
//                List<WebElement> items = driver.findElements(By.cssSelector("div.item-icon-container.item.small"));
//                for (WebElement item : items) {
//                    WebElement img = item.findElement(By.cssSelector("img"));
//                    if (img.getAttribute("src").contains(food)) {
//                        item.click();
//                        break;
//                    }
//                }
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

    private Map<String,Integer> demonsBattle(){


        if(driver.findElements(By.xpath("//div/div/h1[contains(text(),'Enemy Units')]/../../div[contains(@class,'d-flex')]/div")).size()>=1)
        {
            //div.ability-icon-container
            List<WebElement> abilitys = driver.findElements(By.cssSelector("div.ability-icon-container"));
            driver.manage().timeouts().implicitlyWait(300, MILLISECONDS);
            Integer attempts = 0;
            WebElement personHP = null;
            Integer curPersonHP = 1000;
            Integer fullPersonHP = 1000;
            while (!isDisplayedResaltBattle() && attempts < 10)
            {
                try {
                    personHP = driver.findElement(By.xpath("//h1[text()[contains(.,'My')]]/../../div/div/div/div[contains(@class,'justify-content-center')]"));
                    curPersonHP = Integer.parseInt(personHP.getText().split(" / ")[0].replace(".", "").replace("k", "00"));
                    fullPersonHP = Integer.parseInt(personHP.getText().split(" / ")[1].replace(".", "").replace("k", "00"));
                    if (curPersonHP <= fullPersonHP * 0.17) {
                        abilitys.get(2).click();
                        abilitys.get(3).click();
                        abilitys.get(4).click();
                        abilitys.get(5).click();
                        sleep(3000);
                    }
                    abilitys.get(1).click();
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

    private Map<String,Integer> pasiveBattle() {
        //Ждем начала биты.
        while(true) {
            //как только битва началась, стандартно отрабатываем ее до ее окончания.
            if (driver.findElements(By.xpath("//div/div/h1[contains(text(),'Enemy Units')]/../../div[contains(@class,'d-flex')]/div")).size() >= 1) {

                List<WebElement> abilitys = driver.findElements(By.cssSelector("div.ability-icon-container"));
                driver.manage().timeouts().implicitlyWait(300, MILLISECONDS);
                Integer attempts = 0;
                WebElement personHP = null;
                Integer curPersonHP = 1000;
                Integer fullPersonHP = 1000;
                while (!isDisplayedResaltBattle() && attempts < 10) {
                    try {
                        personHP = driver.findElement(By.xpath("//h1[text()[contains(.,'My')]]/../../div/div/div/div[contains(@class,'justify-content-center')]"));
                        curPersonHP = Integer.parseInt(personHP.getText().split(" / ")[0].replace(".", "").replace("k", "00"));
                        fullPersonHP = Integer.parseInt(personHP.getText().split(" / ")[1].replace(".", "").replace("k", "00"));
                        if (curPersonHP <= fullPersonHP * 0.17) {
                            abilitys.get(2).click();
                            abilitys.get(3).click();
                            abilitys.get(4).click();
                            abilitys.get(5).click();
                            sleep(3000);
                        }
                        abilitys.get(5).click();
                    } catch (Exception e) {
                        e.printStackTrace();
                        attempts++;
                    }
                }
                //как только битва закончена (вышли из внутреннего цикла), перестаем ждать битву
                break;
            }
            else if (exit) break; //чтоб можно было "законно" выйти из цыкла

        }

        try {
            //"подсчитываем потери" и выдем их наружу как результат.
            WebElement curEn = driver.findElement(By.cssSelector("div.d-flex.flex-column.my-3"));
            List<WebElement> curState = driver.findElements(By.cssSelector("div.d-flex.flex-column.m-3"));


            Integer curPersonHP = Integer.parseInt(curState.get(0).getText().split(" / ")[0].replace(".", "").replace("k", "00"));
            Integer fullPersonHP = Integer.parseInt(curState.get(0).getText().split(" / ")[1].replace(".", "").replace("k", "00"));
            Integer curPersonEnergy = Integer.parseInt(curEn.getText().split(" / ")[0]);
            Integer fullPersonEnergy = Integer.parseInt(curEn.getText().split(" / ")[1]);
            Map<String, Integer> curPersonState = new HashMap<>();
            curPersonState.put("Health", curPersonHP);
            curPersonState.put("FullHealth", fullPersonHP);
            curPersonState.put("Energy", curPersonEnergy);
            curPersonState.put("FullEnergy", fullPersonEnergy);
            driver.manage().timeouts().implicitlyWait(3, SECONDS);
            return curPersonState;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String,Integer> battle(){

        if(driver.findElements(By.xpath("//div/div/h1[contains(text(),'Enemy Units')]/../../div[contains(@class,'d-flex')]/div")).size()>=1)
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
            sleep(1000);
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
