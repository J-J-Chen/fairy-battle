package player;
import battlecode.common.*;

public class HQ {
	private static RobotController rc;
	private static Movement mv;

	public HQ(RobotController rc, Movement mv) throws GameActionException {
		HQ.rc = rc;
		HQ.mv = mv;
		Direction[] directions = mv.getDirections();
		for(Direction dir : mv.directions)
			mv.tryBuild(RobotType.MINER, dir);
	}

	static void run() throws GameActionException {
		while(true) {
		// TODO: Create func for Min soup
		/**if(rc.getRoundNum() < 3) {
			if(rc.getMapHeight() != rc.getMapWidth()) {
				if(rc.getMapHeight() > rc.getMapWidth()) {
					locHQGuess[0] = rc.getLocation().x;
					locHQGuess[1] = rc.getMapHeight() - rc.getLocation().y;
				} else {
					locHQGuess[0] = rc.getMapWidth() - rc.getLocation().x;
					locHQGuess[1] = rc.getLocation().y;
				}
			} else {
				locHQGuess[0] = rc.getMapWidth() - rc.getLocation().x;
				locHQGuess[0] = rc.getMapWidth() - rc.getLocation().y;
			}
		}*/
		while(!rc.isReady()) Clock.yield();
		RobotInfo[] enemies = mv.senseEnemyRobots();
		for(RobotInfo robot : enemies) {
			if(robot.type == RobotType.DELIVERY_DRONE) {
				while(!rc.isReady()) Clock.yield();
				rc.shootUnit(robot.ID);
			}
		}

		while(!rc.isReady()) Clock.yield();

		/**MapLocation[] soupLocsHQ = rc.senseNearbySoup();
		if(soupLocs.length > 0) {
			MapLocation mostSoup = null;
			for(MapLocation loc : soupLocs) {
				if(rc.getSoup(loc) > greatestSoup - (spawnCount == 0 ? 0 : 100)) {
					mostSoup = loc;
					greatestSoup = rc.getSoup(loc);
					alreadyOcc = false;
				}
			}
		}*/

		if((rc.getTeamSoup() >= (Constants.BASE_MIN_SOUP_TO_PRODUCE_MINER)) &&
			mv.spawnCount < Constants.INIT_NUM_MINERS_TO_PRODUCE) {
			while(!rc.isReady()) Clock.yield();
			//if(tryBuild(RobotType.MINER, rc.getLocation().directionTo(locHQGuess))) {
			//} else {
					for (Direction dir : mv.directions) {
            			if(mv.tryBuild(RobotType.MINER, dir)) {
							++mv.spawnCount;
							System.out.printf("NUMBER OF SPAWN %d\n", mv.spawnCount);
							break;
						}
					}
				//}
		}
    }
	}
}
