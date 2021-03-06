package cpsr.environment.simulation.domains;

import cpsr.environment.ModelQualityExperiment;
import cpsr.environment.components.Action;
import cpsr.environment.components.Observation;
import cpsr.environment.simulation.ASimulator;
import cpsr.planning.PSRPlanningExperiment;

public class GridWorld extends ASimulator 
{

	protected static final char[][] GRID_WORLD = {
		{'x','x','x','x','x','x','x','x','x','x','x','x','x','x'},
		{'x',' ',' ',' ','x',' ',' ',' ',' ',' ',' ',' ',' ','x'},
		{'x',' ',' ',' ',' ',' ',' ',' ',' ',' ','x','G',' ','x'},
		{'x','x','x','x',' ',' ',' ',' ',' ',' ','x',' ',' ','x'},
		{'x',' ',' ',' ',' ',' ',' ',' ',' ',' ','x',' ',' ','x'},
		{'x',' ',' ',' ','x',' ',' ',' ',' ',' ',' ',' ',' ','x'},
		{'x','x','x','x','x','x','x','x','x','x','x','x','x','x'}};

	protected static final int NORTH=0,EAST=1,SOUTH=2,WEST=3;

	protected int xPos, yPos;

	public static void main(String args[])
	{
		GridWorld follow = new GridWorld(10);
		PSRPlanningExperiment experiment = new PSRPlanningExperiment(args[0], args[1], follow);
		experiment.runExperiment();
		experiment.publishResults(args[2]);
//		ModelQualityExperiment experiment = new ModelQualityExperiment("PSRConfigs/follow", "PlanningConfigs/follow", follow);
//		System.out.println("Likilihood: " + experiment.runExperiment());
//		System.out.println("Runtime: " + experiment.getRuntime());
	}
	
	public GridWorld(long seed, int maxRunLength)
	{
		super(seed, maxRunLength);
	}
	
	public GridWorld(long seed)
	{
		super(seed);
	}
	
	@Override
	public String getName()
	{
		return "GridWorld";
	}

	@Override
	protected void initRun() 
	{
		yPos=4;
		xPos=9;
	}

	@Override
	protected int getNumberOfActions() 
	{
		return 4;
	}

	@Override
	protected int getNumberOfObservations() 
	{
		return 16;
	}

	@Override
	protected boolean inTerminalState() 
	{
		return GRID_WORLD[yPos][xPos] == 'G';
	}

	@Override
	protected void executeAction(Action act) 
	{
		int moveDir;

		if(rando.nextDouble() < 0.8)
		{
			moveDir = act.getID();
		}
		else
		{
			moveDir = getPerpindicularDir(act.getID());
		}

		switch(moveDir)
		{
		case NORTH:
			if(GRID_WORLD[yPos-1][xPos] != 'x') yPos--;
			break;
		case EAST:
			if(GRID_WORLD[yPos][xPos+1] != 'x') xPos++;
			break;
		case SOUTH:
			if(GRID_WORLD[yPos+1][xPos] != 'x') yPos++;
			break;
		case WEST:
			if(GRID_WORLD[yPos][xPos-1] != 'x') xPos--;
		}
	}

	protected int getPerpindicularDir(int dir)
	{
		switch(dir)
		{
		case NORTH:
			if(rando.nextBoolean())
			{
				return WEST;
			}
			else
			{
				return EAST;
			}
		case EAST:
			if(rando.nextBoolean())
			{
				return NORTH;
			}
			else
			{
				return SOUTH;
			}
		case SOUTH:
			if(rando.nextBoolean())
			{
				return EAST;
			}
			else
			{
				return WEST;
			}
		case WEST:
			if(rando.nextBoolean())
			{
				return NORTH;
			}
			else
			{
				return SOUTH;
			}
		default:
			return 0;
		}
	}

	@Override
	protected double getCurrentReward() 
	{
		if(inTerminalState())
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}

	@Override
	protected Observation getCurrentObservation() 
	{
		boolean[] walls = new boolean[4];
		
		if(GRID_WORLD[yPos-1][xPos] == 'x')
			walls[0] = true;
		
		if(GRID_WORLD[yPos][xPos+1] == 'x')
			walls[1] = true;
		
		if(GRID_WORLD[yPos+1][xPos] == 'x')
			walls[2] = true;
		
		if(GRID_WORLD[yPos][xPos-1] == 'x')
			walls[3] = true;
		
		int obsID = 0;
		for(int i = 0; i < walls.length; i++)
		{
			if(walls[i])
				obsID += Math.pow(2, i);
		}
			
		return new Observation(obsID);
	}


}
