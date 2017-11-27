import org.openqa.selenium.WebElement;

public class Arventure {
    private WebElement adventure;
    private String timeType;
    private String stateType;

    public Arventure(WebElement adventure, String timeType, String stateType)
    {
        this.adventure = adventure;
        this.timeType = timeType;
        this.stateType = stateType;

    }

    public WebElement getAdventure() {
        return adventure;
    }

    public String getStateType() {
        return stateType;
    }

    public String getTimeType() {
        return timeType;
    }

    public void setAdventure(WebElement adventure) {
        this.adventure = adventure;
    }

    public void setStateType(String stateType) {
        this.stateType = stateType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }
}
