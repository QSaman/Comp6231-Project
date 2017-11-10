package comp6231.project.mostafa.core;

public enum CommandEnum {
	UNDEFINED(-1), CREATE(0), DELETE(1), BOOKROOM(2), GETAVTIMESLOT(3), CANCELBOOKING(4), CHANGERESERVATION(5), Quantity(5);
	
    private final int value;
    
    /**
     * 
     * @param value to set for the Item
     */
    private CommandEnum(int value) {
        this.value = value;
    }

    /**
     * 
     * @return item value
     */
    public int getValue() {
        return value;
    }
}
