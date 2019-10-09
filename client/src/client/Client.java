package client;

import java.io.Serializable;
import java.util.function.Consumer;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class Client extends NetworkConnection {

	private String ip;
	private int port; 
	
	public Client(String ip, int port, Consumer<Serializable> callback) {
		super(callback);
		this.ip = ip;
		this.port = port;
	}

	//@Override
	public BorderPane setPane()
	{
		player1Moves = "";
		player2Moves = "";
		player1Score = 0;
		player2Score = 0;
		round = 0;
		//String newIP = "";
		Text player;
		if(playerID == 0)
			player = new Text("");
		else
			player = new Text("Player " + String.valueOf(playerID));
		
		rock = new Button();		
		paper = new Button();
		scissors = new Button();
		lizard = new Button();
		spock = new Button();
		rock.setDisable(true);
		paper.setDisable(true);
		scissors.setDisable(true);
		lizard.setDisable(true);
		spock.setDisable(true);
		
		ImageView theRock = new ImageView(new Image("rock.jpg"));
		theRock.setFitWidth(100);
		theRock.setFitHeight(100);
		rock.setGraphic(theRock);
		rock.setOnAction(e -> {
			try {send("Rock");}
			catch(Exception e1) {System.out.println(e1);}
			round++;
			rock.setDisable(true);
			paper.setDisable(true);
			scissors.setDisable(true);
			lizard.setDisable(true);
			spock.setDisable(true);
		});
		
		
		ImageView thePaper = new ImageView(new Image("paper.jpg"));
		thePaper.setFitWidth(100);
		thePaper.setFitHeight(100);
		paper.setGraphic(thePaper);
		paper.setOnAction(e -> {
			try {send("Paper");}
			catch(Exception e1) {System.out.println(e1);}
			round++;
			rock.setDisable(true);
			paper.setDisable(true);
			scissors.setDisable(true);
			lizard.setDisable(true);
			spock.setDisable(true);
		});
		
		ImageView theScissors = new ImageView(new Image("scissors.jpg"));
		theScissors.setFitWidth(100);
		theScissors.setFitHeight(100);
		scissors.setGraphic(theScissors);
		scissors.setOnAction(e -> {
			try {send("Scissors");}
			catch(Exception e1) {System.out.println(e1);}
			round++;
			rock.setDisable(true);
			paper.setDisable(true);
			scissors.setDisable(true);
			lizard.setDisable(true);
			spock.setDisable(true);
		});
		
		ImageView theLizard = new ImageView(new Image("lizard.jpg"));
		theLizard.setFitWidth(100);
		theLizard.setFitHeight(100);
		lizard.setGraphic(theLizard);
		lizard.setOnAction(e -> {
			try {send("Lizard");}
			catch(Exception e1) {System.out.println(e1);}
			round++;
			rock.setDisable(true);
			paper.setDisable(true);
			scissors.setDisable(true);
			lizard.setDisable(true);
			spock.setDisable(true);
		});
		
		ImageView theSpock = new ImageView(new Image("spock.png"));
		theSpock.setFitWidth(100);
		theSpock.setFitHeight(100);
		spock.setGraphic(theSpock);
		spock.setOnAction(e -> {
			try {send("Spock");}
			catch(Exception e1) {System.out.println(e1);}
			round++;
			rock.setDisable(true);
			paper.setDisable(true);
			scissors.setDisable(true);
			lizard.setDisable(true);
			spock.setDisable(true);
		});
		
		
		
		
		HBox test = new HBox(10, rock, paper, scissors, lizard, spock);
		
		selectPort = new Button("Select port to connect to");
		enterPort = new TextField(String.valueOf(this.port));
		HBox portOption = new HBox(5, selectPort, enterPort);
		selectPort.setOnAction(e -> {
			String tempString = enterPort.getText();
			if(!tempString.isEmpty())
			{
				this.port = Integer.valueOf(tempString);
			}
		});
		
		selectIP = new Button("Set IP");
		enterIP = new TextField(String.valueOf(this.ip));
		HBox IPOption = new HBox(5, selectIP, enterIP);
		selectIP.setOnAction(e -> {
			String tempString = enterIP.getText();
			if(!tempString.isEmpty())
				this.ip = tempString;
		});
		
		disconnectB = new Button("Disconnect");
		connectB = new Button("Connect");
		VBox connectionOption = new VBox(connectB, disconnectB);
		disconnectB.setDisable(true);
		connectB.setOnAction(e ->{
			disconnectB.setDisable(false);
			connectB.setDisable(true);
			selectIP.setDisable(true);
			enterIP.setDisable(true);
			selectPort.setDisable(true);
			enterPort.setDisable(true);
			
			rock.setDisable(false);
			paper.setDisable(false);
			scissors.setDisable(false);
			lizard.setDisable(false);
			spock.setDisable(false);
			//rock.setDisable(false);
			//System.out.println("Buttons enabled");
			//System.out.println(spock.isDisabled());
			try{startConn();}
			catch(Exception e1) 
			{
				System.out.println(e1);
				disconnectB.setDisable(true);
				connectB.setDisable(false);
				selectIP.setDisable(false);
				enterIP.setDisable(false);
				selectPort.setDisable(false);
				enterPort.setDisable(false);
				
				rock.setDisable(true);
				paper.setDisable(true);
				scissors.setDisable(true);
				lizard.setDisable(true);
				spock.setDisable(true);
			}
		});
		
		disconnectB.setOnAction(e ->{
			connectB.setDisable(false);
			disconnectB.setDisable(true);
			test.setDisable(true);
			rock.setDisable(true);
			paper.setDisable(true);
			scissors.setDisable(true);
			lizard.setDisable(true);
			spock.setDisable(true);
			selectIP.setDisable(false);
			enterIP.setDisable(false);
			selectPort.setDisable(false);
			enterPort.setDisable(false);
			
			VBox temp = (VBox)gamePane.getCenter();
			temp.getChildren().set(0, new Text("Player "));
			
			try{closeConn();}
			catch(Exception e1) {
				System.out.println(e1);
			}
		});
		
		playerScores = new VBox(
				new Text("Player 1 score: " + String.valueOf(player1Score)),
				new Text("Player 2 score: " + String.valueOf(player2Score)));
		
		playerMoves = new VBox(new Text("Player 1 moves: " + player1Moves),
									new Text("Player 2 moves: " + player2Moves));
		
		VBox showAll = new VBox(10,
								player,
								connectionOption,
								test,
								portOption,
								IPOption,
								playerMoves, //5
								playerScores); //6
		showAll.setAlignment(Pos.CENTER_LEFT);
		
		gamePane.setCenter(showAll);
		
		yes = new Button("Play again!");
		no = new Button("I quit!");
		yes.setOnAction(e ->{
			no.setDisable(true);
			yes.setDisable(true);
			try{
				send("yes");
				}
			catch(Exception e1) {System.out.println(e1);}
			});
			no.setOnAction(e -> {
				no.setDisable(true);
				yes.setDisable(true);
				boolean doWeGoAgain = false;
				Serializable maybeContinue;
				try{send("no");}
				catch(Exception e1) {System.out.println(e1);}
				
				gamePane.setCenter(null);
				gamePane.setLeft(null);
				gamePane.setTop(null);
				gamePane.setRight(null);
				gamePane.setBottom(null);
				gamePane.setCenter(new Text("You quit!"));
			});
			
		return gamePane;
	}
	@Override
	protected boolean isServer() {
	
		return false;
	}

	@Override
	protected String getIP() {
		// TODO Auto-generated method stub
		return this.ip;
	}

	@Override
	protected int getPort() {
		// TODO Auto-generated method stub
		return this.port;
	}
	
	@Override
	public void setIP(String newIP)
	{
		this.ip = newIP;
	}
	
	@Override
	public void setPort(int newPort)
	{
		this.port = newPort;
	}

}

