import org.openqa.selenium.WebElement;

public class Adventure {
    private WebElement adventure;
    private String timeType;
    private String stateType;
    private Integer priority;

    public Adventure(WebElement adventure, String timeType, String stateType, Integer priority)
    {
        this.adventure = adventure;
        this.timeType = timeType;
        this.stateType = stateType;
        this.priority = priority;

    }


    public Adventure(WebElement adventure, String timeType, String stateType)
    {
        this.adventure = adventure;
        this.timeType = timeType;
        this.stateType = stateType;
        this.priority = -1;

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

    public Integer getPriority() {
        return priority;
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

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
