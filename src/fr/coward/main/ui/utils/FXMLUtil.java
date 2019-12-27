package fr.coward.main.ui.utils;

import java.io.IOException;

import fr.coward.main.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class FXMLUtil {
	
	public static FXMLLoader getLoader(String fxmlResourceName){
		
		return new FXMLLoader(Main.class.getResource(fxmlResourceName));
	}
	
	public static Parent getFXMLParent(FXMLLoader loader){
		
		Parent parent = null;
		
		try {
			parent = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return parent;
	}

	public static Parent getFXMLParent(String fxmlResourceName){
		
		Parent parent = null;
		
		try {
			parent = FXMLLoader.load(Main.class.getResource(fxmlResourceName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return parent;
	}
	
	public static <T> T getController(String fxmlResourceName){
		
		FXMLLoader loader = null;
		
		loader = new FXMLLoader(Main.class.getResource(fxmlResourceName));
		
		return loader.<T>getController();
	}
	
	public static <T> T getController(FXMLLoader loader){
		
		return loader.<T>getController();
	}
}
