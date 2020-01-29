package player;
import battlecode.common.*;
import java.lang.Math;
import java.util.ArrayList;

public strictfp class RobotPlayer {
    static RobotController rc;
	static Movement mv;

    static Direction[] directions = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    static RobotType[] spawnedByMiner = {RobotType.REFINERY, RobotType.VAPORATOR, RobotType.DESIGN_SCHOOL,
            RobotType.FULFILLMENT_CENTER, RobotType.NET_GUN};

        /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        RobotPlayer.rc = rc;

		RobotPlayer.mv = new Movement(rc);

        mv.turnCount = 0;
		mv.initialRoundNum = rc.getRoundNum();

		mv.locSpawn = rc.getLocation();

		if(rc.getLocation().x > (int)(2*rc.getMapWidth()/3)) mv.isRight = true;
		else if(rc.getLocation().x > (int)(rc.getMapWidth()/3)) mv.isMidX = true;
		else mv.isLeft = true;

		if(rc.getLocation().y > (int)(2*rc.getMapWidth()/3)) mv.isTop = true;
		if(rc.getLocation().y > (int)(rc.getMapWidth()/3)) mv.isMidY = true;
		else mv.isBottom = true;

        while (true) {
            mv.turnCount += 1;
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                switch (rc.getType()) {
                    case HQ:
						HQ hq = new HQ(rc, mv);
						hq.run();
						break;
                    case MINER:
						Miner miner = new Miner(rc, mv);
						miner.run();
						break;
                    case REFINERY:
						Refinery refinery = new Refinery(rc, mv);
						refinery.run();
						break;
                    case VAPORATOR:
						Vaporator vaporator = new Vaporator(rc, mv);
						vaporator.run();
						break;
                    case DESIGN_SCHOOL:
						DesignSchool school = new DesignSchool(rc, mv);
						school.run();
						break;
                    case FULFILLMENT_CENTER:
						FulfillmentCenter fc = new FulfillmentCenter(rc, mv);
						fc.run();
						break;
                    case LANDSCAPER:
						Landscaper landscaper = new Landscaper(rc, mv);
						landscaper.run();
						break;
                    case DELIVERY_DRONE:
						Drone drone = new Drone(rc, mv);
						drone.run();
						break;
                    case NET_GUN:
						NetGun netGun = new NetGun(rc, mv);
						netGun.run();
						break;
                }

            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            }
			System.out.println("NOO I RETURNED. I MESSED UP");
        }
    }

    /**
     * Returns a random RobotType spawned by miners.
     *
     * @return a random RobotType
     */
    static RobotType randomSpawnedByMiner() {
        return spawnedByMiner[(int) (Math.random() * spawnedByMiner.length)];
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

	static void readMessages() {
		//for(String message : messages) {

		//}
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
        if (mv.turnCount < 3) {
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
