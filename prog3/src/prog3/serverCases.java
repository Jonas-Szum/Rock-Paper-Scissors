package prog3;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

class serverCases {
	NetworkConnection serverBoi;
	TextArea messages;
	BorderPane gamePane;
	@BeforeAll
	void pre() throws Exception
	{
		JFXPanel doobopshoodooboodop = new JFXPanel();
	}
	
	@BeforeEach
	void setUp() throws Exception {
		serverBoi = new Server(5555, data -> {
			Platform.runLater(()->{}); });
		gamePane = serverBoi.setPane();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void nullTest() {
		assertNull(gamePane.getTop(), "Top pane not null");
		assertNotNull(gamePane.getBottom(), "Bottom pane not null");
		assertNull(gamePane.getLeft(), "Left pane not null");
		assertNull(gamePane.getRight(), "Right pane not null");
		assertNotNull(gamePane.getCenter(), "Center pane is null");
	}
	void isServerTest() {
		assertTrue(serverBoi.isServer()); //in the current implementation, this doesn't mean much
										  //but in an implementation where server and client extend the same NetworkConnection class, this would be important
	}
	void NullIPTest() {
		assertNull(serverBoi.getIP());
	}

}
