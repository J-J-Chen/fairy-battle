package player;
import battlecode.common.*;
import java.util.ArrayList;

public class Movement {
    static int turnCount = 0;
	static int initialRoundNum = 0;
	static int spawnCount = 0;
	static int greatestSoup = 0;

	static int[][] messages = {{-1}, {-1}};

	static int[] minerPath = {-1};

	static int[] landscaperPath = {-1};

	static MapLocation locHQ = null;
	static MapLocation locHQGuess = null;
	static MapLocation locSpawn = null;

	static boolean isBottom = false;
	static boolean isTop = false;
	static boolean isMidY = false;
	static boolean isRight = false;
	static boolean isLeft = false;
	static boolean isMidX = false;

	private static RobotController rc;

	static Direction[] directions = {Direction.NORTH, Direction.EAST,
			Direction.SOUTH, Direction.WEST};
    static RobotType[] spawnedByMiner = {RobotType.REFINERY,
			RobotType.VAPORATOR, RobotType.DESIGN_SCHOOL,
            RobotType.FULFILLMENT_CENTER, RobotType.NET_GUN};

	public Movement(RobotController rc) {
		Movement.rc = rc;
	}

	public static Direction[] getDirections() {
		return directions;
	}

	/**
	  Implements bug move When stucks, chooses a direction and moves in that direction,
	  will continue to move in that direction if it cannot move towards goal
	  @param closeEnough True if can finish at adjacent tile, false for perfect location
	  */
	static boolean bugMove(MapLocation location, boolean closeEnough) throws GameActionException {
		int x = location.x;
		int y = location.y;
		while(!rc.isReady()) Clock.yield();
		Direction direction = directionToLoc(x, y);

		while((rc.getLocation().x != x) || (rc.getLocation().y != y)) {
			while(!rc.isReady()) Clock.yield();

			direction = canPhysicallyMove(directionToLoc(x,y)) ? directionToLoc(x,y) : direction;
			if(closeEnough && rc.getLocation().isAdjacentTo(new MapLocation(x, y)) &&
					!canPhysicallyMove(directionToLoc(x,y))) return true;

			System.out.printf("Trying to move in direction %s\n", direction.toString());

			// If can't move in direction or if it is flooded, choose a different direction
			while(!canPhysicallyMove(direction) || rc.senseFlooding(rc.getLocation().add(direction)))
				direction = randomDirection(direction);
			if(tryMove(direction))
				Clock.yield();
		}
		return true;
	}

	/**
	  Same as canMove except it doesn't check to see if the robot is ready()
	  */
	static boolean canPhysicallyMove(Direction dir) throws GameActionException {
		MapLocation target  = rc.getLocation().add(dir);
		boolean isOnMap = rc.onTheMap(target);
		boolean isOccuppied = rc.senseRobotAtLocation(target) != null;
		boolean dirtDiff = (Math.abs(rc.senseElevation(rc.getLocation()) - rc.senseElevation(target)) <=
				GameConstants.MAX_DIRT_DIFFERENCE) ? true : false;
		return (isOnMap && !isOccuppied && (dirtDiff || rc.getType() == RobotType.DELIVERY_DRONE));
	}

	/**
	  Finds the direction closest to the given location from current location
	  */
	static Direction directionToLoc(int x, int y) {
		return rc.getLocation().directionTo(new MapLocation(x,y));
	}

	/**
	  Returns a random direction to the left or right of a given direction
	  */
	static Direction randomDirection(Direction dir) {
		boolean first = (Math.random() >= 0.5) ? true : false;
		switch(dir) {
			case NORTH: return (first ? Direction.NORTHWEST : Direction.NORTHEAST);
			case NORTHEAST: return (first ? Direction.NORTH : Direction.EAST);
			case EAST: return (first ? Direction.NORTHEAST : Direction.SOUTHEAST);
			case SOUTHEAST: return (first ? Direction.EAST : Direction.SOUTH);
			case SOUTH: return (first ? Direction.SOUTHWEST : Direction.SOUTHEAST);
			case SOUTHWEST: return (first ? Direction.WEST : Direction.SOUTH);
			case WEST: return (first ? Direction.NORTHWEST : Direction.SOUTHWEST);
			case NORTHWEST: return (first ? Direction.WEST : Direction.NORTH);
			default: return Direction.NORTH;
		}
	}

