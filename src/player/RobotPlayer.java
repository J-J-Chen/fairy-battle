package player;
import battlecode.common.*;
import java.lang.Math;
import java.util.ArrayList;

public strictfp class RobotPlayer {
    static RobotController rc;

    static Direction[] directions = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    static RobotType[] spawnedByMiner = {RobotType.REFINERY, RobotType.VAPORATOR, RobotType.DESIGN_SCHOOL,
            RobotType.FULFILLMENT_CENTER, RobotType.NET_GUN};

    static int turnCount = 0;
	static int initialRoundNum = 0;
	static int spawnCount = 0;

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

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        RobotPlayer.rc = rc;

        turnCount = 0;
		initialRoundNum = rc.getRoundNum();

		locSpawn = rc.getLocation();

		if(rc.getType() == RobotType.MINER) {
			RobotInfo[] robots = senseFriendlyRobots();
			for(RobotInfo robot : robots) {
				if(robot.type == RobotType.HQ) {
					locHQ = robot.location;
					System.out.println("GOT HQ LOC");
					break;
				}
			}
		}

		if(rc.getLocation().x > (int)(2*rc.getMapWidth()/3)) isRight = true;
		else if(rc.getLocation().x > (int)(rc.getMapWidth()/3)) isMidX = true;
		else isLeft = true;

		if(rc.getLocation().y > (int)(2*rc.getMapWidth()/3)) isTop = true;
		if(rc.getLocation().y > (int)(rc.getMapWidth()/3)) isMidY = true;
		else isBottom = true;

        //System.out.println("I'm a " + rc.getType() + " and I just got created!");
        while (true) {
            turnCount += 1;
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                //System.out.println("I'm a " + rc.getType() + "! Location " + rc.getLocation());
                switch (rc.getType()) {
                    case HQ:                 runHQ();                break;
                    case MINER:              runMiner();             break;
                    case REFINERY:           runRefinery();          break;
                    case VAPORATOR:          runVaporator();         break;
                    case DESIGN_SCHOOL:      runDesignSchool();      break;
                    case FULFILLMENT_CENTER: runFulfillmentCenter(); break;
                    case LANDSCAPER:         runLandscaper();        break;
                    case DELIVERY_DRONE:     runDeliveryDrone();     break;
                    case NET_GUN:            runNetGun();            break;
                }

            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            }
        }
    }

    static void runHQ() throws GameActionException {
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
		if((rc.getTeamSoup() >= (Constants.BASE_MIN_SOUP_TO_PRODUCE_MINER)) &&
			spawnCount <= Constants.INIT_NUM_MINERS_TO_PRODUCE) {
			while(!rc.isReady()) Clock.yield();
			//if(tryBuild(RobotType.MINER, rc.getLocation().directionTo(locHQGuess))) {
			//} else {
					for (Direction dir : directions) {
            			if(tryBuild(RobotType.MINER, dir)) {
							++spawnCount;
							break;
						}
					}
				//}
		}
		// If build miner, send message of HQ loc and path to take.
    }

    static void runMiner() throws GameActionException {
        //tryMove(randomDirection());
        // tryBuild(randomSpawnedByMiner(), randomDirection());
    	//for (Direction dir : directions)
            //tryBuild(RobotType.FULFILLMENT_CENTER, dir);
		//bugMove(new MapLocation(50, 5), true);
		if(rc.getTeamSoup() >= Constants.BASE_MIN_SOUP_TO_PRODUCE_FC) {
			for(Direction dir : directions) {
				if(tryBuild(RobotType.FULFILLMENT_CENTER, dir)) break;
			}
		}
		minerMine();
		/**while(rc.getRoundNum() < 3) {
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
		}
        for (Direction dir : directions)
            if (tryRefine(dir))
                System.out.println("I refined soup! " + rc.getTeamSoup());
        for (Direction dir : directions)
            if (tryMine(dir))
                System.out.println("I mined soup! " + rc.getSoupCarrying());
        if (tryMove(randomDirection()))
            System.out.println("I moved!");
			*/
    }

    static void runRefinery() throws GameActionException {
        // System.out.println("Pollution: " + rc.sensePollution(rc.getLocation()));
    }

    static void runVaporator() throws GameActionException {

    }

    static void runDesignSchool() throws GameActionException {
		//if(makeLandscapers) trybuild();
    }

    static void runFulfillmentCenter() throws GameActionException {
        for (Direction dir : directions)
            tryBuild(RobotType.DELIVERY_DRONE, dir);
    }

    static void runLandscaper() throws GameActionException {

    }

    static void runDeliveryDrone() throws GameActionException {
        Team enemy = rc.getTeam().opponent();
        if (!rc.isCurrentlyHoldingUnit()) {
            // See if there are any enemy robots within striking range (distance 1 from lumberjack's radius)
            RobotInfo[] robots = rc.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED, enemy);

            if (robots.length > 0) {
                // Pick up a first robot within range
                rc.pickUpUnit(robots[0].getID());
                System.out.println("I picked up " + robots[0].getID() + "!");
            } else {
				tryMove(randomDirection());
			}
        } else {
            // No close robots, so search for robots within sight radius
            tryMove(randomDirection());
        }
    }

    static void runNetGun() throws GameActionException {

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
     * Returns a random RobotType spawned by miners.
     *
     * @return a random RobotType
     */
    static RobotType randomSpawnedByMiner() {
        return spawnedByMiner[(int) (Math.random() * spawnedByMiner.length)];
    }

    static boolean tryMove() throws GameActionException {
        for (Direction dir : directions)
            if (tryMove(dir))
                return true;
        return false;
        // MapLocation loc = rc.getLocation();
        // if (loc.x < 10 && loc.x < loc.y)
        //     return tryMove(Direction.EAST);
        // else if (loc.x < 10)
        //     return tryMove(Direction.SOUTH);
        // else if (loc.x > loc.y)
        //     return tryMove(Direction.WEST);
        // else
        //     return tryMove(Direction.NORTH);
    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        // System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (rc.isReady() && rc.canMove(dir)) {
            rc.move(dir);
            return true;
        } else return false;
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

	/**
	  Sends a message given flags
	  */
	static void sendMessage(int flags) {
		boolean[] toSend = new boolean[Constants.NUM_MSG_FLAGS];
		for(int i = 0; i < Constants.NUM_MSG_FLAGS; ++i) {
			toSend[i] = ((flags & (int)Math.pow(2, i)) == Math.pow(2,i)) ? true : false;
		}
	}

	/**
	  Miner will take an adventure and explore and gather soup
	  */
	static void minerMine() throws GameActionException {
		//TODO: HQ sense soup, send init direction closest
		while(!rc.isReady()) Clock.yield();
		MapLocation[] soupLocs = rc.senseNearbySoup();
		if(soupLocs.length != 0) {
			MapLocation closestLoc = shortestLocation(soupLocs);
			bugMove(shortestLocation(soupLocs), true);
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
				if(tryMove(randomDirection())) break;
			}
		}
	}

	/**
	  Miner will find the closest refinery or HQ if there are no refineries in vision
	  */
	static void minerRefine() throws GameActionException {
		RobotInfo[] friendlyRobots = senseFriendlyRobots();
		ArrayList<MapLocation> refineries = new ArrayList<MapLocation>();
		for(RobotInfo robot : friendlyRobots) {
			if(robot.type == RobotType.REFINERY) refineries.add(robot.location);
		}
		if(refineries.isEmpty()) {
			bugMove(locHQ, true);
			while(!rc.isReady()) Clock.yield();
			tryRefine(rc.getLocation().directionTo(locHQ));
		} else {
			MapLocation closestRefinery = shortestLocation(refineries.toArray(new MapLocation[0]));
			bugMove(closestRefinery, true);
			while(!rc.isReady()) Clock.yield();
			tryRefine(rc.getLocation().directionTo(closestRefinery));
		}
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
	  The Miner will explore the terrain until it finds another robot or recieves message to stop exploring
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
			//System.out.printf("ROBOT: %d moved %s", rc.getID(), direction.toString());
			tryMove(direction);
		}

		return null;
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
		/**int xDiff = rc.getLocation().x - x;
		int yDiff = rc.getLocation().y - y;

		boolean mainAxis = ((Math.abs(xDiff) * (Math.pow(3,0.5) / 2)) > Math.abs(yDiff)) ? false : true;

		if(!mainAxis) {
			if(xDiff <= 0) {
				if(yDiff <= 0) return Direction.NORTHEAST;
				else return Direction.SOUTHEAST;
			} else {
				if(yDiff < 0) return Direction.NORTHWEST;
				else return Direction.SOUTHWEST;
			}
		} else {
			if((double)(Math.abs(xDiff)/(Math.abs(yDiff) == 0 ? 0.01 : Math.abs(yDiff))) >= 4.0) {
				if(xDiff < 0) return Direction.EAST;
				else return Direction.WEST;
			} else {
				if(yDiff < 0) return Direction.NORTH;
				else return Direction.SOUTH;
			}
		}*/
	}

	/**
	  Returns a random direction to the left or right of a given direction
	  */
	static Direction randomDirection(Direction dir) {
		boolean first = (Math.random() >= 0.5) ? true : false;
		//boolean first = false;
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
				// Is this okay?
				MapLocation loc = new MapLocation(rc.getLocation().x + j, rc.getLocation().y + i);
				if(rc.canSenseLocation(loc)) locs.add(loc);
			}
		}
		return locs.toArray(new MapLocation[0]);
	}

	static void readMessages() {
		//for(String message : messages) {

		//}
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

	private enum MSGType {
		MINING,
		ENEMY_HQ_POS,
		PATH_TO_MINNING_ARR, // First part start, len?, max
	}

	private enum MinerObjective {
		MINE,
		DELIVER,
		EXPLORE
	}

	private enum LandscaperObjective {
		DIG,
		DUMP,
		EXPLORE
	}

    static void tryBlockchain() throws GameActionException {
        if (turnCount < 3) {
            int[] message = new int[10];
            for (int i = 0; i < 10; i++) {
                message[i] = 123;
            }
            if (rc.canSubmitTransaction(message, 10))
                rc.submitTransaction(message, 10);
        }
        // System.out.println(rc.getRoundMessages(turnCount-1));
    }
}
