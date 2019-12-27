/**
 * 
 */
package fr.coward.main.ui.components;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

/**
 * @author Coward
 *
 */
public class DeletableListView<T> extends ListView<T> {
	
	private SimpleObjectProperty<T> deletedItem;
	private ContextMenu contextMenu;
	
	public DeletableListView() {
		
		deletedItem = new SimpleObjectProperty<>(null);
		contextMenu = new ContextMenu();
		MenuItem deleteMenu = new MenuItem("Supprimer");
		contextMenu.getItems().add(deleteMenu);
        
		setCellFactory(new Callback<ListView<T>, ListCell<T>>() {

			@Override
			public ListCell<T> call(ListView<T> list) {
				
				 return new DeletableListCell<T>();
			}
		});
		
		this.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

	        @Override
	        public void handle(MouseEvent e) {
	        	if(contextMenu.isShowing()) contextMenu.hide();
	        	
	            if (e.getButton() == MouseButton.SECONDARY) {
	            	
	            	contextMenu.show((Node)e.getSource(), e.getScreenX(), e.getScreenY());
                    
                    deleteMenu.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {
							removeItem(getSelectionModel().getSelectedItem());
						}
                    	
                    });
	            }
	        }
	    });
		
		/**
		 * Gère le click sur le bouton supprimer
		 */
		this.addEventHandler(DeletableListCell.DELETE_ITEM, new EventHandler<Event>() {

			@SuppressWarnings("unchecked")
			@Override
			public void handle(Event event) {
				removeItem((T) ((Node)event.getTarget()).getUserData());
			}
		});
	}
	
	private void removeItem(T itemToRemove){
		deletedItem.set(itemToRemove);
		getItems().remove(deletedItem.getValue());
	}
	
	/**
	 * @return la property
	 */
	public SimpleObjectProperty<T> getDeletedItemProperty(){
		return this.deletedItem;
	}
}
