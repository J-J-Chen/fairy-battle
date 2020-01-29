package player;
import battlecode.common.*;

public class NetGun {
	private static RobotController rc;
	private static Movement mv;

	public NetGun(RobotController rc, Movement mv) throws GameActionException {
		NetGun.rc = rc;
		NetGun.mv = mv;
	}

	public static void run() throws GameActionException{
		RobotInfo[] enemies = mv.senseEnemyRobots();
		for(RobotInfo robot : enemies) {
			if(robot.type == RobotType.DELIVERY_DRONE) {
				while(!rc.isReady()) Clock.yield();
				rc.shootUnit(robot.ID);
			}
		}
	}
}

