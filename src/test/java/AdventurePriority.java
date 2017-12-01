public class AdventurePriority
{
    private String timeType;
    private String stateType;

    AdventurePriority(String timeType, String stateType)
    {
        this.timeType = timeType;
        this.stateType = stateType;
    }

    public String getTimeType() {
        return timeType;
    }

    public String getStateType() {
        return stateType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }

    public void setStateType(String stateType) {
        this.stateType = stateType;
    }

    @Override
    public boolean equals(Object obj) {
        AdventurePriority adventurePriority = (AdventurePriority) obj;
        if (this.timeType.equals(adventurePriority.getTimeType()) && this.stateType.equals(adventurePriority.getStateType()))
            return true;
        else return false;
    }

    public boolean equals(AdventurePriority obj) {
        if (this.timeType.equals(obj.getTimeType()) && this.stateType.equals(obj.getStateType()))
            return true;
        else return false;
    }
}
