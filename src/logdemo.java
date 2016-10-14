import org.apache.log4j.Logger;
public class logdemo {
	private static Logger log = Logger.getLogger(logdemo.class);
	public static void main(String[] args ) {
		log.info("Enter to main:");
		System.out.println("log demo running ");
		log.info("End of main");
	}

}
