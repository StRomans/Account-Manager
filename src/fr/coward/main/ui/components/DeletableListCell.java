package fr.coward.main.ui.components;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import com.jfoenix.controls.JFXButton;

public class DeletableListCell<T> extends ListCell<T> {
	
    private HBox hbox = new HBox();
    private Label label = new Label("");
    private Pane pane = new Pane();
    private JFXButton button = new JFXButton("Supprimer");
    
    public static final EventType<Event> DELETE_ITEM = new EventType<Event>("DeleteItem");

    public DeletableListCell() {
    	
        super();
        
        hbox.getChildren().addAll(label, pane, button);
        hbox.setAlignment(Pos.CENTER);
        HBox.setHgrow(pane, Priority.ALWAYS);
        
        button.getStyleClass().add("cell-button");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	setUserData(getItem());
            	fireEvent(new Event(this, event.getTarget(), DELETE_ITEM));
            }
        });
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);  // No text in label of super class
        if (empty) {
            setGraphic(null);
        } else {
            label.setText(item!=null ? item.toString() : "<null>");
            label.setWrapText(true);
            setGraphic(hbox);
        }
    }

}
