package fr.coward.main.ui.navigate.alimentation;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;

import fr.coward.main.model.Account;
import fr.coward.main.model.Transaction;
import fr.coward.main.model.rule.RuleEngine;
import fr.coward.main.model.services.AccountService;
import fr.coward.main.model.services.TransactionService;
import fr.coward.main.ui.components.NumericField;
import fr.coward.main.ui.navigate.CreateFormController;
import fr.coward.main.ui.utils.StringUtil;

public class CreateEntryController extends CreateFormController {
	
	@FXML
	private JFXDatePicker transactionDatePicker;
	@FXML
	private NumericField transactionAmountNumericField;
	@FXML
	private JFXCheckBox isRevenuCheckBox;
	@FXML
	private JFXTextField transactionLabelTextField;
	@FXML
	private JFXComboBox<Account> transactionAccountComboBox;
	
	@FXML
	private JFXButton saveButton;

	
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
	 * Charge la liste des comptes
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
			focus(transactionAmountNumericField);
		} else {
			focus(transactionAccountComboBox);
		}
	}

	/* (non-Javadoc)
	 * @see fr.coward.main.ui.navigate.ICreateForm#initComponents()
	 */
	@Override
	public void initComponents(boolean isFirstInit) {
		
		if(isFirstInit){
			// Charger la combobox
			refreshAccountList();
		} else {

			focus(transactionAmountNumericField);
		}

		transactionDatePicker.setValue(LocalDate.now());
		transactionAmountNumericField.setText("");
		transactionLabelTextField.setText("");
		isRevenuCheckBox.setSelected(false);
	}

	/* (non-Javadoc)
	 * @see fr.coward.main.ui.navigate.CreateFormController#validate()
	 */
	@Override
	public void validate() throws Exception {
		
		if(transactionAccountComboBox.getSelectionModel().getSelectedItem() == null){
			throw new Exception("Veuillez selectionner le compte sur lequel enregistrer la transaction.");
		}
		if(null == transactionDatePicker.getValue()){
			throw new Exception("Veuillez renseigner la date de la transaction.");
		}
		if(!StringUtil.isNotNullNotEmpty(transactionAmountNumericField.getText())){
			throw new Exception("Veuillez renseigner le montant de la transaction.");
		}
		if(!transactionAmountNumericField.isValid()){
			throw new Exception("Montant invalide.");
		}		

	}

	/* (non-Javadoc)
	 * @see fr.coward.main.ui.navigate.CreateFormController#doSave()
	 */
	public void doSave() throws Exception {
			
		// Enregistrer la transaction
		Account selectedAccount = transactionAccountComboBox.getSelectionModel().getSelectedItem();
		Date transactionDate = Date.valueOf(transactionDatePicker.getValue().toString());
		double transactionAmount = transactionAmountNumericField.getDouble();
		transactionAmount = (isRevenuCheckBox.isSelected())? transactionAmount : -transactionAmount;
		String transactionLabel = transactionLabelTextField.getText();
		
		Transaction transaction = new Transaction();
		transaction.setAccount(selectedAccount);
		transaction.setDate(transactionDate);
		transaction.setAmount(transactionAmount);
		transaction.setLabel(transactionLabel);
		
		TransactionService.save(transaction);
		if(transaction.getId() != 0){
			// Feedback utilisateur sur le succès
			setMessage("La transaction a bien été enregistrée.");
			// Vider les champs sauf le compte
			initComponents(false);
			
			// Tenter une classification automatique
			RuleEngine.classify(Arrays.asList(transaction));
			
		} else {
			// Feedback utilisateur sur l'échec de son action
			throw new Exception("Cette transaction est déjà enregistrée pour ce compte. S'il ne s'agit pas de la même transaction, veuillez modifier le libellé.");
		}

	}
}
