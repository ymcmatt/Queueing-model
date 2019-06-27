

public abstract class Device {
	
	/**
	 * this class is extended by MM1
	 */
	String name;
	protected double LAMBDA;
	/**
	 * used for logging, for distinguishing between devices
	 * @param name
	 */
	public Device(String name){
		this.name = name;
	}
	public abstract void onDeath(double timestamp);
	public abstract void onBirth(Event ev, double timestamp);
	public abstract void onMonitor(double timestamp, double startTime);
	public abstract void initializeScehduleWithOneEvent();
	public abstract void printStats();
	public abstract double getSlowdown();
	public abstract void SetOutputDevice(Device[] devices, double[] probabilities, double time);
	/**
	 * @return the name of the device
	 */
	public String getName(){
		return name;
	}
	public void printStatsSystem() {
		
	}
	
	
	
}
