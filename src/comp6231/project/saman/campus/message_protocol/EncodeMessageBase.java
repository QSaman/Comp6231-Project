/**
 * 
 */
package comp6231.project.saman.campus.message_protocol;

import comp6231.project.saman.campus.Campus;

/**
 * @author saman
 *
 */
public abstract class EncodeMessageBase {
	public int message_id;
	public String user_id;
	
	public EncodeMessageBase(int message_id, String user_id)
	{
		this.message_id = message_id;
		this.user_id = user_id;
	}
	
	public abstract DecodeMessageBase handleRequest(Campus campus);

	/**
	 * 
	 */
	public EncodeMessageBase() {
		// TODO Auto-generated constructor stub
	}

}
