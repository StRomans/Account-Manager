package fr.coward.main.ui.components;

import java.util.List;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;


public class HoveredPieData extends StackPane {
	
	public static final int DEFAULT_COLOR_SEQUENCE_LENGTH = 8;
	
	private Label label;
	
	public HoveredPieData(String text) {
		// Lui retirer le focus sinon il le prend directement
		setMouseTransparent(true);

		label = new Label(text);
		
		//label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
		label.setWrapText(true);
		this.getChildren().add(label);
		
		this.setFocusTraversable(false);
		
	}
	
	public void setData(int pieIndex, String textToDisplay, List<String> styleClass){
		
		label.setText(textToDisplay);
		
		label.getStyleClass().clear();
		label.getStyleClass().addAll(styleClass);
		label.getStyleClass().remove("chart-pie");
		label.getStyleClass().addAll("chart-line-symbol","chart-series-line");
		label.setStyle("-fx-font-size: 10; -fx-font-weight: bold;");
	}
}
