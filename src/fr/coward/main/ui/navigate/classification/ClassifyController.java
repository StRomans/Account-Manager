package fr.coward.main.ui.navigate.classification;

import java.net.URL;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;

import com.jfoenix.controls.JFXButton;
import com.sun.javafx.scene.control.skin.ListViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;

import fr.coward.main.model.MenuContext;
import fr.coward.main.model.SubCategory;
import fr.coward.main.model.Transaction;
import fr.coward.main.model.rule.RuleEngine;
import fr.coward.main.model.services.SubCategoryService;
import fr.coward.main.model.services.TransactionService;
import fr.coward.main.ui.navigate.NavigationManager;
import fr.coward.main.ui.navigate.NavigationManager.Menu;
import fr.coward.main.ui.service.TimerService;

public class ClassifyController implements Initializable {
	@FXML
	private Label nbTransactionLabel;	
	@FXML
	private ListView<Transaction> transactionList;
	@FXML
	private ListView<SubCategory> subCategoryList;
	@FXML
	private JFXButton applyRulesButton;
	
	private int dragSourceIndex = -1;
	private ContextMenu transactionListContextMenu = new ContextMenu();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		transactionListContextMenu = new ContextMenu();
		MenuItem deleteTransaction = new MenuItem("Supprimer");
        MenuItem buildRuleItem = new MenuItem("Créer une règle");
        transactionListContextMenu.getItems().add(deleteTransaction);
        transactionListContextMenu.getItems().add(buildRuleItem);
		
		loadTransactionsToClassify();
		loadSubCategories();
		
		Image dragImage = new Image(getClass().getResourceAsStream("/fr/coward/main/resources/icons/transaction.png"));
		
		applyRulesButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				RuleEngine.classify(transactionList.getItems());
				loadTransactionsToClassify();
				
			}
		});

		/**
		 * DRAG SOURCE
		 */
		transactionList.setCellFactory(new Callback<ListView<Transaction>, ListCell<Transaction>>() {

			@Override
			public ListCell<Transaction> call(ListView<Transaction> list) {
				
				 final ListCell<Transaction> cell = new ListCell<Transaction>() {
	                    @Override
	                    public void updateItem(Transaction transaction, boolean empty) {
	                        super.updateItem(transaction,  empty);
	                        if (empty) {
	                            setText(null);
	                        } else {
	                            setText(transaction.toString());
	                        }
	                    }
	                };
	                
	                cell.setOnDragDetected(new EventHandler<MouseEvent>() {
	                    @Override
	                    public void handle(MouseEvent event) {
	                        if (!cell.isEmpty()) {
	                        	dragSourceIndex = cell.getIndex();
	                            Dragboard dragboard = cell.startDragAndDrop(TransferMode.MOVE);
	                            ClipboardContent clipboard = new ClipboardContent();
	                            clipboard.putString(String.valueOf(cell.getItem().getId()));
	                            dragboard.setContent(clipboard);
	                            // Java 8 only:
//	                            dragboard.setDragView(cell.snapshot(null, null));
	                            dragboard.setDragView(dragImage);
	                        }
	                    }
	                });

				return cell;
			}
			
		});
		
		transactionList.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

	        @Override
	        public void handle(MouseEvent e) {
	        	if(transactionListContextMenu.isShowing()) transactionListContextMenu.hide();
	        	
	            if (e.getButton() == MouseButton.SECONDARY) {
	            	
                    transactionListContextMenu.show(transactionList, e.getScreenX(), e.getScreenY());
                    
                    deleteTransaction.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {
							// Supprimer la transaction
							Transaction transactionToDelete = transactionList.getSelectionModel().getSelectedItem();
							TransactionService.delete(transactionToDelete);
							// Rafraichir l'écran
							transactionList.getSelectionModel().clearSelection();
                            transactionList.getItems().remove(transactionToDelete);
                            updateTransactionHeader();
						}
                    	
                    });
                    
                    buildRuleItem.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {
							// Rebond sur l'écran de création de règle pré-initialisé
							MenuContext.getInstance().addInitObj(ClassificationRulesController.TRANSACTION_PARAM, transactionList.getSelectionModel().getSelectedItem());
							NavigationManager.goToMenu(Menu.ClassificationRules);
						}
                    	
                    });
	            }
	        }
	    });

		
		/**
		 * DRAG TARGET
		 */
		subCategoryList.setCellFactory(new Callback<ListView<SubCategory>, ListCell<SubCategory>>() {

			@Override
			public ListCell<SubCategory> call(ListView<SubCategory> list) {
				
				 final ListCell<SubCategory> cell = new ListCell<SubCategory>() {
	                    @Override
	                    public void updateItem(SubCategory subCategory, boolean empty) {
	                        super.updateItem(subCategory,  empty);
	                        if (empty) {
	                            setText(null);
	                        } else {
	                            setText(subCategory.toStringForClassificationScreen());
	                        }
	                    }
	                };
	                
	                // authorize item to be dropped
	                cell.setOnDragOver(new EventHandler<DragEvent>() {
	                    @Override
	                    public void handle(DragEvent event) {
	                        if (!cell.isEmpty() && dragSourceIndex >= 0 && event.getDragboard().hasString()) {
	                            event.acceptTransferModes(TransferMode.MOVE);
	                        }
	                    }
	                });
	                
	                // highlight drop target by changing background color:
	                cell.setOnDragEntered(new EventHandler<DragEvent>() {
	                    @Override
	                    public void handle(DragEvent event) {
	                        if (!cell.isEmpty() && dragSourceIndex >= 0  && event.getDragboard().hasString()) {
	                        	
	                        	// Gestion de l'autoscroll
	                        	ListViewSkin<?> listViewSkin = (ListViewSkin<?>) subCategoryList.getSkin();
	                            VirtualFlow<?> virtualFlow = (VirtualFlow<?>) listViewSkin.getChildren().get(0);
	                            int firstVisible = virtualFlow.getFirstVisibleCell().getIndex();
	                            int lastVisible = virtualFlow.getLastVisibleCell().getIndex();
	                            
	                            // scroll vers le bas
	                            if(cell.getIndex() == lastVisible){
	                            	subCategoryList.scrollTo(firstVisible + 1);
	                            }
	                            // scroll vers le haut
	                            if(cell.getIndex() == firstVisible){
	                            	subCategoryList.scrollTo(firstVisible - 1 );
	                            }

	                        	setCellColor(cell, "gold");
	                        }
	                    }
	                });
	                
	                // remove highlight when exited
	                cell.setOnDragExited(new EventHandler<DragEvent>() {
	                    @Override
	                    public void handle(DragEvent event) {
	                        if (!cell.isEmpty() && dragSourceIndex >= 0  && event.getDragboard().hasString()) {
	                        	
	                        	setCellColor(cell, "white");
	                        }
	                    }
	                });
	                
	                cell.setOnDragDropped(new EventHandler<DragEvent>() {
	                    @Override
	                    public void handle(DragEvent event) {
	                        if (!cell.isEmpty() && dragSourceIndex >= 0  && event.getDragboard().hasString()) {

	                            cell.setStyle("-fx-background-color: green;");
	                            
	                            setCellColor(cell, "green");
	                            
	                            TimerService timer = new TimerService(800);
	                            timer.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
	                                @Override
	                                public void handle(WorkerStateEvent event) {
	                                	setCellColor(cell, "white");
	                                }
	                            });
	                            timer.start();
	                        } 
	                        
                            transactionList.getSelectionModel().clearSelection();
                            transactionList.getItems().remove(dragSourceIndex);
                            updateTransactionHeader();
                            cell.getItem().classify(event.getDragboard().getString());
	                        dragSourceIndex= -1;
	                    }
	                });

				return cell;
			}
			
		});

	}
	
	private void setCellColor(ListCell<?> cell , String color){
//		if(color == "white"){
//			cell.setStyle("-fx-background-color: " + color + ";");
//		} else {
//			cell.setStyle("-fx-background-color: " + color + ";");
//		}
		//cell.setStyle("-fx-background-color: " + color + ";");
		//cell.setStyle("-fx-text-background-color: " + color + ";");
		//cell.setStyle("-fx-text-fill: " + color + ";");
		cell.setStyle("-fx-background: " + color + ";");
	}
	
	/**
	 * 
	 */
	private void loadTransactionsToClassify(){
		
		List<Transaction> loadedTransactionList = TransactionService.findAllToClassify();
		
		ObservableList<Transaction> items = FXCollections.observableArrayList();
		
		for(Transaction transaction : loadedTransactionList){
			items.add(transaction);
		}
		transactionList.setItems(items);
		
		updateTransactionHeader();
	}
	
	private void updateTransactionHeader(){
		nbTransactionLabel.setText(MessageFormat.format("Transactions à classer ({0})", transactionList.getItems().size()));
	}

	/**
	 * 
	 */
	private void loadSubCategories(){
		
		List<SubCategory> loadedSubCategoryList = SubCategoryService.findAll();
		
		ObservableList<SubCategory> items = FXCollections.observableArrayList();
		
		for(SubCategory subCategory : loadedSubCategoryList){
			items.add(subCategory);
		}
		subCategoryList.setItems(items);
	}
}
