/**
 * 
 */
package comp6231.project.saman.campus;

/**
 * @author saman
 *
 */
public class CampusContainer {
	private Campus[] campus_list;
	private int current_index;
	private int sequence_number;

	/**
	 * 
	 */
	public CampusContainer(Campus main, Campus backup) {
		campus_list = new Campus[2];
		campus_list[0] = main;
		main.setCampusContainer(this);
		campus_list[1] = backup;
		backup.setCampusContainer(this);
		current_index = 0;
		sequence_number = 0;
	}
	
	
	public void swtich()
	{
		current_index = (current_index + 1) % 2;
		startActiveCampus();
		
	}
	
	public void startActiveCampus()
	{
		campus_list[current_index].starServer();
	}
	
	public void nextSequenceNumber()
	{
		++sequence_number;
	}

	/**
	 * @return the sequence_number
	 */
	public int getSequenceNumber() {
		return sequence_number;
	}
	
}
