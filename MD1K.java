import java.util.LinkedList;


public class MD1K extends MM1K {

	public MD1K(String name, LinkedList<Event> schedule, double lambda, double ts, int k) {
		super(name, schedule, lambda, ts, k);
	}
	
	/**
	 * return a fixed time for processing each request.
	 */
	@Override
	public double getTimeOfNextDeath() {
		return getTS();
	}
}
