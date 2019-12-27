package fr.coward.main.ui.components;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;

import fr.coward.main.model.Account;
import fr.coward.main.model.MenuContext;
import fr.coward.main.model.SubCategory;
import fr.coward.main.model.DTO.TransactionViewDTO;
import fr.coward.main.model.services.AccountService;
import fr.coward.main.model.services.SubCategoryService;
import fr.coward.main.model.services.TransactionService;
import fr.coward.main.ui.navigate.CreateFormController;
import fr.coward.main.ui.utils.DateUtil;
import fr.coward.main.ui.utils.StringUtil;

public class TransactionEditPaneController extends CreateFormController  {
	
	public static String TRANSACTION_PARAM = "transactionParam";
	
	@FXML
	GridPane contentBox;
	
	@FXML
	private JFXComboBox<Account> accountComboBox;
	@FXML
	private JFXDatePicker transactionDatePicker;
	@FXML
	private NumericField transactionAmountNumericField;
	@FXML
	private JFXCheckBox isRevenuCheckBox;
	@FXML
	private JFXTextField transactionLabelTextField;
	@FXML
	private JFXComboBox<SubCategory> subCategoryComboBox;
	@FXML
	private JFXButton clearSubCategoryButton;
	
	private TransactionViewDTO transactionDto;
	
	/**
	 * Complète le formulaire à partir de la transaction fournie
	 * @param transactionDto la {@link TransactionViewDTO} fournie
	 */
	public void initFromTransaction(TransactionViewDTO transactionDto){
		this.transactionDto = transactionDto;
		transactionDatePicker.setValue(DateUtil.date2LocalDate(transactionDto.getDate()));
		transactionAmountNumericField.setText(String.valueOf(Math.abs(transactionDto.getAmount())));
		transactionLabelTextField.setText(transactionDto.getLabel());
		isRevenuCheckBox.setSelected(transactionDto.getAmount() > 0);
		
		accountComboBox.getSelectionModel().select(transactionDto.getAccountProperty().get());
		
		subCategoryComboBox.getSelectionModel().select(transactionDto.getSubCategoryProperty().get());
	}

	/**
	 * Récupère la liste des comptes et sélectionne le premier compte de la liste
	 */
	private void loadAccountList(){
		
		List<Account> loadedAccountList = AccountService.findAll();
		
		ObservableList<Account> items = FXCollections.observableArrayList();
		for(Account account : loadedAccountList){
			items.add(account);
		}
		accountComboBox.setItems(items);
	}
	
	/**
	 * Charge les sous-catégories déclarées
	 */
	private void loadSubCategories(){
		
		List<SubCategory> loadedSubCategoryList = SubCategoryService.findAll();
		
		ObservableList<SubCategory> items = FXCollections.observableArrayList();
		
		for(SubCategory subCategory : loadedSubCategoryList){
			items.add(subCategory);
		}
		subCategoryComboBox.setItems(items);
	}
	
	/**
	 * Au clic sur le bouton de désélection de la sous-catégorie
	 * @param event
	 */
	public void clearSubCategoryButton_onActionHandler(ActionEvent event){
		subCategoryComboBox.getSelectionModel().clearSelection();
	}
	
	/**
	 * Au clic sur le bouton 'Enregistrer'
	 * @param event
	 */
	public void saveButton_onActionHandler(ActionEvent event){
		super.save();
	}
	
	/**
	 * Au clic sur le bouton 'Annuler'
	 * @param event
	 */
	public void cancelButton_onActionHandler(ActionEvent event){
		// TODO
	}
	
	

	@Override
	public void initComponents(boolean isFirstInit) {
		
		loadAccountList();
		loadSubCategories();
		
		Image deleteImg = new Image(getClass().getResourceAsStream("/fr/coward/main/resources/icons/remove_24px.png"));
		ImageView deleteImgView = new ImageView(deleteImg);
		int iconSize = 24;
		deleteImgView.setFitHeight(iconSize);
		deleteImgView.setFitWidth(iconSize);
		clearSubCategoryButton.setGraphic(deleteImgView);
		clearSubCategoryButton.setTooltip(new Tooltip("Retirer sous-catégorie"));
		
		TransactionViewDTO transactionDto = (TransactionViewDTO) MenuContext.getInstance().getInitObj(TRANSACTION_PARAM);
		if(null != transactionDto){
			this.initFromTransaction(transactionDto);
		}
		
		focus(accountComboBox);
	}
	
	@Override
	public void validate() throws Exception {
		if(accountComboBox.getSelectionModel().getSelectedItem() == null){
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
	
	@Override
	public void doSave() throws Exception {
		
		this.transactionDto.setDate(DateUtil.localDate2Date(transactionDatePicker.getValue()));
		double transactionAmount = transactionAmountNumericField.getDouble();
		transactionAmount = (isRevenuCheckBox.isSelected())? transactionAmount : -transactionAmount;
		this.transactionDto.setAmount(transactionAmount);
		this.transactionDto.setLabel(transactionLabelTextField.getText());
		
		Account selectedAccount = accountComboBox.getSelectionModel().getSelectedItem();
		this.transactionDto.setAccount(selectedAccount);
		
		TransactionService.save(transactionDto.toTransaction());
		
		SubCategory selectedSubCategory = subCategoryComboBox.getSelectionModel().getSelectedItem();
		// Si l'utilisateur a effectué une modification de la sous-catégorie
		if(transactionDto.getSubCategoryProperty().get() != selectedSubCategory){
			if(null != selectedSubCategory){
				transactionDto.classify(selectedSubCategory);
			} else {
				transactionDto.unclassify();
			}
		}
	}
	
	public TransactionViewDTO getTransaction() {
				
		return this.transactionDto;
	}
}
