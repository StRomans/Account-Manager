package fr.coward.main.ui.navigate.visualisation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;

import com.jfoenix.controls.JFXComboBox;

import fr.coward.main.model.Account;
import fr.coward.main.model.Transaction;
import fr.coward.main.model.services.AccountService;
import fr.coward.main.model.services.TransactionService;
import fr.coward.main.model.utils.Period;
import fr.coward.main.ui.components.HoveredNode;
import fr.coward.main.ui.components.PeriodSelector;

public class BalanceStatisticsController implements Initializable {
	
	@FXML
	private JFXComboBox<Account> accountComboBox;
	
	@FXML
	private PeriodSelector periodSelector;
	
	@FXML
	private Label totalSavingsLabel;
	@FXML
	private LineChart<String, Double> balanceLineChart;
	
	private List<Transaction> transactions;
	
	private Currency currency;
	
	/* (non-Javadoc)
	 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		refreshAccountList();
		
		periodSelector.setItems(Period.YEAR);
		
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
		
		transactions = TransactionService.findAllByAccount(accountComboBox.getSelectionModel().getSelectedItem(), startDate, endDate);
		
		Account account = AccountService.findById(accountComboBox.getSelectionModel().getSelectedItem().getId());
		
		currency = Currency.getInstance(account.getCurrency());
		
		populateChart();
	}	
	
	
	/**
	 * 
	 */
	private void populateChart(){
		
		ObservableList<Series<String, Double>> lineChartData = FXCollections.observableArrayList();
		
		XYChart.Series<String, Double> expenses = new XYChart.Series<String, Double>();
		expenses.setName("Dépenses");
		
		XYChart.Series<String, Double> incomes = new XYChart.Series<String, Double>();
		incomes.setName("Revenus");
		
		XYChart.Series<String, Double> savings = new XYChart.Series<String, Double>();
		savings.setName("Economies");
		
		for(Month month : Month.values()){
			expenses.getData().add(new XYChart.Data<String, Double>(month.getDisplayName(TextStyle.FULL, Locale.getDefault()), 0d));
			incomes.getData().add(new XYChart.Data<String, Double>(month.getDisplayName(TextStyle.FULL, Locale.getDefault()), 0d));
			savings.getData().add(new XYChart.Data<String, Double>(month.getDisplayName(TextStyle.FULL, Locale.getDefault()), 0d));
		}
		
		for(Transaction transaction : transactions){
			
			GregorianCalendar transactionCalendar = new GregorianCalendar();
			transactionCalendar.setTime(transaction.getDate());
			Month transactionMonth = Month.of(transactionCalendar.get(GregorianCalendar.MONTH)+1);
			
			// Revenu
			if(transaction.getAmount() > 0){
				
				int incomesIndex = transactionMonth.getValue()-1;
				
				BigDecimal total = new BigDecimal(incomes.getData().get(incomesIndex).getYValue()).add(new BigDecimal(Math.abs(transaction.getAmount())));
				incomes.getData().get(incomesIndex).setYValue(total.doubleValue());
			} 
			// Dépense
			else if (transaction.getAmount() < 0) {
				int expensesIndex = transactionMonth.getValue()-1;
				
				BigDecimal total = new BigDecimal(expenses.getData().get(expensesIndex).getYValue()).add(new BigDecimal(Math.abs(transaction.getAmount())));
				expenses.getData().get(expensesIndex).setYValue(total.doubleValue());
			}
		}
		
		BigDecimal totalSavings = BigDecimal.ZERO;
		for(Month month : Month.values()) {
			
			int index = month.getValue()-1;
			
			Double income = incomes.getData().get(index).getYValue();			
			Double expense = expenses.getData().get(index).getYValue();
			BigDecimal saving = new BigDecimal(income).subtract(new BigDecimal(expense));
			
			totalSavings = totalSavings.add(saving).setScale(3, RoundingMode.HALF_EVEN);
			
			savings.getData().get(index).setYValue(saving.doubleValue());
		}


		lineChartData.add(expenses);
		lineChartData.add(incomes);
		lineChartData.add(savings);
		
		addHoveredValue(lineChartData);
		balanceLineChart.setData(lineChartData);
		
		
		totalSavingsLabel.setText(String.valueOf(totalSavings.doubleValue()) + " " + currency);

	}
	
	private void addHoveredValue(ObservableList<Series<String, Double>> chartData){
		
		for (int index = 0; index<chartData.size(); index++){
			
			Series<String, Double> serie = chartData.get(index);
			
			for(Data<String, Double> data : serie.getData()){
				
				data.setNode(new HoveredNode(new BigDecimal(data.getYValue()).setScale(3, RoundingMode.HALF_UP).doubleValue(), currency.getSymbol(), index));
			}
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
