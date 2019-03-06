import java.awt.Font;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import java.awt.FlowLayout;
import java.awt.BorderLayout;

public class Snake extends JFrame implements KeyListener, WindowListener {

	private JPanel contentPane;
	private JTextArea board = new JTextArea();
	private JTextArea input = new JTextArea("Click here if controls aren't working.");
	private JTextArea scoreArea = new JTextArea("Score: ");
	ArrayList<SnakeCell> snake = new ArrayList<SnakeCell>();
	int[][] text = new int[22][22];
	boolean exit = false;
	boolean movedToNext = true;
	boolean start = false;
	String direction = "left";
	int score = 0;

	public static void main(String[] args) {
		Snake game = new Snake("Snake");
		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.setVisible(true);
		game.restart();
		game.update();
		while (!game.exit) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			game.movedToNext = true;
			if (game.start) {
				game.nextGen();
			}
		}
	}

	public Snake(String title) {
		super(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		for (int i = 0; i < text.length; i++)
			for (int j = 0; j < text[i].length; j++)
				text[i][j] = 0;
		board = new JTextArea();
		board.setEditable(false);
		board.setRows(20);
		board.setColumns(20);
		board.setText(update_screen(snake));
		contentPane.setLayout(new BorderLayout(0, 0));
		input.setEditable(false);
		scoreArea.setEditable(false);
		scoreArea.setText("Score: " + score);
		scoreArea.setFocusable(false);
		contentPane.add(scoreArea, BorderLayout.SOUTH);
		contentPane.add(input, BorderLayout.NORTH);
		contentPane.add(board, BorderLayout.CENTER);
		input.addKeyListener(this);
		input.setFocusable(true);
		update();
	}

	public void update() {
		remove(board);
		board = new JTextArea(update_screen(snake), 0, 0);
		board.setFont(new Font("Helvetica", Font.PLAIN, 16));
		board.setEditable(false);
		add(board, BorderLayout.CENTER);
		remove(scoreArea);
		if (!start)
			scoreArea = new JTextArea("          Score: " + score + "   |   Press any key to restart!");
		else
			scoreArea = new JTextArea("Score: " + score);
		scoreArea.setEditable(false);

		scoreArea.setFocusable(false);
		add(scoreArea, BorderLayout.SOUTH);
		pack();
	}

	public void restart() {
		for (int i = 0; i < text.length; i++)
			for (int j = 0; j < text[i].length; j++)
				text[i][j] = 0;
		snake.clear();
		score = 0;
		snake.add(0, new SnakeCell(11, 11, "left"));
		text[(int) (20 * Math.random() + 1)][(int) (20 * Math.random() + 1)] = -1;
		start = false;
	}

	public void addCell(ArrayList<SnakeCell> snake) {
		SnakeCell temp = snake.get(snake.size() - 1);
		if (temp.direction == "left")
			snake.add(new SnakeCell(temp.x + 1, temp.y, temp.direction));
		else if (temp.direction == "right")
			snake.add(new SnakeCell(temp.x - 1, temp.y, temp.direction));
		else if (temp.direction == "up")
			snake.add(new SnakeCell(temp.x, temp.y + 1, temp.direction));
		else if (temp.direction == "down")
			snake.add(new SnakeCell(temp.x, temp.y - 1, temp.direction));
	}

	private String update_screen(ArrayList<SnakeCell> snake) {
		String out = "";
		for (int i = 0; i < 21; i++)
			for (int j = 1; j < 21; j++)
				if (text[i][j] != -1 && text[i][j] != 2)
					text[i][j] = 0;
		for (int i = 0; i < snake.size(); i++) {
			text[snake.get(i).x][snake.get(i).y] = 1;
		}
		for (int i = 2; i < 21; i++) {
			for (int j = 2; j < 21; j++) {
				if (text[j][i] == 1 || text[j][i] == 2)
					out += '\u25A0';
				else if (text[j][i] == -1)
					out += '\u25A3';
				else
					out += '\u25A1';
				out += "  ";
			}

			out += String.format("%n");
		}
		return out;
	}

	private void nextGen() {
		// when the player dies
		if (snake.get(0).direction == "left") {
			if (snake.get(0).x - 1 < 2 || text[snake.get(0).x - 1][snake.get(0).y] == 1) {
				rip();
			}
		} else if (snake.get(0).direction == "right") {
			if (snake.get(0).x + 1 > 20 || text[snake.get(0).x + 1][snake.get(0).y] == 1) {
				rip();
			}
		} else if (snake.get(0).direction == "up") {
			if (snake.get(0).y - 1 < 2 || text[snake.get(0).x][snake.get(0).y - 1] == 1) {
				rip();
			}
		} else if (snake.get(0).direction == "down") {
			if (snake.get(0).y + 1 > 20 || text[snake.get(0).x][snake.get(0).y + 1] == 1) {
				rip();
			}
		}
		if (!exit) {
			// when the player scores
			if (snake.get(0).direction == "left" && text[snake.get(0).x - 1][snake.get(0).y] == -1) {
				score();
			} else if (snake.get(0).direction == "right" && text[snake.get(0).x + 1][snake.get(0).y] == -1) {
				score();
			} else if (snake.get(0).direction == "up" && text[snake.get(0).x][snake.get(0).y - 1] == -1) {
				score();
			} else if (snake.get(0).direction == "down" && text[snake.get(0).x][snake.get(0).y + 1] == -1) {
				score();
			}
			// any other time
			for (int i = 0; i < snake.size(); i++) {
				if (i != 0) {
					if (snake.get(i).direction.equals("left")) {
						snake.set(i, new SnakeCell(snake.get(i).x - 1, snake.get(i).y, snake.get(i).direction));
					} else if (snake.get(i).direction.equals("right")) {
						snake.set(i, new SnakeCell(snake.get(i).x + 1, snake.get(i).y, snake.get(i).direction));
					} else if (snake.get(i).direction.equals("up")) {
						snake.set(i, new SnakeCell(snake.get(i).x, snake.get(i).y - 1, snake.get(i).direction));
					} else if (snake.get(i).direction.equals("down")) {
						snake.set(i, new SnakeCell(snake.get(i).x, snake.get(i).y + 1, snake.get(i).direction));
					}
				} else if (i == 0 || i == snake.size() - 1) {
					if (snake.get(i).direction.equals("left")) {
						snake.set(i, new SnakeCell(snake.get(i).x - 1, snake.get(i).y, snake.get(i).direction));
					} else if (snake.get(i).direction.equals("right")) {
						snake.set(i, new SnakeCell(snake.get(i).x + 1, snake.get(i).y, snake.get(i).direction));
					} else if (snake.get(i).direction.equals("up")) {
						snake.set(i, new SnakeCell(snake.get(i).x, snake.get(i).y - 1, snake.get(i).direction));
					} else if (snake.get(i).direction.equals("down")) {
						snake.set(i, new SnakeCell(snake.get(i).x, snake.get(i).y + 1, snake.get(i).direction));
					}
				}
			}
			updateDir(snake);
			update();
		}
	}

	private void score() {
		int rand1 = (int) (18 * Math.random() + 2);
		int rand2 = (int) (18 * Math.random() + 2);
		while (text[rand1][rand2] == 1) {
			rand1 = (int) (18 * Math.random() + 2);
			rand2 = (int) (18 * Math.random() + 2);
		}
		addCell(snake);
		text[rand1][rand2] = -1;
		score++;
	}

	private void rip() {
		start = false;
		snake.clear();
		update();
		restart();
	}

	public void keyPressed(KeyEvent e) {
		start = true;
		if (movedToNext) {
			movedToNext = false;
			if ((e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == 87) && snake.get(0).direction != "down") {
				snake.set(0, new SnakeCell(snake.get(0).x, snake.get(0).y, "up"));
			} else if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == 83) && snake.get(0).direction != "up") {
				snake.set(0, new SnakeCell(snake.get(0).x, snake.get(0).y, "down"));
			} else if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == 65)
					&& snake.get(0).direction != "right") {
				snake.set(0, new SnakeCell(snake.get(0).x, snake.get(0).y, "left"));
			} else if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == 68)
					&& snake.get(0).direction != "left") {
				snake.set(0, new SnakeCell(snake.get(0).x, snake.get(0).y, "right"));
			} else if (e.getKeyCode() == 82 && !start) {
				restart();
			}
		}
	}

	private void updateDir(ArrayList<SnakeCell> snake) {
		ArrayList<SnakeCell> temp = new ArrayList<SnakeCell>();
		for (int i = 0; i < snake.size(); i++)
			temp.add(snake.get(i));
		if (snake.size() > 1 && movedToNext) {
			int i = 0;
			while (i < (snake.size() - 1)) {
				if (snake.get(i).direction != snake.get(i + 1).direction) {
					temp.set(i + 1, new SnakeCell(snake.get(i + 1).x, snake.get(i + 1).y, snake.get(i).direction));
				}
				i++;
			}
		}
		for (int i = 0; i < snake.size(); i++)
			snake.set(i, temp.get(i));

	}

	public void keyReleased(KeyEvent arg0) {

	}

	public void keyTyped(KeyEvent arg0) {

	}

	public void windowActivated(WindowEvent arg0) {

	}

	public void windowClosed(WindowEvent arg0) {

	}

	public void windowClosing(WindowEvent arg0) {
		exit = true;
		dispose();
		System.exit(0);
	}

	public void windowDeactivated(WindowEvent arg0) {

	}

	public void windowDeiconified(WindowEvent arg0) {

	}

	public void windowIconified(WindowEvent arg0) {

	}

	public void windowOpened(WindowEvent arg0) {

	}

}

class SnakeCell {

	int x;
	int y;
	String direction = "left";

	public SnakeCell(int x, int y, String direction) {
		this.x = x;
		this.y = y;
		this.direction = direction;
	}

	public String toString() {
		return String.format("[%d,%d,%s]", x, y, direction);
	}

}
