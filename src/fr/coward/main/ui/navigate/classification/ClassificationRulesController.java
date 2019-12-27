package fr.coward.main.ui.navigate.classification;

import java.net.URL;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import fr.coward.main.model.MenuContext;
import fr.coward.main.model.Rule;
import fr.coward.main.model.SubCategory;
import fr.coward.main.model.Transaction;
import fr.coward.main.model.rule.AbstractRuleValidator;
import fr.coward.main.model.rule.RuleCriteria;
import fr.coward.main.model.rule.RuleOperator;
import fr.coward.main.model.services.RuleService;
import fr.coward.main.model.services.SubCategoryService;
import fr.coward.main.model.utils.Pair;
import fr.coward.main.ui.components.DeletableListView;
import fr.coward.main.ui.components.NumericField;
import fr.coward.main.ui.navigate.CreateFormController;
import fr.coward.main.ui.utils.StringUtil;

public class ClassificationRulesController extends CreateFormController {
	
	public static String TRANSACTION_PARAM = "transactionParam";
	
	@FXML
	private NumericField priorityNumericField;
	@FXML
	private GridPane gridPane;
	@FXML
	private JFXComboBox<SubCategory> subCategoryComboBox;
	@FXML
	private JFXComboBox<RuleOperator> operatorComboBox;
	@FXML
	private JFXComboBox<RuleCriteria> criteriaComboBox;
	@FXML
	private JFXButton addCriteriaRuleButton;
	@FXML
	private DeletableListView<AbstractRuleValidator<?>> criteriaList;
	@FXML
	private DeletableListView<Rule> existingRulesList;

	@FXML
	private Button saveButton;
	
	private Rule currentRule;
	private RuleCriteria currentCriteria;
	private Control currentControl;
	private int columnIndexToFill = 0;
	private int rowIndexToFill = 0;
	
	/* (non-Javadoc)
	 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		super.initialize(location, resources);
		
		this.existingRulesList.getDeletedItemProperty().addListener(new ChangeListener<Rule>() {

			@Override
			public void changed(ObservableValue<? extends Rule> observable,
					Rule oldValue, Rule newValue) {
				// Supprimer la nouvelle r�gle de la base de donn�es
				RuleService.delete(newValue);
				// La suppression de l'�l�ment dans la liste est g�r�e par le composant
				// r�-initialiser l'�cran
				initComponents(true);
			}
		});
		
		this.criteriaList.getDeletedItemProperty().addListener(new ChangeListener<AbstractRuleValidator<?>>() {

			@Override
			public void changed(ObservableValue<? extends AbstractRuleValidator<?>> observable,
					AbstractRuleValidator<?> oldValue, AbstractRuleValidator<?> newValue) {
				// La suppression de l'�l�ment dans la liste est g�r�e par le composant
				// r�-initialiser le formulaire
				criteriaComboBox.getSelectionModel().clearSelection();
				// L'�l�ment suivant prend la s�lection sauf si on change le focus
				focus(priorityNumericField);
			}
		});
	}
	
	/**
	 * Pr�-rempli le formulaire pour une transaction donn�e
	 * @param transaction
	 */
	public void buildRuleForTransaction(Transaction transaction){
		Rule rule = new Rule();
		// Initialisation des valeurs de l'�cran pour une r�gle de type :
		// Montant <= montant transaction
		rule.setIntraOperator(RuleOperator.AND);
		rule.getCriteriaList().add(new Pair<RuleCriteria, String>(
																	RuleCriteria.TransactionAmountLowerThan, 
																	String.valueOf(Math.abs(transaction.getAmount()))
																)
									);
		// Date = date transaction
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(transaction.getDate());
		rule.getCriteriaList().add(new Pair<RuleCriteria, String>(
																	RuleCriteria.TransactionDayOfMonthEquals, 
																	String.valueOf(cal.get(GregorianCalendar.DAY_OF_MONTH))
																)
													);
		// Compte = .*libell� compte.*
		rule.getCriteriaList().add(new Pair<RuleCriteria, String>(
																	RuleCriteria.AccountLabelRegex, 
																	".*" + transaction.getAccount().getLabel() + ".*"
																)
													);
		// Libell� = .*libell� transaction.-*
		rule.getCriteriaList().add(new Pair<RuleCriteria, String>(
																	RuleCriteria.TransactionLabelRegex, 
																	".*" + transaction.getLabel() + ".*"
																)
													);
		this.initFormFor(rule);
	}
	
