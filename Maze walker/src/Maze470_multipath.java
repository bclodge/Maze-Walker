import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

public class Maze470_multipath 
{
	public static final int MWIDTH=30,MHEIGHT=30,BLOCK=20;
	public static boolean robotActive=true;
	public static final int SPEED=100;

	public static final int LEFT=4,RIGHT=8,UP=1,DOWN=2;

	//1=wall above, 2=wall below, 4=wall on left, 8=wall on right, 16=not included in maze yet
	static int[][] maze;
	static MazeComponent mazecomp;

	//current position of robot
	static int robotX=0,robotY=0;

	//true means that a "crumb" is shown in the room
	static boolean[][] crumbs;

	public static void main(String[] args)
	{
		//maze a maze array and a crumb array
		maze=new int[MWIDTH][MHEIGHT];
		crumbs=new boolean[MWIDTH][MHEIGHT];
		//set each room to be surrounded by walls and not part of the maze
		for (int i=0; i<MWIDTH; i++)
			for (int j=0; j<MHEIGHT; j++)
			{
				maze[i][j]=31;
				crumbs[i][j]=false;
			}

		//generate the maze
		makeMaze();

		//knock down up to 100 walls
		for(int i=0; i<100; i++)
		{
			int x=(int)(Math.random()*(MWIDTH-2));
			int y=(int)(Math.random()*MHEIGHT);
			if((maze[x][y]&RIGHT)!=0)
			{
				maze[x][y]^=RIGHT;
				maze[x+1][y]^=LEFT;
			}
		}

		JFrame f = new JFrame();
		f.setSize(MWIDTH*BLOCK+15,MHEIGHT*BLOCK+30);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setTitle("Maze!");
		mazecomp=new MazeComponent();
		f.add(mazecomp);
		f.setVisible(true);

		 int[] testDirs = new int[]{RIGHT,DOWN,DOWN,LEFT,UP};
		 
		 
		//have the robot wander around in its own thread
		if(robotActive)
        {
			
			
		    new Thread(new Runnable(){
			    public void run() {
				    //doMazeRandomWalk();
			    	
			    	position test = new position(robotX, robotY);
			    	test.dfs(robotX, robotY);
			    	
			    	
			    	
			    	
			    }
		    }).start();
        }
	}

	public static void makeMaze()
	{
		int[] blockListX = new int[MWIDTH*MHEIGHT];
		int[] blockListY = new int[MWIDTH*MHEIGHT];
		int blocks=0;
		int x,y;

		//Choose random starting block and add it to maze
		x=(int)(Math.random()*(MWIDTH-2)+1);
		y=(int)(Math.random()*(MHEIGHT-2)+1);
		maze[x][y]^=16;

		//Add all adjacent blocks to blocklist
		if (x>0)
		{
			blockListX[blocks]=x-1;
			blockListY[blocks]=y;
			blocks++;
		}
		if (x<MWIDTH-1)
		{
			blockListX[blocks]=x+1;
			blockListY[blocks]=y;
			blocks++;
		}
		if (y>0)
		{
			blockListX[blocks]=x;
			blockListY[blocks]=y-1;
			blocks++;
		}
		if (y<MHEIGHT-1)
		{
			blockListX[blocks]=x;
			blockListY[blocks]=y+1;
			blocks++;
		}

		//approach:
		// start with a single room in maze and all neighbors of the room in the "blocklist"
		// choose a room that is not yet part of the maze but is adjacent to the maze
		// add it to the maze by breaking a wall
		// put all of its neighbors that aren't in the maze into the "blocklist"
		// repeat until everybody is in the maze
		while (blocks>0)
		{
			//choose a random block from blocklist
			int b = (int)(Math.random()*blocks);

			//find which block in the maze it is adjacent to
			//and remove that wall
			x=blockListX[b];
			y=blockListY[b];

			//get a list of all of its neighbors that aren't in the maze
			int[] dir=new int[4];
			int numdir=0;

			//left
			if (x>0 && (maze[x-1][y]&16)==0)
			{
				dir[numdir++]=0;
			}
			//right
			if (x<MWIDTH-1 && (maze[x+1][y]&16)==0)
			{
				dir[numdir++]=1;
			}
			//up
			if (y>0 && (maze[x][y-1]&16)==0)
			{
				dir[numdir++]=2;
			}
			//down
			if (y<MHEIGHT-1 && (maze[x][y+1]&16)==0)
			{
				dir[numdir++]=3;
			}

			//choose one at random
			int d = (int)(Math.random()*numdir);
			d=dir[d];

			//tear down the wall
			//left
			if (d==0)
			{
				maze[x][y]^=LEFT;
				maze[x-1][y]^=RIGHT;
			}
			//right
			else if (d==1)
			{
				maze[x][y]^=RIGHT;
				maze[x+1][y]^=LEFT;
			}
			//up
			else if (d==2)
			{
				maze[x][y]^=UP;
				maze[x][y-1]^=DOWN;
			}
			//down
			else if (d==3)
			{
				maze[x][y]^=DOWN;
				maze[x][y+1]^=UP;
			}

			//set that block as "in the maze"
			maze[x][y]^=16;

			//remove it from the block list
			for (int j=0; j<blocks; j++)
			{
				if ((maze[blockListX[j]][blockListY[j]]&16)==0)
				{
					for (int i=j; i<blocks-1; i++)
					{
						blockListX[i]=blockListX[i+1];
						blockListY[i]=blockListY[i+1];
					}
					blocks--;
					j=0;
				}
			}

			//put all adjacent blocks that aren't in the maze in the block list
			if (x>0 && (maze[x-1][y]&16)>0)
			{
				blockListX[blocks]=x-1;
				blockListY[blocks]=y;
				blocks++;
			}
			if (x<MWIDTH-1 && (maze[x+1][y]&16)>0)
			{
				blockListX[blocks]=x+1;
				blockListY[blocks]=y;
				blocks++;
			}
			if (y>0 && (maze[x][y-1]&16)>0)
			{
				blockListX[blocks]=x;
				blockListY[blocks]=y-1;
				blocks++;
			}
			if (y<MHEIGHT-1 && (maze[x][y+1]&16)>0)
			{
				blockListX[blocks]=x;
				blockListY[blocks]=y+1;
				blocks++;
			}
		}

		//remove top left and bottom right edges
//		maze[0][0]^=LEFT;    //commented out for now so that robot doesn't run out the entrance
		maze[MWIDTH-1][MHEIGHT-1]^=RIGHT;
	}

