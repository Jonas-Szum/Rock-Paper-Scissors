package prog3;

import java.io.Serializable;
import java.util.function.Consumer;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class Server extends NetworkConnection {

	private int port;
	
	public Server(int port, Consumer<Serializable> callback) {
		super(callback);
		// TODO Auto-generated constructor stub
		this.port = port;
		player1Plays = "";
		player2Plays = "";
		player1Score = 0;
		player2Score = 0;
	}

	//@Override
	public BorderPane setPane()
	{
		TextField myText = new TextField(String.valueOf(this.port));
		//Text for gameStats
		myText.setMaxWidth(50);
		Button myEnter = new Button("Select port to listen to");
		myEnter.setOnAction(e -> {
			String tempString = myText.getText();
			if(!tempString.isEmpty())
			{
				this.port = Integer.valueOf(tempString);
			}
		});
		decidePort = new VBox(5, myEnter, myText);
		
		Button refreshData = new Button("Refresh");
		gameStats = new VBox(10, 
								  refreshData,
								  new Text("Connections Established: " + connections),
								  new Text("Player 1 plays: " + player1Plays), 
								  new Text("Player 2 plays: " + player2Plays), 
								  new Text("Player 1 score: " + String.valueOf(player1Score)),
								  new Text("Player 2 score: " + String.valueOf(player2Score)),
								  new Text("Round: " + String.valueOf(round)));
		refreshData.setOnAction(e -> {
			gameStats.getChildren().set(1, new Text("Connections Established: " + connections));
			gameStats.getChildren().set(2, new Text("Player 1 plays: " + player1Plays));
			gameStats.getChildren().set(3, new Text("Player 2 plays: " + player2Plays));
			gameStats.getChildren().set(4, new Text("Player 1 score: " + String.valueOf(player1Score)));
			gameStats.getChildren().set(5, new Text("Player 2 score: " + String.valueOf(player2Score)));
			gameStats.getChildren().set(6, new Text("Round: " + String.valueOf(round)));
		});
		gameStats.setAlignment(Pos.BOTTOM_LEFT);
		
		Button turnOn = new Button("Turn on server");
		Button turnOff = new Button("Turn off server (Will not allow you to turn back on)");
		turnOff.setDisable(true);
		
		turnOn.setOnAction(e -> { 
			runServer.start(); //connection closed
			turnOff.setDisable(false);
			turnOn.setDisable(true);
			decidePort.setDisable(true);
			//turn on server listening
		});
		
		turnOff.setOnAction(e -> {
			runServer.stop();
			turnOn.setDisable(false);
			turnOff.setDisable(true);
			decidePort.setDisable(false);
			try {
				closeConn();
			} catch (Exception e1) {}
			//turn off server listening
		});
		
		controlStatus = new VBox(turnOn, turnOff);
		
		portAndControl = new HBox(400, decidePort, controlStatus);
		portAndControl.setAlignment(Pos.TOP_CENTER);
		gamePane = new BorderPane();
		gamePane.setBottom(gameStats);
		gamePane.setTop(portAndControl);
		
		return gamePane; //test server
	}
	
	@Override
	protected boolean isServer() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected String getIP() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int getPort() {
		// TODO Auto-generated method stub
		return port;
	}

}

