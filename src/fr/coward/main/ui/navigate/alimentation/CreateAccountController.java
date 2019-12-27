package fr.coward.main.ui.navigate.alimentation;

import java.net.URL;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;

import fr.coward.main.model.Account;
import fr.coward.main.model.services.AccountService;
import fr.coward.main.ui.navigate.CreateFormController;
import fr.coward.main.ui.utils.StringUtil;

public class CreateAccountController extends CreateFormController {
	
	@FXML
	private JFXTextField accountLabelTextField;
	@FXML
	private JFXComboBox<String> accountCurrencyComboBox;
	@FXML
	private ListView<Account> accountList;
	@FXML
	private JFXButton saveButton;
	
	private Account currentAccount;

	
	/* (non-Javadoc)
	 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		super.initialize(location, resources);
	
	}
	
	public void saveButton_onActionHandler(ActionEvent event){
		save();
	}
	
	/**
	 * 
	 */
	private void refreshList(){
		
		List<Account> loadedAccountList = AccountService.findAll();
		
		if(loadedAccountList.isEmpty()){
			loadedAccountList.add(new Account("Aucun enregistrement trouvé."));
		}
		
		ObservableList<Account> items = FXCollections.observableArrayList();
		for(Account account : loadedAccountList){
			items.add(account);
		}
		accountList.setItems(items);
	}


	@Override
	public void initComponents(boolean isFirstInit) {
		
		currentAccount = null;

		accountLabelTextField.textProperty().setValue("");
		
		ObservableList<String> items = FXCollections.observableArrayList();
		for(Currency currency : Currency.getAvailableCurrencies()){
			items.add(currency.getCurrencyCode());
		}
		
		Collections.sort(items);
		
		accountCurrencyComboBox.setItems(items);
		
		focus(accountLabelTextField);
		
		refreshList();
		
	}
	
	public void accountList_onClickHandler(MouseEvent event){
		Account selectedItem = this.accountList.getSelectionModel().getSelectedItem();
		this.initFormFor(selectedItem);
	}
	
	public void initFormFor(Account account){
		
		currentAccount = account;
		
		if(null != account){
			this.accountLabelTextField.setText(account.getLabel());
			this.accountCurrencyComboBox.getSelectionModel().select(account.getCurrency());
		} else {
			initComponents(false);
		}
	}

	@Override
	public void validate() throws Exception {

		if(!StringUtil.isNotNullNotEmpty(accountLabelTextField.textProperty().getValue().trim())){
			throw new Exception("Veuillez saisir un libellé.");
		}
		if(accountCurrencyComboBox.getSelectionModel().getSelectedItem() == null){
			throw new Exception("Veuillez selectionner la devise du compte.");
		}
	}

	@Override
	public void doSave() throws Exception {
		
		if(null == currentAccount){
			currentAccount = new Account();
		}
		
		currentAccount.setCurrency(accountCurrencyComboBox.getSelectionModel().getSelectedItem());
		currentAccount.setLabel(accountLabelTextField.textProperty().getValue().trim());
		
		boolean success = AccountService.save(currentAccount);
		
		// Si compte ajouté
		if (success){
			if(currentAccount.getId() != 0){
				
				initComponents(false);
			} 
		}else {
			throw new Exception("Un compte existe déjà pour ce libellé. Veuillez saisir un autre libellé.");
		}
	}
}
