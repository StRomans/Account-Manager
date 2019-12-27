package fr.coward.main.ui.components;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;


public class HoveredNode extends StackPane {
	
	public HoveredNode(double value, String symbol, int lineIndex) {

		setPrefSize(7, 7);

		final Label label = createDataThresholdLabel(value, symbol, lineIndex);

		setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				
				getChildren().setAll(label);
				setCursor(Cursor.NONE);
				toFront();
			}
		});
		setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				getChildren().clear();
			}
		});
	}

	private Label createDataThresholdLabel(double value, String symbol, int lineIndex) {
		
		String prefix = "";
		if(value > 0){
			prefix = "+";
		}
		
		final Label label = new Label(prefix + String.valueOf(value) + " " + symbol);
		label.getStyleClass().addAll("default-color" + lineIndex, "chart-line-symbol","chart-series-line");
		label.setStyle("-fx-font-size: 10; -fx-font-weight: bold;");
		
		label.setTextFill(Color.BLACK);
		label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
		
		return label;
	}
}
