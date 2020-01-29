package player;
import battlecode.common.*;

public class Drone {
	private static RobotController rc;
	private static Movement mv;

	public Drone(RobotController rc, Movement mv) throws GameActionException {
		Drone.rc = rc;
		Drone.mv = mv;
	}

	public static void run() throws GameActionException {
 		Team enemy = rc.getTeam().opponent();
        if (!rc.isCurrentlyHoldingUnit()) {
            RobotInfo[] robots = rc.senseNearbyRobots(
					GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED, enemy);

            if (robots.length > 0) {
				for(RobotInfo enemies : robots) {
					if(enemies.type != RobotType.DELIVERY_DRONE)
                		rc.pickUpUnit(robots[0].getID());
				}
                System.out.println("I picked up " + robots[0].getID() + "!");
            } else {
				mv.tryMove(mv.randomDirection());
			}
        } else {
            // No close robots, so search for robots within sight radius
            mv.tryMove(mv.randomDirection());
        }
	}
}
