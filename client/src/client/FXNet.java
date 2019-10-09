package client;


import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class FXNet extends Application {
	private NetworkConnection conn = createClient();
	private TextArea messages = new TextArea();
	
	private Parent createContent() throws Exception{

		BorderPane welcomePane = new BorderPane();
		welcomePane = conn.setPane();
		return welcomePane;
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setScene(new Scene(createContent()));
		primaryStage.setWidth(1200);
	    primaryStage.setHeight(600);
	    primaryStage.show();
        
	}

	public static void main(String[] args) {
		launch(args);
	}

	private Client createClient() {
		return new Client("127.0.0.1", 5555, data -> {
			Platform.runLater(()->{
				messages.appendText(data.toString() + "\n");
			});
		});
	}
	
}
