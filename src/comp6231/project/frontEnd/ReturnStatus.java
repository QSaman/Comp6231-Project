package comp6231.project.frontEnd;

public enum ReturnStatus {
    Ok(0), Timeout(1), ErrorInMessageType(2), FakeGenertor(3), ErrorSetTimeOut(4), CantLogin(5);
    private int value; // the value of enum state

    /**
     * the constructor of state
     * @param value the value of enum state
     */
    private ReturnStatus(int value) {
        this.value = value;
    }

    /**
     * State class override toString method.
     * @return the string
     */
    public String toString() {
        switch (this.value){
            case 0 : return "Ok";
            case 1 : return "Timeout";
            case 2 : return "ErrorInMessageType";
            case 3 : return "FakeGenertor";
            case 4 : return "setTimeOutError";
            case 5:  return "CantLogin";
        }
        return "";
    }
}
