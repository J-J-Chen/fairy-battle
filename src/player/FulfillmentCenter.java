package player;
import battlecode.common.*;

public class FulfillmentCenter {
	private static RobotController rc;
	private static Movement mv;

	public FulfillmentCenter(RobotController rc, Movement mv) throws GameActionException {
		FulfillmentCenter.rc = rc;
		FulfillmentCenter.mv = mv;
	}

	public void run() throws GameActionException {
 		for (Direction dir : mv.directions)
            mv.tryBuild(RobotType.DELIVERY_DRONE, dir);
	}
}