	/**
	  Returns an ArrayList of squares visible to the robot
	  */
	static MapLocation[] getVisibleLocs() {
		int visionRadius = rc.getCurrentSensorRadiusSquared();
		int radius = (int) Math.pow(visionRadius, 0.5);
		ArrayList<MapLocation> locs = new ArrayList<MapLocation>();
		for(int i = -radius; i <= radius; ++i) {
			for(int j = -radius; j <= radius; ++j) {
				// Is Movement okay?
				MapLocation loc = new MapLocation(rc.getLocation().x + j, rc.getLocation().y + i);
				if(rc.canSenseLocation(loc)) locs.add(loc);
			}
		}
		return locs.toArray(new MapLocation[0]);
	}

	/**
	  Finds the location closest to the current location given an array of locs
	  Need to implement a better path finding alg for better results
	  */
	static MapLocation shortestLocation(MapLocation[] locs) {
		int shortestPath = 10000;
		MapLocation shortestLoc = locs[0];
		for(MapLocation loc : locs) {
			//TODO: Find taxi cab shortest path
			int length = Math.abs(rc.getLocation().x - loc.x) + Math.abs(rc.getLocation().y - loc.y);
			if(length < shortestPath) {
				shortestPath = length;
				shortestLoc = loc;
			}
		}
		return shortestLoc;
	}

		/**
	  The Miner will explore the terrain until a certain number of turns to stop exploring
	  or number of turns elapses
	  */
	//TODO: Create advanced explore that looks for friendly robots and ID's and then updates path accordingly
	//TODO: Implement message interrupts
	static RobotType explore(int numTurns, boolean mine) throws GameActionException {
		boolean isMiner = (rc.getType() == RobotType.MINER);
		double xFrac = Math.min(Math.abs(locSpawn.x - rc.getMapWidth()), Math.abs(locSpawn.x));
		double yFrac = Math.min(Math.abs(locSpawn.y - rc.getMapHeight()), Math.abs(locSpawn.y));

		//TODO: Create fancy func to determine init direction
		Direction direction = randomDirection();
		for(int numTurnsElapsed = 0; numTurnsElapsed < numTurns; ++numTurnsElapsed) {
			if(senseEnemyRobots() != null) break;
			while(!rc.isReady()) Clock.yield();
			while(!rc.canMove(direction)) {
				// Will spin if robot stuck. This is (probably) okay behavior, should move when possible
				// Also is kind of inefficient. Oh well.
				direction = randomDirection(direction);
			}
			tryMove(direction);
		}

		return null;
	}

	static RobotInfo[] senseEnemyRobots() {
		return rc.senseNearbyRobots(rc.getCurrentSensorRadiusSquared(),
				(rc.getTeam() == Team.A ? Team.B : Team.A));
	}

	static RobotInfo[] senseFriendlyRobots() {
		ArrayList<RobotInfo> friendlyRobots = new ArrayList<RobotInfo>();
		RobotInfo[] allRobots = rc.senseNearbyRobots();
		//System.out.printf("NUMBER OF BOTS %d\n", allRobots.length);
		for(RobotInfo robot : allRobots) {
			if(robot.team == rc.getTeam()) friendlyRobots.add(robot);
		}
		return friendlyRobots.toArray(new RobotInfo[0]);
	}

	static boolean tryMove() throws GameActionException {
        for (Direction dir : directions)
            if (tryMove(dir))
                return true;
        return false;
    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canMove(dir)) {
            rc.move(dir);
            return true;
        } else return false;
    }

	/**
     * Returns a random Direction.
     *
     * @return a random Direction
     */
    static Direction randomDirection() {
        return directions[(int) (Math.random() * directions.length)];
    }

    /**
     * Attempts to build a given robot in a given direction.
     *
     * @param type The type of the robot to build
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryBuild(RobotType type, Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        } else return false;
    }

	/**
	  Returns a new location that is in the opposite direction numTimes
	  */
	static MapLocation addInvert(MapLocation initLoc, Direction dir, int numTimes) {
		MapLocation finalLoc = initLoc;
		for(int i = 0; i < numTimes; ++i) {
			switch(dir) {
				case EAST: finalLoc.add(Direction.WEST); break;
				case WEST: finalLoc.add(Direction.EAST); break;
				case NORTH: finalLoc.add(Direction.SOUTH); break;
				case SOUTH: finalLoc.add(Direction.NORTH); break;
				case NORTHEAST: finalLoc.add(Direction.SOUTHWEST); break;
				case NORTHWEST: finalLoc.add(Direction.SOUTHEAST); break;
				case SOUTHEAST: finalLoc.add(Direction.NORTHWEST); break;
				case SOUTHWEST: finalLoc.add(Direction.NORTHEAST); break;
				default: break;
			}
		}
		return finalLoc;
	}
}
