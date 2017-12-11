import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Used to set the components of the obstacle board, like the lines to limit the spaces for the vehicles
 * in the game.
 */
public class ObstacleBoard extends JPanel implements ActionListener, MouseListener
{
	private LevelAdministrator levelAdministrator = null;
	private ObstacleMatrix obstacleMatrix = null;
	private Timer timerCarAnimation = null;

	private int movingCarX = 0;
	private int movingCarRow = -1;
	private int movingCarColumn = -1;
	private int direction = 0;
	private static final int MOVING_PIXELS = 2;

	public ObstacleBoard()
	{
		this.levelAdministrator = new LevelAdministrator();
		
		this.obstacleMatrix = new ObstacleMatrix( this.levelAdministrator.getCurrentLevel() );
		this.obstacleMatrix.run();

		this.addMouseListener(this);

		this.timerCarAnimation = new Timer(33, this);
	}

	// Override paintComponent to perform your own painting
	/**
	 * It sets the background to white and draws the lines, both horizontally and vertically to limit the 
	 * spaces for the level.
	 * @param g used to change how the obstacle board looks.
	 */
	@Override public void paintComponent(Graphics g)
	{
		super.paintComponent(g);     // paint parent's background
		int cellWidth = this.getWidth() / obstacleMatrix.getColumnCount();
		int cellHeight = this.getHeight() / obstacleMatrix.getRowCount();		
		int paddingHorizontal = (int)(cellWidth * 0.1);
		int paddingVertical = (int)(cellHeight * 0.2);

		for ( int row = 0; row < obstacleMatrix.getRowCount(); ++row )
		{
			for ( int column = 0; column < obstacleMatrix.getColumnCount(); ++column )
			{
				int y = row * cellHeight;
				int x = column * cellWidth;
				g.drawRect(x, y, cellWidth, cellHeight);
				if ( obstacleMatrix.hasObstacleIn(row, column))
				{
					int obstacleSize = obstacleMatrix.getWeigth(row, column);
					int vehicleWidth = cellWidth *  obstacleSize;	
					if ( row == this.movingCarRow && column == this.movingCarColumn )
					{											
						g.drawImage(this.obstacleMatrix.getObstacle(row, column)
								, x + paddingHorizontal + this.movingCarX, y + paddingVertical
								, vehicleWidth - 2 * paddingHorizontal, cellHeight - 2 * paddingVertical, null);
						this.movingCarX += MOVING_PIXELS * this.direction;
						if ( Math.abs(this.movingCarX) > cellWidth )
						{
							this.timerCarAnimation.stop();
							this.movingCarX = 0;
							this.movingCarRow = this.movingCarColumn = MOVING_PIXELS * direction;
							obstacleMatrix.moveObstacle(row, column, direction);
						}
					}
					else
					{					
						g.drawImage(this.obstacleMatrix.getObstacle(row, column)
								, x + paddingHorizontal, y + paddingVertical
								, vehicleWidth - 2 * paddingHorizontal, cellHeight - 2 * paddingVertical, null);			
					}
					for (int displacement = 0; displacement < obstacleSize-1; ++displacement)
					{					
						x = ++column * cellWidth;
						g.drawRect(x, y, cellWidth, cellHeight);
					}
				}
			}
		}
	}
	

	/**
	 * Causes the window to repaint if an action is being performed
	 */
	@Override
	public void actionPerformed(ActionEvent event)
	{
		if ( event.getSource() == this.timerCarAnimation )
		{
			this.repaint();
		}
	}

	/**
	 * Invoked when the mouse button has been clicked (pressed
	 * and released) on a component.
	 */
	@Override
	public void mouseClicked(MouseEvent event)
	{
		if(!this.timerCarAnimation.isRunning())
		{
			int cellWidth = this.getWidth() / obstacleMatrix.getColumnCount();
			int cellHeight = this.getHeight() / obstacleMatrix.getRowCount();
			int row = event.getY() / cellHeight;
			int column = event.getX() / cellWidth;
			System.out.printf("mouseClicked(%d,%d)%n", event.getX(), event.getY());
			System.out.printf("Obstacle(%d,%d)%n", row + 1, column + 1);
			int carWidth = obstacleMatrix.getWeigth(row, column);
			this.direction =event.getX() - carWidth * cellWidth  % cellWidth > 40 ? 1 : -1;
			switch (carWidth)
			{
				case 1: this.direction = event.getX() % cellWidth > 40 ? 1 : -1; break;
				case 2: this.direction = column - obstacleMatrix.getStartColumn(row, column) == 0 ? -1 : 1 ;break;
				case 3: 
					if (column - obstacleMatrix.getStartColumn(row, column) == 0)
						this.direction = -1;
					else if(column - obstacleMatrix.getStartColumn(row, column)-2 == 0)
						this.direction = 1;
					else
						this.direction = event.getX() % cellWidth > 40 ? 1 : -1;
					break;
			}
			System.out.printf("The direction is %d%n", this.direction);			
			if((obstacleMatrix.canMoveTo(row, obstacleMatrix.getStartColumn(row, column), direction)))
			{
				this.movingCarRow = row;
				this.movingCarColumn = obstacleMatrix.getStartColumn(row, column);
				System.out.printf("The coordinates are(%d,%d)%n", movingCarRow+1, movingCarColumn+1);
				this.movingCarX = MOVING_PIXELS * direction;
				this.timerCarAnimation.start();
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent event)
	{
		// TODO Auto-generated method stub
		// System.out.printf("mousePressed(%d,%d)%n", event.getX(), event.getY());
	}

	@Override
	public void mouseReleased(MouseEvent event)
	{
		// TODO Auto-generated method stub
		// System.out.printf("mouseReleased(%d,%d)%n", event.getX(), event.getY());
	}

	@Override
	public void mouseEntered(MouseEvent event)
	{
		// TODO Auto-generated method stub
		// System.out.printf("mouseEntered(%d,%d)%n", event.getX(), event.getY());
	}

	@Override
	public void mouseExited(MouseEvent event)
	{
		// TODO Auto-generated method stub
		// System.out.printf("mouseExited(%d,%d)%n", event.getX(), event.getY());
	}
}
