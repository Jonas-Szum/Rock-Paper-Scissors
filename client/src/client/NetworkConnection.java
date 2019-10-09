package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public abstract class NetworkConnection {
	protected int playerID;
	protected BorderPane gamePane;
	protected ConnThread connthread = new ConnThread();
	protected Consumer<Serializable> callback;
	protected String player1Moves;
	protected String player2Moves;
	protected int player1Score;
	protected int player2Score;
	protected int playerSize;
	protected Button rock;
	protected Button paper;
	protected Button scissors;
	protected Button lizard;
	protected Button spock;
	protected Button yes;
	protected Button no;
	protected volatile int round;
	protected boolean doWeGoAgain;
	protected Button selectPort;
	protected Button selectIP;
	protected Button connectB;
	protected Button disconnectB;
	protected TextField enterPort;
	protected TextField enterIP;
	protected VBox playerScores;
	protected VBox playerMoves;
	public NetworkConnection(Consumer<Serializable> callback) {
		this.callback = callback;
		connthread.setDaemon(true);
		player1Moves = "";
		player2Moves = "";
		player1Score = 0;
		player2Score = 0;
		doWeGoAgain = false;
		gamePane = new BorderPane();
		round = 1;
	}
	
	public void startConn() throws Exception{
		connthread.start();
	}
	
	public void send(Serializable data) throws Exception{
		connthread.out.writeObject(data);
		connthread.out.flush();
	}
	
	public void closeConn() throws Exception{
		ConnThread temp = connthread;
		this.connthread = new ConnThread();
		this.connthread.setDaemon(true);
		temp.socket.close();
	}
	
	abstract public BorderPane setPane();
	
	abstract protected boolean isServer();
	abstract protected String getIP();
	abstract protected int getPort();
	abstract protected void setIP(String newIP);
	abstract protected void setPort(int newPort);
	public void reset()
	{
		setPane();
		HBox test = (HBox)(((VBox)(gamePane.getCenter())).getChildren().get(2));
		disconnectB.setDisable(false);
		connectB.setDisable(true);
		selectIP.setDisable(true);
		enterIP.setDisable(true);
		selectPort.setDisable(true);
		enterPort.setDisable(true);
		gamePane.setBottom(null);
		for(Node but : test.getChildren())
		{
			but.setDisable(false);
		}
		//Text playerText = (Text)gamePane.getChildren().get(0);
		//playerText.setText("Player " + String.valueOf(playerID));
		round = 1;
	}
	
	class ConnThread extends Thread{
		private Socket socket;
		private ObjectOutputStream out;
		public void run() { //this now has direct access to the gamePane, so there doesn't need to be a "refresh" button
			try(ServerSocket server = null;
					Socket socket = new Socket(getIP(), getPort());
					ObjectOutputStream out = new ObjectOutputStream( socket.getOutputStream());
					ObjectInputStream in = new ObjectInputStream(socket.getInputStream())){
				
				this.socket = socket;
				this.out = out;
				socket.setTcpNoDelay(true);
				Serializable data = (Serializable) in.readObject(); //accept player ID
				callback.accept(data);
				playerID = (int)data;
				Platform.runLater(new Runnable() {
					@Override public void run() {
						VBox gameStats = (VBox)gamePane.getCenter(); 
						gameStats.getChildren().set(0, new Text("Player " + String.valueOf(playerID)));
						if(playerID == 1)
							gamePane.setTop(new Text("Only one connected!"));
					}
				});
				while(true) {
					Serializable numPlayers = (Serializable) in.readObject(); //will accept the first iteration and then update when another player joins
					//System.out.println("player " + playerID + " accepted how many players");
					callback.accept(numPlayers);
					playerSize = (int)numPlayers;
					if(playerSize == 1)
					{
						Platform.runLater(new Runnable() {
							@Override public void run() {
								gamePane.setTop(new Text("You're the only one connected!"));
							}});
					}
					else
					{
						Platform.runLater(new Runnable() {
							@Override public void run() {
						gamePane.setTop(null);
							}});
					//System.out.println("player " + playerID + " waiting for moves");
					Serializable player1Move = (Serializable) in.readObject(); //read player 1 move
					callback.accept(player1Move);
					player1Moves = (String)player1Move + " ";
					Serializable player2Move = (Serializable) in.readObject(); //read player 2 move
					callback.accept(player2Move);
					player2Moves = (String)player2Move + " ";
					//System.out.println("Recieved player 1 and 2 moves");
					
					Serializable player1ScoreT = (Serializable) in.readObject(); //read player 1 score
					callback.accept(player1ScoreT);
					player1Score = (int)player1ScoreT;
					Serializable player2ScoreT = (Serializable) in.readObject(); //read player 2 score
					callback.accept(player2ScoreT);
					player2Score = (int)player2ScoreT;
					//System.out.println("Recieved scores");
					
					Serializable currRound = (Serializable) in.readObject();
					callback.accept(currRound);
					round = (int)currRound;
					//System.out.println("Round " + round + " for player " + playerID);
					if(round != 4){
					Platform.runLater(new Runnable() {
						@Override public void run() {
						//System.out.println("Should update");
						//System.out.println("Player 1 Score: " + player1Score);
						//System.out.println("Player 2 Score: " + player2Score);
							VBox gameStats = (VBox)gamePane.getCenter(); 
							
							
							gameStats.getChildren().set(6, new VBox(
									new Text("Player 1 score: " + String.valueOf(player1Score)),
									new Text("Player 2 score: " + String.valueOf(player2Score))));
							
							gameStats.getChildren().set(5, new VBox (
									new Text("Player 1 moves: " + player1Moves),
									new Text("Player 2 moves: " + player2Moves)));
							rock.setDisable(false);
							paper.setDisable(false);
							scissors.setDisable(false);
							lizard.setDisable(false);
							spock.setDisable(false);
						}
					});}
					
					
					if(round == 4) //3 rounds played
					{
						Platform.runLater(new Runnable() {
							@Override public void run() {
						VBox gameStats = (VBox)gamePane.getCenter(); 
						
						
						gameStats.getChildren().set(6, new VBox(
								new Text("Player 1 score: " + String.valueOf(player1Score)),
								new Text("Player 2 score: " + String.valueOf(player2Score))));
						
						gameStats.getChildren().set(5, new VBox (
								new Text("Player 1 moves: " + player1Moves),
								new Text("Player 2 moves: " + player2Moves)));
						//System.out.println("round 4 for player " + playerID);
						//gamePane.setCenter(null);
						//gamePane.setLeft(null);
						//gamePane.setTop(null);
						gamePane.setRight(null);
						gamePane.setBottom(null);
						rock.setDisable(true);
						paper.setDisable(true);
						scissors.setDisable(true);
						lizard.setDisable(true);
						spock.setDisable(true);
						disconnectB.setDisable(false);
						connectB.setDisable(true);
						selectIP.setDisable(true);
						enterIP.setDisable(true);
						selectPort.setDisable(true);
						enterPort.setDisable(true);
						
						HBox replay = new HBox(5, yes, no);
						replay.setAlignment(Pos.CENTER);
						gamePane.setRight(replay);
						
						}});
						//boolean doWeGoAgain = false;
						//System.out.println("Will we continue?");
						Serializable maybeContinue = (Serializable)in.readObject();	
						doWeGoAgain = (boolean)maybeContinue;
						//System.out.println("Recieved whether or not we will continue: " + doWeGoAgain);
						if(doWeGoAgain == true)
							Platform.runLater(new Runnable() {
								@Override public void run() {
										reset(); 
										gamePane.setRight(null);}});
						else
						{
							Platform.runLater(new Runnable() {
								@Override public void run() {
							gamePane.setCenter(new Text("Someone voted to not continue the game!"));
							gamePane.setRight(null);
								}});
						}
						
					} 
				} //end the else statement checking for multiple players
				} //end the while loop
			}
			catch(Exception e) {
				//System.out.println(e);
				callback.accept("connection Closed");
			}
		}
	}
	
}	

