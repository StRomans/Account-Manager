package fr.coward.main.ui.navigate.alimentation;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import fr.coward.main.loader.AllowedExtension;
import fr.coward.main.loader.FileLoader;
import fr.coward.main.loader.FileOFC;
import fr.coward.main.loader.FileOFX;
import fr.coward.main.model.Account;
import fr.coward.main.model.Transaction;
import fr.coward.main.model.rule.RuleEngine;
import fr.coward.main.model.services.AccountService;
import fr.coward.main.model.services.TransactionService;
import fr.coward.main.model.utils.FileUtils;
import fr.coward.main.ui.service.TimerService;

public class ImportFileController implements Initializable {
	
	@FXML
	private JFXButton pickFileButton;
	@FXML
	private JFXButton resetFileButton;
	@FXML
	private JFXComboBox<Account> transactionAccountComboBox;
	@FXML
	private Label selectedFileLabel;
	@FXML
	private JFXButton importFileButton;
	
	@FXML
	private Label messageLabel;
	
	private File pickedFile;
	private int selectedFileLabelInitialRowPos;
	private int selectedFileLabelInitialIndexPos;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		pickedFile = null;
		selectedFileLabelInitialIndexPos = GridPane.getColumnIndex(selectedFileLabel);
		selectedFileLabelInitialRowPos = GridPane.getRowIndex(selectedFileLabel);
		
		refreshAccountList();
		
	}
	
	public void pickFileButton_onActionHandler(ActionEvent event){
		pickedFile = pickFile();
		refreshFile();
	}
	
	public void resetFileButton_onActionHandler(ActionEvent event){
		pickedFile = null;
		refreshFile();
	}
	
	public void importFileButton_onActionHandler(ActionEvent event){
		importFile(pickedFile);
		
		pickedFile = null;
		refreshFile();
	}
	
	/**
	 * 
	 */
	private void refreshAccountList(){
		
		List<Account> loadedAccountList = AccountService.findAll();
		
		ObservableList<Account> items = FXCollections.observableArrayList();
		for(Account account : loadedAccountList){
			items.add(account);
		}
		transactionAccountComboBox.setItems(items);
		
		// sélectionner le compte si unique
		if(items.size() == 1){
			transactionAccountComboBox.getSelectionModel().select(items.get(0));
		}
	}
	
	private void importFile(File file){			
        try 
        {
        	
        	if (file == null) {
        		throw new Exception("Veuillez sélectionner un fichier à importer.");
        	}
        	if(null == transactionAccountComboBox.getSelectionModel().getSelectedItem()){
        		throw new Exception("Veuillez sélectionner un compte.");
			}
        	
        	FileLoader moneyFile = null;
        	if(FileUtils.getExtension(file.getName()).equalsIgnoreCase(AllowedExtension.OFC.name())){
        		moneyFile = new FileOFC(file.getAbsolutePath());
        	}
        	else if (FileUtils.getExtension(file.getName()).equalsIgnoreCase(AllowedExtension.OFX.name())){
        		moneyFile = new FileOFX(file.getAbsolutePath());
        	}

			
			Account selectedAccount = transactionAccountComboBox.getSelectionModel().getSelectedItem();
			
			List<Transaction> transactions = moneyFile.parse();
			
			int nbDoublons = 0;
			for(Transaction transaction : transactions){
				transaction.setAccount(selectedAccount);
				TransactionService.save(transaction);
				if(transaction.getId() == 0){
					nbDoublons++;
				}
			}
			
			// Tenter classification automatique
			RuleEngine.classify(transactions);
			
			String message = "Fichier importé avec succès.";
			if(nbDoublons > 0){
				message += " (" + nbDoublons + " transactions en doublon ignorées)";
			}
			resetFileButton.fire();
			importFileButton.setVisible(true);
			setMessage(message);
			
			
		} catch (Exception e) {
			setMessage(e.getMessage());
			e.printStackTrace();
		} finally {
			importFileButton.setVisible(true);
		}
	}
	
	private File pickFile(){
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Sélectionner un fichier à importer");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		
		for(AllowedExtension extension : AllowedExtension.values()){
			fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Fichier " + extension.name(), "*." + extension.name()));
		}
		
		return fileChooser.showOpenDialog(new Stage());
	}
	
	private void refreshFile(){
		
		pickFileButton.setVisible(null == pickedFile);
		importFileButton.setVisible(null != pickedFile);
		resetFileButton.setVisible(null != pickedFile);
		
		
		if(null == pickedFile){
			selectedFileLabel.setText("Aucun fichier sélectionné.");
			
			GridPane.setColumnIndex(selectedFileLabel, selectedFileLabelInitialIndexPos);
			GridPane.setRowIndex(selectedFileLabel, selectedFileLabelInitialRowPos);
			
		} else {
			selectedFileLabel.setText(pickedFile.getName());
			
			GridPane.setColumnIndex(selectedFileLabel, GridPane.getColumnIndex(pickFileButton));
			GridPane.setRowIndex(selectedFileLabel, GridPane.getRowIndex(pickFileButton));
			
		}
	}
	
	/**
	 * @param errorMsg
	 */
	private void setMessage(String errorMsg){
		messageLabel.setText(errorMsg);
		
		TimerService timer = new TimerService(10000);
        timer.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
            	eraseErrorMsg();
            }
        });
         
        timer.start();
	}
	
	
	/**
	 * 
	 */
	private void eraseErrorMsg(){
		setMessage("");
	}
}
