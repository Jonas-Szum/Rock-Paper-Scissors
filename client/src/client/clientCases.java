package client;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
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
import javafx.stage.Stage;
class clientCases {
	NetworkConnection thisClient;
	TextArea messages;
	BorderPane gamePane;

	
	@BeforeAll
	static void pre() throws Exception
	{
		JFXPanel doobopshoodooboodop = new JFXPanel();
	}
	
	@BeforeEach
	void setUp() throws Exception {
		thisClient = new Client("127.0.0.1", 5555, data -> {
			Platform.runLater(()->{}); });
		gamePane = thisClient.setPane();
	}

	@AfterEach
	void tearDown() throws Exception {}

	@Test
	void nullTest() {
		assertNull(gamePane.getTop(), "Top pane not null");
		assertNull(gamePane.getBottom(), "Bottom pane not null");
		assertNull(gamePane.getLeft(), "Left pane not null");
		assertNull(gamePane.getRight(), "Right pane not null");
		assertNotNull(gamePane.getCenter(), "Center pane is null");
	}
	
	void defaultIPTest()
	{
		assertEquals("127.0.0.1", thisClient.getIP(), "Constructor IP and actual IP are not the same");
	}
	
	void defaultPortTest()
	{
		assertEquals(5555, thisClient.getPort(), "Constructor port and actual port are not the same");
	}
	
	void setIPTest()
	{
		thisClient.setIP("127.0.0.2");
		assertEquals("127.0.0.2", thisClient.getIP(), "Could not change the IP from default");
	}
	
	void setPortTest()
	{
		thisClient.setPort(4444);
		assertEquals(4444, thisClient.getPort(), "Could not change Port from default");
	}

}
