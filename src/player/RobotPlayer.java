package player;
import battlecode.common.*;
import java.lang.Math;

public strictfp class RobotPlayer {
    static RobotController rc;

    static Direction[] directions = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    static RobotType[] spawnedByMiner = {RobotType.REFINERY, RobotType.VAPORATOR, RobotType.DESIGN_SCHOOL,
            RobotType.FULFILLMENT_CENTER, RobotType.NET_GUN};

    static int turnCount = 0;
	static int initialRoundNum = 0;

	static int[][] messages = {{-1}, {-1}};

	static int[] minerPath = {-1};

	static int[] landscaperPath = {-1};

	static int[] locHQ = {-1, -1};  // 0 -> x value, 1 -> y value
	static int[] locHQGuess = {-1, -1};

	static int[] locSpawn = {-1, -1};

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

		locSpawn = new int[2];
		locSpawn[0] = rc.getLocation().x;
		locSpawn[1] = rc.getLocation().y;

		if(rc.getLocation().x > (int)(2*rc.getMapWidth()/3)) isRight = true;
		else if(rc.getLocation().x > (int)(rc.getMapWidth()/3)) isMidX = true;
		else isLeft = true;

		if(rc.getLocation().y > (int)(2*rc.getMapWidth()/3)) isTop = true;
		if(rc.getLocation().y > (int)(rc.getMapWidth()/3)) isMidY = true;
		else isBottom = true;

        System.out.println("I'm a " + rc.getType() + " and I just got created!");
        while (true) {
            turnCount += 1;
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                // Here, we've separated the controls into a different method for each RobotType.
                // You can add the missing ones or rewrite this into your own control structure.
                System.out.println("I'm a " + rc.getType() + "! Location " + rc.getLocation());
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

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

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
		if(rc.getTeamSoup() >= (Constants.BASE_MIN_SOUP_TO_PRODUCE_MINER)) {
			if(tryBuild(RobotType.MINER, directionToLoc(locHQGuess[0], locHQGuess[1]))) {
			} else {
					for (Direction dir : directions)
            			if(tryBuild(RobotType.MINER, dir)) break;
				}
		}
		// If build miner, send message of HQ loc and path to take.
    }

    static void runMiner() throws GameActionException {
        //tryMove(randomDirection());
        // tryBuild(randomSpawnedByMiner(), randomDirection());
    	//for (Direction dir : directions)
            //tryBuild(RobotType.FULFILLMENT_CENTER, dir);
		bugMove(50,5);
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
			//Clock.yield();
		}
		System.out.printf("MADE IT, %d, %d", locHQGuess[0], locHQGuess[1]);
		bugMove(locHQGuess[0], locHQGuess[1]);
		//bugMove(37, 37);
		//TODO: MAKE SURE TO NOT REFINE OPPONENT'S
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
	  The Miner will explore the terrain until it finds another robot or recieves message to stop exploring
	  or number of turns elapses
	  */
	//TODO: Create advanced explore that looks for friendly robots and ID's and then updates path accordingly
	//TODO: Implement message interrupts
	static RobotType explore(int numTurns, boolean mine) throws GameActionException {
		boolean isMiner = (rc.getType() == RobotType.MINER);
		double xFrac = Math.min(Math.abs(locSpawn[0] - rc.getMapWidth()), Math.abs(locSpawn[0]));
		double yFrac = Math.min(Math.abs(locSpawn[0] - rc.getMapHeight()), Math.abs(locSpawn[0]));

		//TODO: Create fancy func to determine init direction
		Direction direction = randomDirection();
		for(int numTurnsElapsed = 0; numTurnsElapsed < numTurns; ++numTurnsElapsed) {
			if(senseEnemyRobots() != null) break;
			while(!rc.canMove(direction)) {
				// Will spin if robot stuck. This is (probably) okay behavior, should move when possible
				// Also is kind of inefficient. Oh well.
				direction = randomDirection(direction);
			}
			System.out.printf("ROBOT: %d moved %s", rc.getID(), direction.toString());
			tryMove(direction);
		}

		return null;
	}

	/**
	  Implements bug move When stucks, chooses a direction and moves in that direction,
	  will continue to move in that direction if it cannot move towards goal
	  */
	static boolean bugMove(int x, int y) throws GameActionException{
		System.out.printf("HELLOOOOOO, %d, %d", x, y);
		int spinCounter = 0;
		int turnCount = 0;
		Direction direction = directionToLoc(x, y);
		while((rc.getLocation().x != x) || (rc.getLocation().y != y)) {
			System.out.printf("BUG MOVE turn %d\n", turnCount);
			direction = rc.canMove(directionToLoc(x,y)) ? directionToLoc(x,y) : direction;
			System.out.printf("Trying to move in direction %s\n", direction.toString());
			//if(turnCount >= 200) System.out.println("Unable to reach");
			while(!rc.canMove(direction)) {
				++spinCounter;
				if(spinCounter >= 10000) System.out.println("Spun out, returned false");
				direction = randomDirection(direction); // Does this work better or checking both R and L?
			}
			if(tryMove(direction))
				Clock.yield();
			spinCounter = 0;
			//++turnCount;
		}
		return true;
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
	  Returns an array of squares visible to the robot
	  */
	static MapLocation[] getVisibleLocs() {
		int visionRadius = rc.getCurrentSensorRadiusSquared();
		int radius = (int) Math.pow(visionRadius, 0.5);
		for(int i = 0; i < radius; ++i) {
			for(int j = 0; j < radius; ++j) {
			}
		}

		return null;
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
		return rc.senseNearbyRobots(rc.getCurrentSensorRadiusSquared(), rc.getTeam());
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
