package comp6231.project.mostafa.serverSide;

import core.Constants;

public class Timer implements Runnable{
	public static int timer = Constants.INITIAL_TIME;
	
	@Override
	public void run() {
		while(true){
			--timer;
			
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				Server.log(e.getMessage());
			}
			
			if(timer <= 0)
			{
				reset();
			}
		}
	}
	
	private void reset(){
		timer = Constants.INITIAL_TIME;
		Database.getInstance().getBookingCount().clear();
		Database.getInstance().getBookingIds().clear();
		Database.getInstance().reset();
		Server.log("Timer and booking numbers reseted");
	}
}