	//the robot will wander around aimlessly until it happens to stumble on the exit
/*	public static void doMazeRandomWalk()
	{
		int dir=RIGHT;

		while(robotX!=MWIDTH-1 || robotY!=MHEIGHT-1)
		{
			int x=robotX;
			int y=robotY;

			//choose a direction at random
			dir=new int[]{LEFT,RIGHT,UP,DOWN}[(int)(Math.random()*4)];
			//move the robot
			if((maze[x][y]&dir)==0)
			{
				if(dir==LEFT) robotX--;
				if(dir==RIGHT) robotX++;
				if(dir==UP) robotY--;
				if(dir==DOWN) robotY++;
			}

			//leave a crumb
			crumbs[x][y]=true;

			//repaint and pause momentarily
			mazecomp.repaint();
			try{ Thread.sleep(SPEED); } catch(Exception e) { }
		}
		System.out.println("Done");
	}
*/
	public static void doMazeGuidedWalk(int [] directions)
	{
		int dir = RIGHT;
		int dirCounter=0;
		
		
		while(robotX!=MWIDTH-1 || robotY!=MHEIGHT-1)
		{
			int x=robotX;
			int y=robotY;

			//choose a direction at random
			 dir = directions[dirCounter];
			//move the robot
			if((maze[x][y]&dir)==0)
			{
				if(dir==LEFT) robotX--;
				if(dir==RIGHT) robotX++;
				if(dir==UP) robotY--;
				if(dir==DOWN) robotY++;
			}

			//leave a crumb
			crumbs[x][y]=true;

			//repaint and pause momentarily
			mazecomp.repaint();
			try{ Thread.sleep(SPEED); } catch(Exception e) { }
			
			if(dirCounter<=directions.length)
			{
				dirCounter++;
			}
			else
			{
				break;
			}
			
	
			
		}
		System.out.println("Done");
	}
		
