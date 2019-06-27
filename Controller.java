import java.util.Collections;
import java.util.LinkedList;


public class Controller {
	
	static Device CPU;
	static Device Disk;
	static Device Network;

	
	/**
	 * Simulation constants
	 */
	public static final double MONITOR_INTERVAL = 0.03;
	public static final int SIMULATION_TIME = 100;
	
	/**
	 * time elapsed time before starting to log
	 */
	public static final double LOGGING_START_TIME = 10;
	
	
	/**
	 * holds all the devices in the system
	 * in this case we have one
	 */
	public static LinkedList<Device> devices = new LinkedList<Device>();
	
	/**
	 * initialize the schedule with a birth and a monitor event
	 * @return a schedule with two events
	 */
	public static LinkedList<Event> initSchedule(){
		LinkedList<Event> schedule = new LinkedList<Event>();
		double[] CPUProbabilities={0.5,0.1,0.4};
		double[] DiskProbabilities={1,0.5,0.5};
		double[] NetworkProbabilities={1,1,0};
		
		Device CPU = new MMN("CPU", schedule, 40, 0.02, 2);
		Device CPUIOjob = new MMN("CPUIO",schedule,2,0.248,2);
		Device CPUcpujob = new MMN("CPUcpujob",schedule,38,0.008,2);
		Device Disk= new MM1("Disk",schedule,0,0.1);
		Device Network= new MM1("Network",schedule,0,0.025);
	
		/*CPU.SetOutputDevice( new Device[]{Disk,Network},CPUProbabilities,);
		Disk.SetOutputDevice(new Device[]{CPU,Network}, DiskProbabilities);
		Network.SetOutputDevice(new Device[]{CPU,Disk},NetworkProbabilities);*/
		CPU.initializeScehduleWithOneEvent();
		CPUIOjob.initializeScehduleWithOneEvent();
		CPUcpujob.initializeScehduleWithOneEvent();
		//Disk.initializeScehduleWithOneEvent();
		//Network.initializeScehduleWithOneEvent();
		devices.add(CPU);
		devices.add(Disk);
		devices.add(Network);
		devices.add(CPUIOjob);
		devices.add(CPUcpujob);
		schedule.add(new Event(MONITOR_INTERVAL, EventType.MONITOR));
		//schedule2.add(new Event(MONITOR_INTERVAL, EventType.MONITOR));
		//schedule3.add(new Event(MONITOR_INTERVAL, EventType.MONITOR));
		return schedule;
	}
	
	
	
	/**
	 * sorts the schedule according to time, and returns the earliest event.
	 * @param schedule
	 * @return the earliest event in the schedule
	 */
	public static Event getNextEvent(LinkedList<Event> schedule){
			Collections.sort(schedule);
			return schedule.getFirst();
	}
	
	
	public static void main(String[] args){
		LinkedList<Event> schedule = initSchedule();
		double[] CPUProbabilities={0.1,0.5,1};
		double[] DiskProbabilities={0.5,1,0};
		double[] NetworkProbabilities={1,0,0};
		
		double time = 0, maxTime = SIMULATION_TIME;
		while(time < maxTime){
			Event event = getNextEvent(schedule);
			time = event.getTime();
			event.function(schedule, time);
			
			if(event.getType()==EventType.DEATH)
			   {
			    Device type=event.getDevice();
			    if(type==CPU)
			    {
			     CPU.SetOutputDevice(new Device[]{Disk,Network},CPUProbabilities,time);
			    }
			    else if(type==Disk)
			    {
			     Disk.SetOutputDevice(new Device[]{CPU,Network},DiskProbabilities,time);
			    }
			    else if(type==Network){
			     Network.SetOutputDevice(new Device[]{CPU,Disk},NetworkProbabilities,time);
			    }
			   }
		}
		double slowdown = 0;
		for(Device device: devices){
			System.out.println(device.LAMBDA);
			//device.printStats();
			device.printStatsSystem();
			slowdown += device.getSlowdown();
		}
		slowdown= slowdown /3;
		System.out.println("************************************");
		System.out.println("************************************");
		System.out.println("The whole system slowdown for Q1 is:"+slowdown);
		}
	}
	
	
		

