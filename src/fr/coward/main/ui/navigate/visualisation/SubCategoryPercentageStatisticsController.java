package fr.coward.main.ui.navigate.visualisation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import com.jfoenix.controls.JFXComboBox;

import fr.coward.main.Main;
import fr.coward.main.model.Account;
import fr.coward.main.model.Category;
import fr.coward.main.model.MenuContext;
import fr.coward.main.model.SubCategory;
import fr.coward.main.model.Transaction;
import fr.coward.main.model.services.AccountService;
import fr.coward.main.model.services.TransactionService;
import fr.coward.main.model.utils.Period;
import fr.coward.main.ui.components.CategorySelector;
import fr.coward.main.ui.components.HoveredPieData;
import fr.coward.main.ui.components.PeriodSelector;
import fr.coward.main.ui.navigate.NavigationManager;
import fr.coward.main.ui.navigate.NavigationManager.Menu;
import fr.coward.main.ui.navigate.classification.UpdateClassificationController;
import fr.coward.main.ui.service.TimerService;

public class SubCategoryPercentageStatisticsController implements Initializable {
	
	public static String CATEGORY_PARAM = "categoryParam";
	public static String PERIOD_PARAM = "periodParam";
	public static String CURRENTDATE_PARAM = "currentDateParam";
	
	@FXML
	private VBox mainBox;
	
	@FXML
	private JFXComboBox<Account> accountComboBox;
	@FXML
	private PieChart subCategoryPieChart;

	@FXML
	private PeriodSelector periodSelector;
	@FXML
	private CategorySelector categorySelector;
	
	private List<Transaction> classifiedTransactions;
	
	private Currency currency;
	
	private ContextMenu pieChartContextMenu;
	private MenuItem goToTransactionsItem;
	private Map<Data, SubCategory> reversePieChartMap;
	
