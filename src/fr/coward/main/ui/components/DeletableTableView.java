package fr.coward.main.ui.components;

import java.util.Optional;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import com.jfoenix.controls.JFXButton;

public class DeletableTableView<T> extends TableView<T> {
	
	private SimpleObjectProperty<T> deletedItem;
	private ContextMenu contextMenu;
	
	private TableColumn<T, Boolean> deleteColumn;

	public DeletableTableView(ObservableList<T> arg0) {
		super(arg0);
	}

	public DeletableTableView() {
		deletedItem = new SimpleObjectProperty<>(null);
		contextMenu = new ContextMenu();
		MenuItem deleteMenu = new MenuItem("Supprimer");
		contextMenu.getItems().add(deleteMenu);
		
		deleteColumn = new TableColumn<>("");
		deleteColumn.setPrefWidth(39);
		deleteColumn.setStyle("-fx-alignment: CENTER;");
		deleteColumn.setSortable(false);
		deleteColumn.setCellValueFactory(row -> new SimpleBooleanProperty(null != row.getValue()));
		deleteColumn.setCellFactory(row -> {
												return new TableCell<T, Boolean>()
												{
													Image deleteImg = new Image(getClass().getResourceAsStream("/fr/coward/main/resources/icons/delete3.png"));
													ImageView deleteImgView = new ImageView(deleteImg);
													JFXButton deleteBtn = new JFXButton("",deleteImgView);
													
													{
														deleteBtn.setOnAction((ActionEvent event) -> {
															T item = getTableView().getItems().get(getIndex());
															removeItem(item);
								                        });
								                    }
													
													@Override
											        protected void updateItem(Boolean item, boolean empty) {
														super.updateItem(item, empty);
														if (empty) {
								                            setGraphic(null);
								                        } else {
								                        	int iconSize = 16;
								                        	deleteImgView.setFitHeight(iconSize);
															deleteImgView.setFitWidth(iconSize);
															deleteBtn.getStyleClass().add("transparent");
															deleteBtn.maxHeight(iconSize);
															deleteBtn.setTooltip(new Tooltip("Supprimer"));
								                        	
								                        	HBox buttonsHBox = new HBox();
								                    		buttonsHBox.getChildren().add(deleteBtn);
								                    		buttonsHBox.maxHeight(iconSize);
								                            setGraphic(buttonsHBox);
								                        }
													};
												};
		});
		
		// Fait apparaitre le menu contextuel
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
	
	public void registerColumns(@SuppressWarnings("unchecked") TableColumn<T, ?>... columns){
		this.getColumns().clear();
		this.getColumns().addAll(columns);
		this.getColumns().add(deleteColumn);
	}
	
	private void removeItem(T itemToRemove){
		ButtonType confirmButtonType = new ButtonType("Confirmer", ButtonData.OK_DONE);
		ButtonType cancelButtonType = new ButtonType("Annuler", ButtonData.CANCEL_CLOSE);
		Dialog<ButtonType> confirmDeleteDialog = new Dialog<>();
		confirmDeleteDialog.setTitle("Demande de confirmation");
		confirmDeleteDialog.setContentText("Etes-vous sûr de vouloir supprimer cet élément ?");
		confirmDeleteDialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, cancelButtonType);
		Optional<ButtonType> result = confirmDeleteDialog.showAndWait();
		if (result.isPresent() && ButtonData.OK_DONE.equals(result.get().getButtonData())) {
			deletedItem.set(itemToRemove);
			getItems().remove(itemToRemove);
		}
	}
	
	/**
	 * @return la property
	 */
	public SimpleObjectProperty<T> getDeletedItemProperty(){
		return this.deletedItem;
	}
}
