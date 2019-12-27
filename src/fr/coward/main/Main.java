package fr.coward.main;
	
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import fr.coward.main.model.Session;
import fr.coward.main.ui.utils.FXMLUtil;


public class Main extends Application {
	
	// TODO : Ajouter l'édition de Compte (DeletableListView)
	// TODO : Etudier l'intérêt d'une différentiation des dépenses/revenus sur les statistiques. Certains revenus sont des remboursements, d'autres pas.
	// TODO : Amélioration graphique : CSS des boutons, listes, combobox etc.
	// TODO : Mettre en place des traitements multi-threadés sur les chargements de données
	
	private static final String STAGE_TITLE = "Gestion de comptes v1.8.4 © R.DIENY";
	private static final String STAGE_FXML = "ui/Main.fxml";
	
	private static Scene scene;
	
	@Override
	public void start(Stage mainStage) {
		try {			
			
			Parent root = FXMLUtil.getFXMLParent(STAGE_FXML);
		    
	        scene = new Scene(root);	        
	        mainStage.setTitle(STAGE_TITLE);
	        mainStage.setScene(scene);
	        mainStage.getIcons().add(new Image(getClass().getResourceAsStream("/fr/coward/main/resources/icons/app_icon.png")));
	        mainStage.setResizable(false);
	        //mainStage.setFullScreen(true);
	        mainStage.sizeToScene();
	        mainStage.show();
	        
	        
	        /* Gestion du thème */
	        scene.getStylesheets().addAll(getStyleSheets());
	        
//	        ScenicView.show(scene);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		try {		
		    System.setOut(new PrintStream(new File("AccountManager-stdout.log")));
		    System.setErr(new PrintStream(new File("AccountManager-stderr.log")));
		} catch (Exception e) {
		     e.printStackTrace();
		}
		
		launch(args);
	}
	
	public static void setDynamicContentPane(Node element){
		
		Pane dynamicContentPane = getRootPane();
		
		dynamicContentPane.getChildren().clear();
		dynamicContentPane.getChildren().add(element);
	}
	
	public static Hyperlink getMenuById(String menuId){
		Node navigationBox = scene.lookup("#navigationBox");
		
		Node menu = ((VBox) navigationBox).lookup("#"+menuId);
		
		return (Hyperlink) menu;
	}
	
	public static Pane getRootPane(){
		
		Node dynamicContentNode = scene.lookup("#dynamicContentPane");
		
		return (Pane) dynamicContentNode;
	}
	
	public static Collection<String> getStyleSheets(){
		
		List<String> styleSheets = new ArrayList<>();
		styleSheets.add(Main.class.getResource("application.css").toExternalForm());
		styleSheets.add(Main.class.getResource("theme/" + Session.getInstance().getThemeProperty().getValue()).toExternalForm());
	
		return styleSheets;
	}
}
