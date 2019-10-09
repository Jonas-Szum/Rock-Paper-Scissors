package prog3;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import prog3.NetworkConnection.startConn;

public abstract class NetworkConnection {
	protected volatile BorderPane gamePane;
	//private ConnThread connthread;// = new ConnThread();
	private volatile Consumer<Serializable> callback;
	
	protected volatile int connections;
	protected volatile String player1Plays;
	protected volatile String player2Plays;
	protected volatile int player1Score;
	protected volatile int player2Score;
	protected volatile int round;
	protected volatile VBox decidePort;
	protected volatile VBox gameStats;
	protected volatile VBox controlStatus;
	protected volatile HBox portAndControl;
	protected volatile String p1CurrentPlay;
	protected volatile String p2CurrentPlay;
	protected volatile boolean player1TookTurn;
	protected volatile boolean player2TookTurn;
	protected volatile boolean player1Written;
	protected volatile boolean player2Written;
	protected volatile boolean p1PlayAgain;
	protected volatile boolean p2PlayAgain;
	protected volatile boolean p1DecisionMade;
	protected volatile boolean p2DecisionMade;
	protected ArrayList<ConnThread> joined;
	//protected volatile boolean loading;
	//protected ArrayList<Integer> playersID;
	protected volatile startConn runServer;
	public NetworkConnection(Consumer<Serializable> callback) {
		this.callback = callback;
		runServer = new startConn();
		connections = 0;
		p1CurrentPlay = "";
		p2CurrentPlay = "";
		//updatedInfo = false;
		player1TookTurn = false;
		player2TookTurn = false;
		player1Written = false;
		player2Written = false;
		player1Score = 0;
		player2Score = 0;
		p1PlayAgain = false;
		p2PlayAgain = false;
		p1DecisionMade = false;
		p2DecisionMade = false;
		//loading = true;
		round = 1;
		joined = new ArrayList<ConnThread>();
	}
	
	
	 class startConn extends Thread {
		 public void run() {
			try{
				ServerSocket mySocket = new ServerSocket(getPort());	
				while(true) {
					try {
					ConnThread connthread = new ConnThread(mySocket.accept());
					connthread.setDaemon(true);
					connthread.start();}
					catch(Exception e) {}
				}
			}
			 catch(Exception e) {}
		 }
	}
	
	public void send(ConnThread connthread, Serializable data) throws Exception{ //havent used this in current implementation
		connthread.out.writeObject(data);
	}

	public void closeConn() throws Exception{
		for(ConnThread me : joined)
		{
			me.closeConn();
		}
	}

	abstract public BorderPane setPane();
	
	abstract protected boolean isServer();
	abstract protected String getIP();
	abstract protected int getPort();
	
class ConnThread extends Thread{
	private volatile Socket socket;
	private volatile int serverPort;
	private volatile ObjectOutputStream out;

