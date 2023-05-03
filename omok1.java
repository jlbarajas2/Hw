import javax.swing.*;

import java.awt.*;

import java.awt.event.*;



import java.util.Scanner;

public class Omok {
    private Board board;
    private Player player1;
    private Player player2;
    private NetworkAdapter networkAdapter;
    private boolean isNetworkGame;
    private boolean isClientPlayer;

    public Omok(int boardSize, boolean isNetworkGame) {
        this.isNetworkGame = isNetworkGame;
        this.board = new Board(boardSize, player1, player2);
        if (isNetworkGame) {
            networkAdapter = new NetworkAdapter(this::handleMessage);
            connectToOpponent();
        } else {
            player1 = new HumanPlayer("Player 1", Stone.BLACK);
            player2 = new HumanPlayer("Player 2", Stone.WHITE);
            currentPlayer = player1;
        }
    }

    public void start() {
        while (!board.isGameOver()) {
            if (isNetworkGame && isClientPlayer != (currentPlayer == player1)) {
                continue;
            }
            board.print();
            System.out.println(currentPlayer.getName() + "'s turn.");
            Move move = currentPlayer.getMove(board);
            boolean isValidMove = board.placeStone(move, currentPlayer.getStone());
            if (!isValidMove) {
                System.out.println("Invalid move. Try again.");
                continue;
            }
            if (isNetworkGame) {
                networkAdapter.sendMessage(MessageType.MOVE, move.getColumn() + "," + move.getRow());
            }
            currentPlayer = (currentPlayer == player1) ? player2 : player1;
        }
        board.print();
        Player winner = board.getWinner();
        if (winner == null) {
            System.out.println("It's a tie!");
        } else {
            System.out.println(winner.getName() + " wins!");
        }
    }

    private void handleMessage(Message message) {
        switch (message.getType()) {
            case PLAY:
                isClientPlayer = false;
                System.out.println("Opponent has joined the game.");
                boolean isFirstTurn = board.placeStone(new Move(board.getSize() / 2, board.getSize() / 2), Stone.BLACK);
                networkAdapter.sendMessage(MessageType.PLAY_ACK, "1," + (isFirstTurn ? "1" : "0"));
                currentPlayer = player2;
                break;
            case PLAY_ACK:
                String[] playAckParts = message.getBody().split(",");
                boolean isAccepted = playAckParts[0].equals("1");
                boolean isMyTurn = playAckParts[1].equals("1");
                if (!isAccepted) {
                    System.out.println("Play request was rejected.");
                    break;
                }
                System.out.println("Opponent has accepted the play request.");
                if (isMyTurn) {
                    currentPlayer = player1;
                } else {
                    currentPlayer = player2;
                    isClientPlayer = true;
                }
                break;
            case MOVE:
                String[] moveParts = message.getBody().split(",");
                int column = Integer.parseInt(moveParts[0]);
                int row = Integer.parseInt(moveParts[1]);
                boolean isValidMove = board.placeStone(new Move(column, row), player2.getStone());
                if (!isValidMove) {
                    System.out.println("Invalid move received from opponent.");
                    break;
                }
                networkAdapter.sendMessage(MessageType.MOVE_ACK, moveParts[0] + "," + moveParts[1]);
                currentPlayer = player1;
                break;
            case MOVE_ACK:
                // do nothing
                break;
            case QUIT:
            }
        }
