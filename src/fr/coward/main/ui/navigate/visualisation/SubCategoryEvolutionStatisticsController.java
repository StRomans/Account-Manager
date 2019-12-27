package fr.coward.main.ui.navigate.visualisation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import javafx.scene.chart.XYChart.Series;

import com.jfoenix.controls.JFXComboBox;

import fr.coward.main.model.Account;
import fr.coward.main.model.Category;
import fr.coward.main.model.Transaction;
import fr.coward.main.model.services.AccountService;
import fr.coward.main.model.services.TransactionService;
import fr.coward.main.model.utils.Period;
import fr.coward.main.ui.components.CategorySelector;
import fr.coward.main.ui.components.HoveredNode;
import fr.coward.main.ui.components.PeriodSelector;

public class SubCategoryEvolutionStatisticsController implements Initializable {
	
	@FXML
	private JFXComboBox<Account> accountComboBox;
	@FXML
	private LineChart<String, Double> subCategoryLineChart;
	
	@FXML
	private PeriodSelector periodSelector;
	@FXML
	private CategorySelector categorySelector;
	
	private List<Transaction> classifiedTransactions;
	
	private Currency currency;
	
	/* (non-Javadoc)
	 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		refreshAccountList();
		
		periodSelector.setItems(Period.YEAR, Period.ALL);
		
		moveSelectorToNewestPeriodWithData(periodSelector, accountComboBox.getSelectionModel().getSelectedItem());

		periodSelector.addEventHandler(PeriodSelector.CHANGED_VALUE, new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				loadData(periodSelector.getSelectedPeriod(), periodSelector.getPeriodStartDate(), periodSelector.getPeriodEndDate(), categorySelector.getSelectedCategory());
			}
		});
		
		categorySelector.addEventHandler(CategorySelector.CHANGED_VALUE, new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				loadData(periodSelector.getSelectedPeriod(), periodSelector.getPeriodStartDate(), periodSelector.getPeriodEndDate(), categorySelector.getSelectedCategory());
			}
		});

		accountComboBox.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				loadData(periodSelector.getSelectedPeriod(), periodSelector.getPeriodStartDate(), periodSelector.getPeriodEndDate(), categorySelector.getSelectedCategory());
			}
		});
		
		loadData(periodSelector.getSelectedPeriod(), periodSelector.getPeriodStartDate(), periodSelector.getPeriodEndDate(), categorySelector.getSelectedCategory());
	}
	
	private void loadData(Period periodSelected, Date periodStartDate, Date periodEndDate, Category category){
		
		Date startDate = null;
		Date endDate = null;
		Period period = null;
		
		if(Period.YEAR.equals(periodSelected)){
			
			// Afficher l'évolution par catégorie selon les mois (pour l'année en cours)			
			startDate = periodStartDate;
			endDate = periodEndDate;
			period = Period.MONTH;
			
		}
		else {
			// Afficher l'évolution par catégorie selon les années
			startDate = null;
			endDate = null;
			period = Period.YEAR;
			periodSelector.hidePeriodValueControllers();
		}
		
		classifiedTransactions = TransactionService.findAllSpendingsClassifiedByAccountAndCategoryForPeriodGroupByPeriod(accountComboBox.getSelectionModel().getSelectedItem(), period, startDate, endDate, category);
		
		Account account = AccountService.findById(accountComboBox.getSelectionModel().getSelectedItem().getId());
		
		currency = Currency.getInstance(account.getCurrency());
		
		populateCategoryLineChart();
	}	
	
	
	/**
	 * 
	 */
	private void populateCategoryLineChart(){
		
		Date minimalDate = null;
		Date maximalDate = null;
		
		Map<String,Map<Date, Double>> subCategoryDateTransactionMap = new HashMap<>();
		for(Transaction cumulatedTransaction : classifiedTransactions){
			
			
			String subCategoryLabel = cumulatedTransaction.getSubCategory().toString();
			if(!subCategoryDateTransactionMap.containsKey(subCategoryLabel)){
				subCategoryDateTransactionMap.put(subCategoryLabel, new HashMap<>());
			} 
			
			Date transactionDate = cumulatedTransaction.getDate();
			if(!subCategoryDateTransactionMap.get(subCategoryLabel).containsKey(transactionDate)){
				subCategoryDateTransactionMap.get(subCategoryLabel).put(transactionDate, Math.abs(cumulatedTransaction.getAmount()));
			}
			
			if(null == minimalDate || transactionDate.before(minimalDate)){
				minimalDate = transactionDate;
			}
			if(null == maximalDate || transactionDate.after(maximalDate)){
				maximalDate = transactionDate;
			}
		}
		
		ObservableList<Series<String, Double>> lineChartData = FXCollections.observableArrayList();
		GregorianCalendar minimalDateCalendar = new GregorianCalendar();
		minimalDateCalendar.setTimeInMillis(minimalDate.getTime());
		GregorianCalendar maximalDateCalendar = new GregorianCalendar();
		maximalDateCalendar.setTimeInMillis(maximalDate.getTime());
		
		
		for(String subCategoryLabel : subCategoryDateTransactionMap.keySet()){
			
			XYChart.Series<String, Double> categoryExpenses = new XYChart.Series<String, Double>();
			categoryExpenses.setName(subCategoryLabel);
			

			if(Period.YEAR.equals(periodSelector.getSelectedPeriod())){
				
				for(Month month : Month.values()){
					// find transaction
					GregorianCalendar dateToLookFor =  new GregorianCalendar();
					dateToLookFor.setTime(minimalDate);
					dateToLookFor.set(GregorianCalendar.MONTH, month.getValue()-1);
					dateToLookFor.set(GregorianCalendar.DAY_OF_MONTH, 1);
					
					Double transactionValue = subCategoryDateTransactionMap.get(subCategoryLabel).get(dateToLookFor.getTime());
					transactionValue = (null == transactionValue) ? 0d : transactionValue;
					
					categoryExpenses.getData().add(new XYChart.Data<String, Double>(month.getDisplayName(TextStyle.FULL, Locale.getDefault()), transactionValue));
				}
				
			} else {

				for(int year=minimalDateCalendar.get(GregorianCalendar.YEAR); year <= maximalDateCalendar.get(GregorianCalendar.YEAR); year++){
					// find transaction
					GregorianCalendar dateToLookFor =  new GregorianCalendar();
					dateToLookFor.setTime(minimalDate);
					dateToLookFor.set(GregorianCalendar.YEAR, year);
					dateToLookFor.set(GregorianCalendar.MONTH, 0);
					dateToLookFor.set(GregorianCalendar.DAY_OF_MONTH, 1);
					
					Double transactionValue = subCategoryDateTransactionMap.get(subCategoryLabel).get(dateToLookFor.getTime());
					transactionValue = (null == transactionValue) ? 0d : transactionValue;
					
					categoryExpenses.getData().add(new XYChart.Data<String, Double>(String.valueOf(year), transactionValue));
				}
			}
		
			lineChartData.add(categoryExpenses);
		}
		
		addHoveredValue(lineChartData);
		subCategoryLineChart.setData(lineChartData);
	}
	
	private void addHoveredValue(ObservableList<Series<String, Double>> chartData){
		
		for (int index = 0; index<chartData.size(); index++){
			
			Series<String, Double> serie = chartData.get(index);
			
			for(XYChart.Data<String, Double> data : serie.getData()){
				
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
