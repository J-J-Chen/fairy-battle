package player;
import battlecode.common.*;
import java.util.ArrayList;

public class Miner {
	private static RobotController rc;
	private static Movement mv;

	public Miner(RobotController rc, Movement mv) throws GameActionException {
		Miner.rc = rc;
		Miner.mv = mv;
		RobotInfo[] robots = mv.senseFriendlyRobots();
		for(RobotInfo robot : robots) {
			if(robot.type == RobotType.HQ) {
				mv.locHQ = robot.location;
				System.out.println("GOT HQ LOC");
				break;
			}
		}
	}

	public static void run() throws GameActionException {
		while(true) {
		boolean toBuildDesign = false;
		for(int i = 0; i < GameConstants.INITIAL_COOLDOWN_TURNS; ++i)
			Clock.yield();
		while(!rc.isReady()) Clock.yield();

		if(rc.getTeamSoup() >= 150) {
			// Dear God Miner is horrible
			mv.bugMove(mv.locHQ.add(mv.locHQ.directionTo(mv.locSpawn)).
					add(mv.locHQ.directionTo(mv.locSpawn)), false);
			while(!rc.isReady()) Clock.yield();
			if(mv.tryBuild(RobotType.DESIGN_SCHOOL, mv.locHQ.directionTo(mv.locSpawn))) {
				//
			} else {
				for(Direction dir : mv.directions) {
					if(mv.tryBuild(RobotType.DESIGN_SCHOOL, dir)) break;
				}
			}
		}

		while(true) {
			while(!rc.isReady()) Clock.yield();
			if(false) {
			//if(rc.getTeamSoup() >= Constants.BASE_MIN_SOUP_TO_PRODUCE_FC) {
				for(Direction dir : mv.directions) {
					if(mv.tryBuild(RobotType.FULFILLMENT_CENTER, dir)) break;
				}
			}
			minerMine();
		}
		}
	}

	static void minerMine() throws GameActionException {
		//TODO: HQ sense soup, send init direction closest
		while(!rc.isReady()) Clock.yield();
		MapLocation[] soupLocs = rc.senseNearbySoup();
		if(soupLocs.length != 0) {
			MapLocation closestLoc = mv.shortestLocation(soupLocs);
			mv.bugMove(mv.shortestLocation(soupLocs), true);
			while(true) {
				while(!rc.isReady()) Clock.yield();
				if(rc.getSoupCarrying() >= 99) {
					minerRefine();
					return;
				}
				while(!rc.isReady()) Clock.yield();
				if(rc.senseSoup(closestLoc) == 0 ) return;
				if(!tryMine(rc.getLocation().directionTo(closestLoc)))
					System.out.println("BUG DONE GOOFED");
			}
		} else {
			//Continue finding soup
			while(!rc.isReady()) Clock.yield();
			while(true) {
				if(mv.tryMove(mv.randomDirection())) break;
			}
		}
	}


	/**
	  Miner will find the closest refinery or HQ if there are no refineries in vision
	  */
	static void minerRefine() throws GameActionException {
		RobotInfo[] friendlyRobots = mv.senseFriendlyRobots();
		ArrayList<MapLocation> refineries = new ArrayList<MapLocation>();
		for(RobotInfo robot : friendlyRobots) {
			if(robot.type == RobotType.REFINERY) refineries.add(robot.location);
		}
		if(refineries.isEmpty()) {
			mv.bugMove(mv.locHQ, true);
			while(!rc.isReady()) Clock.yield();
			tryRefine(rc.getLocation().directionTo(mv.locHQ));
		} else {
			MapLocation closestRefinery = mv.shortestLocation(refineries.toArray(new MapLocation[0]));
			mv.bugMove(closestRefinery, true);
			while(!rc.isReady()) Clock.yield();
			tryRefine(rc.getLocation().directionTo(closestRefinery));
		}
	}

	 static void minerEndGame() throws GameActionException {
		Direction dirToHQ = mv.locSpawn.directionTo(mv.locHQ);
		MapLocation toStay = mv.addInvert(mv.locSpawn, dirToHQ, 1);

		//TODO: Make more robust, in case it is impossible to move to
		mv.bugMove(toStay, false);
		// Look at other positions, furthest away from center builds first
	}

    /**
     * Attempts to mine soup in a given direction.
     *
     * @param dir The intended direction of mining
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canMineSoup(dir)) {
            rc.mineSoup(dir);
            return true;
        } else return false;
    }

	/**
     * Attempts to refine soup in a given direction.
     *
     * @param dir The intended direction of refining
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryRefine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canDepositSoup(dir)) {
            rc.depositSoup(dir, rc.getSoupCarrying());
            return true;
        } else return false;
    }
}
