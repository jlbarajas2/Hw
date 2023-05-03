import java.util.Scanner;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

public class BoardPanel extends JPanel {
    
    private int[][] board;
    private int cellSize;
    
    public BoardPanel(int[][] board, int cellSize) {
        this.board = board;
        this.cellSize = cellSize;
        int width = board.length * cellSize;
        int height = board[0].length * cellSize;
        setPreferredSize(new Dimension(width, height));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw the board grid
        g.setColor(Color.BLACK);
        for (int i = 0; i <= board.length; i++) {
            int x = i * cellSize;
            g.drawLine(x, 0, x, getHeight());
        }
        for (int j = 0; j <= board[0].length; j++) {
            int y = j * cellSize;
            g.drawLine(0, y, getWidth(), y);
        }
        
        // Draw the stones
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                int stone = board[i][j];
                if (stone != 0) {
                    int x = i * cellSize + cellSize / 2;
                    int y = j * cellSize + cellSize / 2;
                    if (stone == 1) {
                        g.setColor(Color.BLACK);
                    } else {
                        g.setColor(Color.WHITE);
                    }
                    g.fillOval(x - cellSize / 4, y - cellSize / 4, cellSize / 2, cellSize / 2);
                }
            }
        }
    }
}

public class Board {
    private final int size;
    private Stone[][] grid;
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private boolean gameOver;

    public Board(int size, Player player1, Player player2) {
        this.size = size;
        this.player1 = player1;
        this.player2 = player2;
        this.grid = new Stone[size][size];
        this.currentPlayer = player1;
    }

    public int getSize() {
        return size;
    }

    public Stone getStoneAt(int row, int col) {
        return grid[row][col];
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean placeStone(int row, int col) {
        if (gameOver || grid[row][col] != null) {
            return false;
        }
        Stone stone = new Stone(currentPlayer);
        grid[row][col] = stone;
        currentPlayer.addStone(stone);

        if (checkForWin(row, col)) {
            gameOver = true;
        } else {
            currentPlayer = (currentPlayer == player1) ? player2 : player1;
        }

        return true;
    }

private boolean checkForWin(int row, int col) {

Stone stone = grid[row][col];

if (stone == null) {

return false;

}

int count = 0;



// check horizontal

for (int c = 0; c < size; c++) {

if (grid[row][c] != null && grid[row][c].getPlayer() == stone.getPlayer()) {

count++;

} else {

count = 0;

}

if (count == 5) {

return true;

}

}



// check vertical

count = 0;

for (int r = 0; r < size; r++) {

if (grid[r][col] != null && grid[r][col].getPlayer() == stone.getPlayer()) {

count++;

} else {

count = 0;

}

if (count == 5) {

return true;

}

}



// check diagonal from top-left to bottom-right

count = 0;

int r = row - Math.min(row, col);

int c = col - Math.min(row, col);

while (r < size && c < size) {

if (grid[r][c] != null && grid[r][c].getPlayer() == stone.getPlayer()) {

count++;

} else {

count = 0;

}

if (count == 5) {

return true;

}

r++;

c++;

}



// check diagonal from top-right to bottom-left

count = 0;

r = row - Math.min(row, size - col - 1);

c = col + Math.min(row, size - col - 1);

while (r < size && c >= 0) {

if (grid[r][c] != null && grid[r][c].getPlayer() == stone.getPlayer()) {

count++;

} else {

count = 0;

}

if (count == 5) {

return true;

}

r++;

c--;

}



return false;

}

public class NetworkAdapter {
    private static final String PLAY_MSG = "play:";
    private static final String PLAY_ACK_MSG = "play_ack:";
    private static final String MOVE_MSG = "move:";
    private static final String MOVE_ACK_MSG = "move_ack:";
    private static final String QUIT_MSG = "quit:";

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public NetworkAdapter(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void sendPlayRequest() {
        out.println(PLAY_MSG);
    }

    public void sendMove(int x, int y) {
        out.println(MOVE_MSG + x + "," + y);
    }

    public void sendQuit() {
        out.println(QUIT_MSG);
    }

    public boolean receivePlayAck() throws IOException {
        String msg = in.readLine();
        String[] parts = msg.split(":")[1].split(",");
        int response = Integer.parseInt(parts[0]);
        int turn = Integer.parseInt(parts[1]);
        return response == 1 && (turn == 0 || turn == 1);
    }

    public void receiveMoveAck() throws IOException {
        String msg = in.readLine();
        String[] parts = msg.split(":")[1].split(",");
        int x = Integer.parseInt(parts[0]);
        int y = Integer.parseInt(parts[1]);
    }

    public void close() throws IOException {
        socket.close();
    }
}



}

public class Player {
    private String name;
    private int score;
    private List<Stone> stones;

    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.stones = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        score += points;
    }

    public void addStone(Stone stone) {
        stones.add(stone);
    }

    public List<Stone> getStones() {
        return stones;
    }
}

public class Stone {
    private Player player;

    public Stone(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}


