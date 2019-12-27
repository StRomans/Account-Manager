package fr.coward.main.ui.navigate.visualisation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import com.jfoenix.controls.JFXComboBox;

import fr.coward.main.model.Account;
import fr.coward.main.model.Transaction;
import fr.coward.main.model.services.AccountService;
import fr.coward.main.model.services.TransactionService;
import fr.coward.main.model.utils.Period;
import fr.coward.main.ui.components.PeriodSelector;

public class DashboardController implements Initializable {
	
	@FXML
	private JFXComboBox<Account> accountComboBox;
	
	@FXML
	private PeriodSelector periodSelector;
	
	@FXML
	private Label spendingsVariationPeriodLabel;
	@FXML
	private Label spendingsVariationIndicatorLabel;
	
	@FXML
	private Label incomesVariationPeriodLabel;
	@FXML
	private Label incomesVariationIndicatorLabel;
	
	@FXML
	private Label savingsVariationPeriodLabel;
	@FXML
	private Label savingsVariationIndicatorLabel;
	
	/* (non-Javadoc)
	 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		refreshAccountList();
		
		periodSelector.setItems(Period.MONTH, Period.YEAR);
		
		moveSelectorToNewestPeriodWithData(periodSelector, accountComboBox.getSelectionModel().getSelectedItem());

		periodSelector.addEventHandler(PeriodSelector.CHANGED_VALUE, new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				loadData(periodSelector.getPeriodStartDate(), periodSelector.getPeriodEndDate());
			}
		});

		accountComboBox.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				loadData(periodSelector.getPeriodStartDate(), periodSelector.getPeriodEndDate());
			}
		});
		
		loadData(periodSelector.getPeriodStartDate(), periodSelector.getPeriodEndDate());
	}
	
	private void loadData(Date startDate, Date endDate){
	
		//transactions = TransactionService.findAllByAccount(accountComboBox.getSelectionModel().getSelectedItem(), startDate, endDate);	
		
		// calcul / affichage de l'indicateur
		this.processVariations(startDate, endDate);
	}
	
	private void processVariations(Date startDate, Date endDate){
		
		PeriodSelector oldPeriodSelector = new PeriodSelector();
		oldPeriodSelector.setValue(periodSelector.getSelectedPeriod(), startDate);
		oldPeriodSelector.goToPreviousPeriod();
		
		List<Transaction> oldPeriodTransactions = TransactionService.findAllByAccount(accountComboBox.getSelectionModel().getSelectedItem(), oldPeriodSelector.getPeriodStartDate(), oldPeriodSelector.getPeriodEndDate());
		List<Transaction> currentPeriodTransactions = TransactionService.findAllByAccount(accountComboBox.getSelectionModel().getSelectedItem(), startDate, endDate);
		
		// Affichage des variations de dépenses
		BigDecimal oldPeriodSpendings = Transaction.sumSpendings(oldPeriodTransactions);
		BigDecimal currentPeriodSpendings = Transaction.sumSpendings(currentPeriodTransactions);
		
		spendingsVariationPeriodLabel.setText(oldPeriodSelector.getPeriodLabel() + " : ");
		if(!BigDecimal.ZERO.equals(oldPeriodSpendings)){
			BigDecimal spendingsVariationRatio = currentPeriodSpendings.divide(oldPeriodSpendings, 3, RoundingMode.HALF_EVEN).subtract(BigDecimal.ONE).multiply(BigDecimal.valueOf(100));
			setLabelPercentage(spendingsVariationIndicatorLabel, spendingsVariationRatio.doubleValue());
		} else {
			setLabelPercentage(spendingsVariationIndicatorLabel, null);
		}
		
		// Affichage des variations de revenus
		BigDecimal oldPeriodIncomes = Transaction.sumIncomes(oldPeriodTransactions);
		BigDecimal currentPeriodIncomes = Transaction.sumIncomes(currentPeriodTransactions);
		
		incomesVariationPeriodLabel.setText(oldPeriodSelector.getPeriodLabel() + " : ");
		if(!BigDecimal.ZERO.equals(oldPeriodIncomes)){
			BigDecimal incomesVariationRatio = currentPeriodIncomes.divide(oldPeriodIncomes, 3, RoundingMode.HALF_EVEN).subtract(BigDecimal.ONE).multiply(BigDecimal.valueOf(100));
			setLabelPercentage(incomesVariationIndicatorLabel, incomesVariationRatio.doubleValue());
		} else {
			setLabelPercentage(incomesVariationIndicatorLabel, null);
		}
		
		// Affichage des variations de d'économies
		BigDecimal oldPeriodSavings = oldPeriodIncomes.subtract(oldPeriodSpendings);
		BigDecimal currentPeriodSavings = currentPeriodIncomes.subtract(currentPeriodSpendings);
		
		savingsVariationPeriodLabel.setText(oldPeriodSelector.getPeriodLabel() + " : ");
		if(!BigDecimal.ZERO.equals(oldPeriodSavings)){
			BigDecimal savingsVariationRatio = currentPeriodSavings.divide(oldPeriodSavings, 3, RoundingMode.HALF_EVEN).subtract(BigDecimal.ONE).multiply(BigDecimal.valueOf(100));
			setLabelPercentage(savingsVariationIndicatorLabel, savingsVariationRatio.doubleValue());
		} else {
			setLabelPercentage(savingsVariationIndicatorLabel, null);
		}
	}
	
	private void setLabelPercentage(Label label, Double value){
		
		if(null != value){
			
			if(Double.compare(value, 0) > 0){
				
				label.setText("+ " + value + " %");
				label.setStyle("-fx-text-fill: green;");
				
			} else if (Double.compare(value, 0) < 0){
				
				label.setText("- " + -value + " %");
				label.setStyle("-fx-text-fill: #cc443d;");
			} else {
				label.setText("0 %");
				label.setStyle("-fx-text-fill: black;");
			}
			
		} else {
			// non calculable
			label.setText("N/A");
			label.setStyle("-fx-text-fill: black;");
		}
	}
	
	/**
	 * Récupère la liste des comptes et sélectionne le premier compte de la liste
	 */
	private void refreshAccountList(){
		
		List<Account> loadedAccountList = AccountService.findAll();
		
		ObservableList<Account> items = FXCollections.observableArrayList();
		for(Account account : loadedAccountList){
			items.add(account);
		}
		accountComboBox.setItems(items);
		
		// sélectionner le compte si unique
		if(!items.isEmpty()){
			accountComboBox.getSelectionModel().selectFirst();
		}
	}
	
	private void moveSelectorToNewestPeriodWithData(PeriodSelector periodSelector, Account selectedAccount){
		
		Date moveToDate = TransactionService.findNewestTransactionDateByAccount(selectedAccount);
		
		if(null != moveToDate)	periodSelector.setValue(periodSelector.getSelectedPeriod(), moveToDate);
	}
}
