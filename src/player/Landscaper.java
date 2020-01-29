package player;
import battlecode.common.*;
import java.util.ArrayList;

public class Landscaper {
	private static RobotController rc;
	private static Movement mv;

	private int numLandscapers;

	public Landscaper(RobotController rc, Movement mv) throws GameActionException {
		Landscaper.rc = rc;
		Landscaper.mv = mv;
		RobotInfo[] robots = mv.senseFriendlyRobots();
		for(RobotInfo robot : robots) {
			if(robot.type == RobotType.HQ) {
				mv.locHQ = robot.location;
			} else if(robot.type == RobotType.LANDSCAPER) {
				++numLandscapers;
			}
		}
	}

	public static void run() throws GameActionException {
		if(mv.locHQ == null) {
			System.out.println("COULDN'T FIND HQ");
			while(true) Clock.yield();
		} else {
			mv.bugMove(mv.locHQ, true);
		}

		while(true) {
			while(!rc.isReady()) Clock.yield();

			//Check if landscaper has another landscaper next to them, then dig
			if(rc.getDirtCarrying() > 0) {
				//if(distance to mv.
				//Direction dir = rc.getLocation().directionTo(
			}
		}
	}

	public static RobotInfo[] getFriendlyLandscapers() {
		RobotInfo[] robots = mv.senseFriendlyRobots();
		ArrayList<RobotInfo> landscapers = new ArrayList<RobotInfo>();
		for(RobotInfo robot : robots) {
			if(robot.type == RobotType.LANDSCAPER) {
				landscapers.add(robot);
			}
		}
		return landscapers.toArray(new RobotInfo[0]);
	}
}