	ConnThread(Socket socket)
	{
		joined.add(this);
		this.socket = socket;
	}
	public void closeConn()
	{
		try{this.socket.close();}
		catch(Exception e) {};
	}
	public void run() {
			try(ServerSocket server = new ServerSocket(serverPort);
					ObjectOutputStream out = new ObjectOutputStream( socket.getOutputStream());
					ObjectInputStream in = new ObjectInputStream(socket.getInputStream())){
				connections++;
				int playerIt = 0;
				int iter = 0;
				Platform.runLater(new Runnable() {
					@Override public void run() {
						VBox gameStats = (VBox) gamePane.getBottom();
						gameStats.getChildren().set(1, new Text("Connections Established: " + connections));
						gameStats.getChildren().set(2, new Text("Player 1 plays: " + player1Plays));
						gameStats.getChildren().set(3, new Text("Player 2 plays: " + player2Plays));
						gameStats.getChildren().set(4, new Text("Player 1 score: " + String.valueOf(player1Score)));
						gameStats.getChildren().set(5, new Text("Player 2 score: " + String.valueOf(player2Score)));
						gameStats.getChildren().set(6, new Text("Round: " + String.valueOf(round))); 
					}
				});
				int thisPlayer = connections;
				this.out = out;
				out.writeObject(connections); //send the player number
				socket.setTcpNoDelay(true);
				while(true) {
					iter++;
					if(iter == 100000) iter = 3; //i had byte overflows so i did this
					if( ((thisPlayer == 1 && playerIt == 0 && connections > 1) || iter == 1
						|| (thisPlayer == 2 && playerIt == 0 && connections > 1))
						 && (player1Written == false && player2Written == false)) //only send the first iteration to the first player
						{
						if(thisPlayer == 1 && playerIt == 0 && connections > 1) //if its the first case, iterate playerIt
							playerIt++;
						else if(thisPlayer == 2 && playerIt == 0  && connections > 1)
							playerIt++;
						out.writeObject(connections);
						}
					if(connections > 1 && playerIt != 0)
					{
					if(((thisPlayer == 1 && p1CurrentPlay == "") || (thisPlayer == 2 && p2CurrentPlay == ""))
						&& round != 4)
					{
						Serializable data = (Serializable) in.readObject(); //should be rock paper lizard spock
						callback.accept(data);
						if(thisPlayer == 1)
						{
							player1TookTurn = true;
							p1CurrentPlay = (String)data;
						}
						if(thisPlayer == 2)
						{
							player2TookTurn = true;
							p2CurrentPlay = (String)data;
						}
						//read what a client played, calculate score, update GUI
					}
					if(player1TookTurn == true && player2TookTurn == true) //each thread will send data to their respective thread
					{
						synchronized(this) {
						if(thisPlayer == 1 && player1Written == false) //only update out-of-scope info once
						{
							
							if(round != 1)
							{
								player1Plays += ", ";
								player2Plays += ", ";
							}
							player1Plays += p1CurrentPlay;
							player2Plays += p2CurrentPlay;
							round++;
							if(p1CurrentPlay.equals("Rock"))
							{
								if(p2CurrentPlay.equals("Spock") || p2CurrentPlay.equals("Paper"))
									{
									player2Score++;
									}
								else if(!p2CurrentPlay.equals("Rock"))
									player1Score++;
							}
							else if(p1CurrentPlay.equals("Paper"))
							{
								if(p2CurrentPlay.equals("Lizard") || p2CurrentPlay.equals("Scissors"))
									player2Score++;
								else if(!p2CurrentPlay.equals("Paper"))
									player1Score++;
							}
							else if(p1CurrentPlay.equals("Scissors"))
							{
								if(p2CurrentPlay.equals("Spock") || p2CurrentPlay.equals("Rock"))
									player2Score++;
								else if(!p2CurrentPlay.equals("Scissors"))
									player1Score++;
							}
							else if(p1CurrentPlay.equals("Lizard"))
							{
								if(p2CurrentPlay.equals("Rock") || p2CurrentPlay.equals("Scissors"))
									player2Score++;
								else if(!p2CurrentPlay.equals("Lizard"))
									player1Score++;
							}
							else if(p1CurrentPlay.equals("Spock"))
							{
								if(p2CurrentPlay.equals("Paper") || p2CurrentPlay.equals("Lizard"))
									player2Score++;
								else if(!p2CurrentPlay.equals("Spock"))
									player1Score++;
							}
							//loading = false;
								Platform.runLater(new Runnable() {
								@Override public void run() {
									
									VBox gameStats = (VBox) gamePane.getBottom();
									gameStats.getChildren().set(1, new Text("Connections Established: " + connections));
									gameStats.getChildren().set(2, new Text("Player 1 plays: " + player1Plays));
									gameStats.getChildren().set(3, new Text("Player 2 plays: " + player2Plays));
									gameStats.getChildren().set(4, new Text("Player 1 score: " + String.valueOf(player1Score)));
									gameStats.getChildren().set(5, new Text("Player 2 score: " + String.valueOf(player2Score)));
									gameStats.getChildren().set(6, new Text("Round: " + String.valueOf(round)));
									}
								});
						}
						
						if((thisPlayer == 1 && player1Written == false) 
						|| (thisPlayer == 2 && player2Written == false && player1Written == true))//wait until player 1 writes
						{
							try{out.writeObject(player1Plays);
							out.writeObject(player2Plays);
							out.writeObject(player1Score);
							out.writeObject(player2Score);
							out.writeObject(round);}
							catch(Exception e) {System.out.println(e);};
							if(thisPlayer == 1)
								{
								player1Written = true;
								}
							else if(thisPlayer == 2)
								{
								player2Written = true;
								}
							playerIt = 0;
							iter = 0;

						}
						}
						if(player1Written == true && player2Written == false)
							while(player2Written == false && !(player1Written == false && player2Written == false)) {}
						if(player1Written == true && player2Written == true)
						{
							if(round != 4)
							{
								playerIt = 0;
								iter = 0;
								player1TookTurn = false;
								player1Written = false;
								p1CurrentPlay = "";
								player2TookTurn = false;
								player2Written = false;
								p2CurrentPlay = "";
							}
							
							if(round == 4 )
							{
								Serializable playAgain = (Serializable)in.readObject();	
								callback.accept(playAgain);
								
								String yesOrNo = (String)playAgain;
								if(yesOrNo.equals("yes"))
								{
									if(thisPlayer == 1)
										p1PlayAgain = true;
									else
										p2PlayAgain = true;
								}
								else
								{
									if(thisPlayer == 1)
										p1PlayAgain = false;
									else
										p2PlayAgain = false;
								}
								if(thisPlayer == 1)
									p1DecisionMade = true;
								else
									p2DecisionMade = true;
								//cant synchronize this while loop
							while(p1DecisionMade == false || p2DecisionMade == false) {}
							if(p1PlayAgain == true && p2PlayAgain == true)
								{
								round = 1;
								Platform.runLater(new Runnable() {
									@Override public void run() {
								VBox playingAgain = new VBox(new Text("Clients are playing again!"));
								playingAgain.setAlignment(Pos.CENTER_LEFT);
								gamePane.setLeft(playingAgain);
									}});
								out.writeObject(true);
								p1CurrentPlay = "";
								p2CurrentPlay = "";
								player1Plays = "";
								player2Plays = "";
								player1TookTurn = false;
								player2TookTurn = false;
								player1Written = false;
								player2Written = false;
								player1Score = 0;
								player2Score = 0;
								p1PlayAgain = false;
								p2PlayAgain = false;
								p1DecisionMade = false;
								p2DecisionMade = false;
								}
							else
							{
								Platform.runLater(new Runnable() {
									@Override public void run() {
							VBox playingAgain = new VBox(new Text("Clients are not playing again!"));
							playingAgain.setAlignment(Pos.TOP_CENTER);
							gamePane.setLeft(playingAgain);
									}});
							out.writeObject(false);		
							} 
							player1TookTurn = false;
							player2TookTurn = false;
							player1Written = false;
							player2Written = false;
							p1CurrentPlay = "";
							p2CurrentPlay = "";
							round = 1;}
						}
						else
						{
							System.out.println();
						}
						
					}
				} //end the if statement checking for more than 1 players
				} //end while loop
			} //end exception statement
			catch(Exception e) {
				callback.accept("connection Closed");
				connections--;
				Platform.runLater(new Runnable() {
					@Override public void run() {
						VBox gameStats = (VBox) gamePane.getBottom();
						gameStats.getChildren().set(1, new Text("Connections Established: " + connections));
						gameStats.getChildren().set(2, new Text("Player 1 plays: " + player1Plays));
						gameStats.getChildren().set(3, new Text("Player 2 plays: " + player2Plays));
						gameStats.getChildren().set(4, new Text("Player 1 score: " + String.valueOf(player1Score)));
						gameStats.getChildren().set(5, new Text("Player 2 score: " + String.valueOf(player2Score)));
						gameStats.getChildren().set(6, new Text("Round: " + String.valueOf(round))); 
					}
				});
			}
		}
	}
	
}	