	/* (non-Javadoc)
	 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		refreshAccountList();
		
		pieChartContextMenu = new ContextMenu();
		goToTransactionsItem = new MenuItem("Voir les transactions correspondantes");
        pieChartContextMenu.getItems().add(goToTransactionsItem);
		
		moveSelectorToNewestPeriodWithData(periodSelector, accountComboBox.getSelectionModel().getSelectedItem());
		
		
		Category categoryToSelect = (Category) MenuContext.getInstance().getInitObj(CATEGORY_PARAM);
		if(null != categoryToSelect){
			categorySelector.setCategory(categoryToSelect);
		}
			
		Period periodToSelect = (Period) MenuContext.getInstance().getInitObj(PERIOD_PARAM);
		Date currentDatePeriod = (Date) MenuContext.getInstance().getInitObj(CURRENTDATE_PARAM);
		if(null != periodToSelect && null != currentDatePeriod){
			periodSelector.setValue(periodToSelect,currentDatePeriod);
		}
		
		periodSelector.addEventHandler(PeriodSelector.CHANGED_VALUE, new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				loadData(periodSelector.getPeriodStartDate(), periodSelector.getPeriodEndDate(), categorySelector.getSelectedCategory());
			}
		});
		
		categorySelector.addEventHandler(CategorySelector.CHANGED_VALUE, new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				loadData(periodSelector.getPeriodStartDate(), periodSelector.getPeriodEndDate(), categorySelector.getSelectedCategory());
			}
		});

		accountComboBox.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				loadData(periodSelector.getPeriodStartDate(), periodSelector.getPeriodEndDate(), categorySelector.getSelectedCategory());
			}
		});
		
		subCategoryPieChart.getParent().addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if(pieChartContextMenu.isShowing()) pieChartContextMenu.hide();
			}
    		
    	});
		
		loadData(periodSelector.getPeriodStartDate(), periodSelector.getPeriodEndDate(),categorySelector.getSelectedCategory());
	}
	
	private void loadData(Date startDate, Date endDate, Category category){
		
		classifiedTransactions = TransactionService.findAllSpendingsClassifiedByAccountForPeriod(accountComboBox.getSelectionModel().getSelectedItem(), startDate, endDate, category);
		
		Account account = AccountService.findById(accountComboBox.getSelectionModel().getSelectedItem().getId());
		
		currency = Currency.getInstance(account.getCurrency());
		
		populateSubCategoryPieChart();
	}	
	
	
	/**
	 * 
	 */
	private void populateSubCategoryPieChart(){
		
		Map<SubCategory, BigDecimal> repartitionTransactionParSubCategory = new TreeMap<>();
		BigDecimal totalPieAmount = BigDecimal.ZERO;
		for(Transaction transaction : classifiedTransactions){
			
			SubCategory subCategory = transaction.getSubCategory();
			BigDecimal amount = new BigDecimal(Math.abs(transaction.getAmount()));
			
			if(!repartitionTransactionParSubCategory.containsKey(subCategory)){
				repartitionTransactionParSubCategory.put(subCategory, new BigDecimal(0));
			}
			
			repartitionTransactionParSubCategory.put(subCategory, repartitionTransactionParSubCategory.get(subCategory).add(amount));
			totalPieAmount = totalPieAmount.add(amount);
		}
		
		ObservableList<Data> pieChartData = FXCollections.observableArrayList();
		reversePieChartMap = new HashMap<PieChart.Data, SubCategory>();
		for(SubCategory subCategory : repartitionTransactionParSubCategory.keySet()){
			
			BigDecimal subCategorieAmount = repartitionTransactionParSubCategory.get(subCategory).setScale(3, RoundingMode.HALF_UP);
			BigDecimal subCategoriePercentage = subCategorieAmount.divide(totalPieAmount, 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
			Data data = new Data(subCategory.toString() + " (" + subCategoriePercentage.doubleValue() + "%)", subCategorieAmount.doubleValue());
			
			pieChartData.add(data);
			reversePieChartMap.put(data, subCategory);
		}
		
		TimerService timer = new TimerService(100);
        timer.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
            	subCategoryPieChart.setData(pieChartData);
            	registerPieChartHandlers(pieChartData);
            }
        });
        timer.start();
		
	}
	
	private void registerPieChartHandlers(ObservableList<Data> pieChartData){
		
		HoveredPieData caption = new HoveredPieData("");     

    	for (final PieChart.Data data : pieChartData){
    		
    	    data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
	            @Override public void handle(MouseEvent e) {           	                
	                Main.getRootPane().getChildren().add(caption);
	             }
	        });
    	    
    	    data.getNode().addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
    	    	@Override public void handle(MouseEvent e) {
    	    			caption.setTranslateX(e.getX());
    	    			caption.setTranslateY(e.getY()  - 20);
	                	caption.setData(pieChartData.indexOf(data), data.getName() + " ~ " + String.valueOf(data.getPieValue() + " " + currency.getSymbol()), data.getNode().getStyleClass());
    	            }
    	        });
    	    
    	    data.getNode().addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
    	    	@Override public void handle(MouseEvent e) {
    	    		Main.getRootPane().getChildren().remove(caption);
    	    		}
    	    	});
    	    
    	    /**
    	     * Go to SubCategoryPercentageStatistics screen on CLICK
    	     */
    	    data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
	            @Override public void handle(MouseEvent e) {           	                					

					if(pieChartContextMenu.isShowing()) pieChartContextMenu.hide();
		        	
		            if (e.getButton() == MouseButton.SECONDARY) {
		       
		            	pieChartContextMenu.show(subCategoryPieChart, e.getScreenX(), e.getScreenY());
		            	
		            	goToTransactionsItem.setOnAction(new EventHandler<ActionEvent>() {

							@Override
							public void handle(ActionEvent event) {
								SubCategory subCategory = reversePieChartMap.get(data);
								Account account = accountComboBox.getSelectionModel().getSelectedItem();
								
								Date startDate = periodSelector.getPeriodStartDate();
								Date endDate = periodSelector.getPeriodEndDate();
								
								MenuContext.getInstance().addInitObj(UpdateClassificationController.ACCOUNT_PARAM, account);
								MenuContext.getInstance().addInitObj(UpdateClassificationController.STARTDATE_PARAM, startDate);
								MenuContext.getInstance().addInitObj(UpdateClassificationController.ENDDATE_PARAM, endDate);
								MenuContext.getInstance().addInitObj(UpdateClassificationController.LABEL_PARAM, null);
								MenuContext.getInstance().addInitObj(UpdateClassificationController.CATEGORY_PARAM, subCategory.getCategory());
								MenuContext.getInstance().addInitObj(UpdateClassificationController.SUBCATEGORY_PARAM, subCategory);
								NavigationManager.goToMenu(Menu.UpdateClassification);
							}
	                    	
	                    });
			            
			        }
	             }
	        });
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
