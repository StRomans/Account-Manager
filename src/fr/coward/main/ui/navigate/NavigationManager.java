package fr.coward.main.ui.navigate;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import fr.coward.main.Main;
import fr.coward.main.model.Session;
import fr.coward.main.ui.utils.FXMLUtil;

public class NavigationManager implements Initializable {

	@FXML
    private VBox navigationBox;
	@FXML
	public StackPane dynamicContentPane;
	
	@FXML 
	private Object activeContentController;
	
	private Hyperlink currentMenu;
	
	private static Parent currentScreen;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		for(Node menuCategory : navigationBox.getChildren()){
			
			if(menuCategory instanceof TitledPane){
				
				Node node = ((TitledPane) menuCategory).getContent();
				
				if(node instanceof GridPane){
					GridPane pane = (GridPane) node;
					
					for(Node menuLink : pane.getChildren()){
						
						if(menuLink instanceof Hyperlink){
							((ButtonBase) menuLink).setOnAction(new EventHandler<ActionEvent>() {

								@Override
								public void handle(ActionEvent event) {
									
									try {
										setCurrentMenu((Hyperlink) menuLink);
										
										String fxmlElementId = ((Node)event.getSource()).idProperty().get();
										String fxmlResourceName = Menu.valueOf(fxmlElementId).getRegisteredFXML();
										
										navigateTo(fxmlResourceName);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
						}
					}
				}
			}
		}
		
		Session.getInstance().addTransactionPropertyChangeListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				updateClassifyMenuLabel();
			}

			
		});
		
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				updateClassifyMenuLabel();
				
				if(Session.getInstance().getUnclassifiedTransactionCountProperty().get() > 0){
					goToMenu(Menu.Classify);
				}
				else {
					goToMenu(Menu.CreateEntry);
				}
				
			}
		});
		
	}
	
	public static Parent getActiveScreen(){
		return currentScreen;
	}
	
	private void updateClassifyMenuLabel(){
		
		String regularLabel = "Classer les nouvelles entrées";
		
		Hyperlink menu = Main.getMenuById(Menu.Classify.name());
		menu.setText(regularLabel + " (" + Session.getInstance().getUnclassifiedTransactionCountProperty().get() + ")");
	}
	
	/**
	 * Position l'interface sur ce menu
	 * @param menuEnum
	 */
	public static void goToMenu(Menu menuEnum){
		
		Hyperlink menu = Main.getMenuById(menuEnum.name());
		menu.fire();
	}
	
	/**
	 * Mémorise menu courant
	 * @param hyperlink
	 */
	private void setCurrentMenu(Hyperlink hyperlink){
		if(null != currentMenu){
			currentMenu.setVisited(false);
		}
		currentMenu = hyperlink;
	}
	
	public static void navigateTo(String fxmlResourceName){
		
		currentScreen = FXMLUtil.getFXMLParent(fxmlResourceName);
		
		Main.setDynamicContentPane(currentScreen);
	}
	
	public enum Menu {
		CreateAccount("ui/navigate/alimentation/CreateAccount.fxml"),
		CreateEntry("ui/navigate/alimentation/CreateEntry.fxml"),
		ImportFile("ui/navigate/alimentation/ImportFile.fxml"),
		
		CreateCategory("ui/navigate/classification/CreateCategory.fxml"),
		CreateSubCategory("ui/navigate/classification/CreateSubCategory.fxml"),
		Classify("ui/navigate/classification/Classify.fxml"),
		UpdateClassification("ui/navigate/classification/UpdateClassification.fxml"),
		ClassificationRules("ui/navigate/classification/ClassificationRules.fxml"),
		
		CategoryPercentageStatistics("ui/navigate/visualisation/CategoryPercentageStatistics.fxml"),
		SubCategoryPercentageStatistics("ui/navigate/visualisation/SubCategoryPercentageStatistics.fxml"),
		CategoryEvolutionStatistics("ui/navigate/visualisation/CategoryEvolutionStatistics.fxml"),
		SubCategoryEvolutionStatistics("ui/navigate/visualisation/SubCategoryEvolutionStatistics.fxml"),
		BalanceStatistics("ui/navigate/visualisation/BalanceStatistics.fxml"),
		Dashboard("ui/navigate/visualisation/Dashboard.fxml"),
		
		Settings("ui/navigate/configuration/Settings.fxml");
		
		private String registeredFXML;
		
		Menu(String registeredFXML){
			this.registeredFXML = registeredFXML;
		}
		
		public String getRegisteredFXML(){
			return registeredFXML;
		}
	}

}