	/**
	 * Initialise le formulaire � partir de la r�gle
	 * @param rule la r�gle
	 */
	private void initFormFor(Rule rule){
		
		if(null != rule){
			this.criteriaList.getItems().clear();
			
			this.currentRule = rule;
			
			// Recopier la priorit�
			this.priorityNumericField.setText(String.valueOf((rule.getPriority() > 0) ? rule.getPriority() : 1));
			// Recopier l'op�rateur
			this.operatorComboBox.getSelectionModel().select(rule.getIntraOperator());
			// Recopier les crit�res
			for(Pair<RuleCriteria, String> criteria : rule.getCriteriaList()){
				AbstractRuleValidator<?> validator;
				try {
					validator = criteria.getL().getValidationClass().newInstance();
					validator.setUserValue(criteria.getR());
					criteriaList.getItems().add(validator);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if(rule.getSubCategory() != null){
				this.subCategoryComboBox.getSelectionModel().select(rule.getSubCategory());
			}
		} else {
			initComponents(false);
		}
	}
	
	/**
	 * A la modification de selection d'un crit�re
	 * @param event
	 */
	public void criteriaComboBox_onActionHandler(ActionEvent event){
		currentCriteria = criteriaComboBox.getSelectionModel().getSelectedItem();
		
		showHideCriteriaControl(currentCriteria, null);
	}
	
	/**
	 * Au clic sur le bouton "Enregistrer"
	 * @param event
	 */
	public void saveButton_onActionHandler(ActionEvent event) {
		save();
	}
	
	/**
	 * Sur la s�lection d'une r�gle existante, alimenter le formulaire avec les donn�es de la r�gle
	 * Cela permet une modification de la donn�e
	 * @param event
	 */
	public void existingRulesList_onClickHandler(MouseEvent event){
		this.initFormFor(existingRulesList.getSelectionModel().getSelectedItem());
	}	
	
	/**
	 * Sur la s�lection d'un crit�re, positionner les champs de saisie sur ce crit�re.
	 * Cela permet une modification de la donn�e
	 * @param event
	 */
	public void criteriaList_onClickHandler(MouseEvent event){
		RuleCriteria currentCriteria = RuleCriteria.valueOf(criteriaList.getSelectionModel().getSelectedItem().getClass());
		criteriaComboBox.getSelectionModel().select(currentCriteria);
		this.showHideCriteriaControl(currentCriteria, criteriaList.getSelectionModel().getSelectedItem().getUserValue());
		
	}
	
	/**
	 * Sur l'ajout d'un crit�re au panier
	 * @param event
	 */
	public void addCriteriaRuleButton_onActionHandler(ActionEvent event){
		
		AbstractRuleValidator<?> validator = null;
		try {
			if(this.operatorComboBox.getSelectionModel().getSelectedItem().equals(RuleOperator.AND)){
				// Supprimer l'ancienne instance de criteria car ils'agit d'un update
				for(AbstractRuleValidator<?> existingValidator : criteriaList.getItems()){
					if(existingValidator.getClass().equals(currentCriteria.getValidationClass())){
						criteriaList.getItems().remove(existingValidator);
						break;
					}
				}
			}
			validator = currentCriteria.getValidationClass().newInstance();
			validator.setUserValue(((TextField)currentControl).getText());
			criteriaList.getItems().add(validator);
			
			criteriaComboBox.getSelectionModel().clearSelection();
			
		} catch (Exception e) {
			validator = null;
			setMessage(e.getMessage());
			e.printStackTrace();
		} 
	}
	
	/**
	 * Charge les r�gles de classification existantes
	 */
	private void loadExistingRules(){
		
		ObservableList<Rule> items = FXCollections.observableArrayList(RuleService.findAll());
		
		existingRulesList.setItems(items);
	}
	
	/**
	 * Charge les sous-cat�gories
	 */
	private void loadSubCategories(){
		
		List<SubCategory> loadedSubCategoryList = SubCategoryService.findAll();
		
		ObservableList<SubCategory> items = FXCollections.observableArrayList();
		
		for(SubCategory subCategory : loadedSubCategoryList){
			items.add(subCategory);
		}
		subCategoryComboBox.setItems(items);
		
		if(items.size() == 1){
			subCategoryComboBox.getSelectionModel().selectFirst();
		}
	}
	
	/**
	 * Affiche ou masque le champ de saisie d'une r�gle
	 * @param selectedCriteria
	 */
	private void showHideCriteriaControl(RuleCriteria selectedCriteria, String controlValue){
		
		if(null != selectedCriteria){
			
			if(null != currentControl){
				gridPane.getChildren().remove(currentControl);
				currentControl = null;
			}
			
			try {
				currentControl = selectedCriteria.getControlClass().newInstance();
				currentControl.setVisible(true);
				
				if(null != controlValue) ((TextField)currentControl).setText(controlValue);
			} catch (Exception e) {
				e.printStackTrace();
			} 			
			
			gridPane.add(currentControl, columnIndexToFill, rowIndexToFill);
			
			addCriteriaRuleButton.setVisible(true);
		} else {
			if(null != currentControl){
				gridPane.getChildren().remove(currentControl);
				currentControl = null;
			}
			addCriteriaRuleButton.setVisible(false);
		}
	}
	
	/**
	 * Charge les op�rateurs
	 */
	private void loadOperators(){
		
		ObservableList<RuleOperator> items = FXCollections.observableArrayList(RuleOperator.values());
		operatorComboBox.setItems(items);
		
		if(!items.isEmpty()){
			operatorComboBox.getSelectionModel().selectFirst();
		}
	}
	
	private void loadCriteria(){
		
		ObservableList<RuleCriteria> items = FXCollections.observableArrayList(RuleCriteria.values());

		criteriaComboBox.setItems(items);
	}
	

	@Override
	public void initComponents(boolean isFirstInit) {
		
		currentRule = null;
		
		if(isFirstInit){
			loadOperators();
			loadCriteria();
			loadSubCategories();
			
			columnIndexToFill = GridPane.getColumnIndex(criteriaComboBox)+1;
			rowIndexToFill = GridPane.getRowIndex(criteriaComboBox);
			
		}
		
		loadExistingRules();
		
		priorityNumericField.setText("");
		subCategoryComboBox.getSelectionModel().clearSelection();
		criteriaComboBox.getSelectionModel().clearSelection();
		criteriaList.getItems().clear();
		
		focus(priorityNumericField);
		
		Transaction transaction = (Transaction) MenuContext.getInstance().getInitObj(TRANSACTION_PARAM);
		if(null != transaction){
			if(null != transaction){
				this.buildRuleForTransaction(transaction);
			}
		}
	}

	@Override
	public void validate() throws Exception {

		if (!StringUtil.isNotNullNotEmpty(priorityNumericField.getText())) {
			throw new Exception("Veuillez renseigner la priorit�.");
		}
		if(null == subCategoryComboBox.getSelectionModel().getSelectedItem()){
			throw new Exception("Veuillez sp�cifier la sous-cat�gorie r�sultante.");
		}
		if(criteriaList.getItems().isEmpty()){
			throw new Exception("Veuillez d�finir au moins un crit�re.");
		}
	}

	@Override
	public void doSave() throws Exception {

		int priority = Integer.parseInt(priorityNumericField.getText());
		SubCategory expectedResult = subCategoryComboBox.getSelectionModel().getSelectedItem();
		
		List<Pair<RuleCriteria, String>> pairList = new ArrayList<Pair<RuleCriteria,String>>();
		for(AbstractRuleValidator<?> validator : criteriaList.getItems()){
			pairList.add(new Pair<RuleCriteria, String>(RuleCriteria.valueOf(validator.getClass()), validator.getUserValue()));						
		}
		
		if(null == currentRule){
			currentRule = new Rule();
		}
		
		currentRule.setPriority(priority);
		currentRule.setSubCategory(expectedResult);
		currentRule.setCriteriaList(pairList);
		currentRule.setIntraOperator(operatorComboBox.getSelectionModel().getSelectedItem());
		RuleService.save(currentRule);
		
		if(currentRule.getId() != 0){
			initComponents(false);
		} else {
			throw new Exception("Echec d'enregistrement de la r�gle.");
		}

	}
}