	public static class MazeComponent extends JComponent
	{
		public void paintComponent(Graphics g)
		{
			g.setColor(Color.WHITE);
			g.fillRect(0,0,MWIDTH*BLOCK,MHEIGHT*BLOCK);
			g.setColor(new Color(100,0,0));
			for (int x=0; x<MWIDTH; x++)
			{
				for (int y=0; y<MHEIGHT; y++)
				{
					if ((maze[x][y]&1)>0)
						g.drawLine(x*BLOCK,y*BLOCK,x*BLOCK+BLOCK,y*BLOCK);
					if ((maze[x][y]&2)>0)
						g.drawLine(x*BLOCK,y*BLOCK+BLOCK,x*BLOCK+BLOCK,y*BLOCK+BLOCK);
					if ((maze[x][y]&4)>0)
						g.drawLine(x*BLOCK,y*BLOCK,x*BLOCK,y*BLOCK+BLOCK);
					if ((maze[x][y]&8)>0)
						g.drawLine(x*BLOCK+BLOCK,y*BLOCK,x*BLOCK+BLOCK,y*BLOCK+BLOCK);
				}
			}

			if (robotActive)
			{
				g.setColor(Color.BLUE);
				for (int x=0; x<MWIDTH; x++)
				{
					for (int y=0; y<MHEIGHT; y++)
					{
						if (crumbs[x][y])
							g.fillRect(x*BLOCK+BLOCK/2-1,y*BLOCK+BLOCK/2-1,2,2);
					}
				}

				g.setColor(Color.GREEN);
				g.fillOval(robotX*BLOCK+1,robotY*BLOCK+1,BLOCK-2,BLOCK-2);
			}
		}
	}
	
	public static class position 
	{
		// rememberX, rememberY for position
		int rememberX;
		int rememberY;
		position parent;
		int direction;
		
		// Position constructor(int x, int y)
		public position(int x, int y)
		{
			this.rememberX= x;
			this.rememberY =y;
	
		}
		
		// issolved x==MWIDTH-1&& y==MHEIGHT-1
		public boolean isSolved()
		{
			if(rememberX == MWIDTH-1 && rememberY == MHEIGHT-1 )
			{
				return true;
			}
			return false;
		}
		
		public boolean canMove(int direction)
		{
			boolean moveable;
			if((maze[rememberX][rememberY]&direction)==0)
			{
				moveable = true;
			}
			else
			{
				moveable = false;
			}
			return moveable;
		}
		
		public int[] allmMoves()
		{
			int count = 0;
			
			if(canMove(UP)) count++;
			if(canMove(DOWN)) count++;
			if(canMove(LEFT)) count++;
			if(canMove(RIGHT)) count++;

			int[] moves=new int[count];

			count=0;
			if(canMove(UP)) moves[count++]=UP;
			if(canMove(DOWN)) moves[count++]=DOWN;
			if(canMove(LEFT)) moves[count++]=LEFT;
			if(canMove(RIGHT)) moves[count++]=RIGHT;

			return moves;
		}
		//init direction
		
		//move method
			//child.parent = this;
			//child = direction = direction;
		public position move(int direction)
		{
			position child = new position(robotX, robotY);
			
			child.parent = this;
			child.direction = direction;
			
			if(direction == UP)
			{
				child.rememberY--;
			}
			if(direction == DOWN)
			{
				child.rememberY++;
			}
			if(direction == RIGHT)
			{
				child.rememberX++;
			}
			if(direction == LEFT)
			{
				child.rememberX--;
			}
			
			return child;
		}
		
		public position[] adjecentPositions()
		{
			int[] dirs = allmMoves();
			
			position[] adj = new position[dirs.length]; 
			
			for(int i=0; i<dirs.length; i++)
			{
				adj[i]=move(dirs[i]);
				System.out.println(adj[i]);
			}
			return adj;
			
		}
		
		
		public void dfs(int x, int y)
		{
			position state = new position(x, y);
			
			position child = state;
			
			ArrayList<position> queue=new ArrayList<position>();
			
			 ArrayList<position> visited=new ArrayList<position>();
			
			ArrayList<position> solution=new ArrayList<position>();
			
			queue.add(state);
			
			int count = 0;
			
			
			while(queue.size()>0)
			{
				state = queue.remove(queue.size()-1);
				count++;
				
				System.out.println("Visiting: " + state.rememberX + state.rememberY);
				
				
				if (state.isSolved())
					break;
				
				position[] adjMoves = state.adjecentPositions();
				
				for(position neighbor: adjMoves)
				{
					boolean haveSeen = false;
					for(int i=0; i<visited.size();i++)
					{
						if(visited.get(i).equals(neighbor))
						{
							haveSeen = true;
							break;
						}
						if(!haveSeen)
						queue.add(neighbor);
					}
				}
				
			}
			
			/*while(state.parent != null)
			{
				solution.add(state);
				
				state = state.parent;
			}
			
			for(int i=0; i<solution.size();i++)
			{
				System.out.println(state.rememberX + state.rememberY);
				System.out.println("here");
			}
			*/
		}
		
	}

}
