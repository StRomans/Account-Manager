package fr.coward.main.ui.navigate.classification;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;

import fr.coward.main.Main;
import fr.coward.main.model.Account;
import fr.coward.main.model.Category;
import fr.coward.main.model.MenuContext;
import fr.coward.main.model.SubCategory;
import fr.coward.main.model.DTO.TransactionViewDTO;
import fr.coward.main.model.services.AccountService;
import fr.coward.main.model.services.CategoryService;
import fr.coward.main.model.services.SubCategoryService;
import fr.coward.main.model.services.TransactionService;
import fr.coward.main.ui.components.TransactionEditPaneController;
import fr.coward.main.ui.utils.DateUtil;
import fr.coward.main.ui.utils.FXMLUtil;

public class UpdateClassificationController implements Initializable {
	
	public static String ACCOUNT_PARAM = "accountParam";
	public static String STARTDATE_PARAM = "startDateParam";
	public static String ENDDATE_PARAM = "endDateParam";
	public static String LABEL_PARAM = "labelParam";
	public static String CATEGORY_PARAM = "categoryParam";
	public static String SUBCATEGORY_PARAM = "subCategoryParam";
	
	@FXML
	private JFXComboBox<Account> accountComboBox;
	@FXML
	private JFXDatePicker transactionStartDatePicker;
	@FXML
	private JFXDatePicker transactionEndDatePicker;
	@FXML
	private JFXTextField transactionLabelTextField;
	@FXML
	private JFXComboBox<Category> categoryComboBox;
	@FXML
	private JFXComboBox<SubCategory> subCategoryComboBox;
	@FXML
	private JFXButton searchTransactionButton;
	@FXML
	private JFXButton clearTransactionButton;
	
	@FXML
	private HBox resultHbox;
	@FXML
	private Label resultCountLabel;
	@FXML
	private TableView<TransactionViewDTO> transactionDtoTableView;
	@FXML
    private TableColumn<TransactionViewDTO, Account> accountLabelColumn;
	@FXML
    private TableColumn<TransactionViewDTO, java.util.Date> transactionDateColumn;
	@FXML
    private TableColumn<TransactionViewDTO, String> transactionLabelColumn;
	@FXML
    private TableColumn<TransactionViewDTO, Double> transactionAmountColumn;
	@FXML
    private TableColumn<TransactionViewDTO, String> transactionCurrencyColumn;
	@FXML
    private TableColumn<TransactionViewDTO, Category> transactionCategoryLblColumn;
	@FXML
    private TableColumn<TransactionViewDTO, SubCategory> transactionSubCategoryLblColumn;
	@FXML
	private TableColumn<TransactionViewDTO, Integer> actionColumn; 

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		Account account = null;
		Date startDate = null;
		Date endDate = null;
		String libelle = null;
		Category category = null;
		SubCategory subCategory = null;
		
		this.initComponents(true);
		
		if(MenuContext.getInstance().hasObjects()){
			
			account = (Account) MenuContext.getInstance().getInitObj(ACCOUNT_PARAM);
			startDate = (Date) MenuContext.getInstance().getInitObj(STARTDATE_PARAM);
			endDate = (Date) MenuContext.getInstance().getInitObj(ENDDATE_PARAM);
			libelle = (String) MenuContext.getInstance().getInitObj(LABEL_PARAM);
			category = (Category) MenuContext.getInstance().getInitObj(CATEGORY_PARAM);
			subCategory = (SubCategory) MenuContext.getInstance().getInitObj(SUBCATEGORY_PARAM);
			
			initComponentsWithCriteria(account, startDate, endDate, libelle, category, subCategory);
		} else {
			showResults(false);
		}
	}
	
	public void initComponents(boolean isFirstInit) {
		
		loadAccountList();
		loadCategories();
		loadSubCategories();
		initTableView();
		showResults(false);
		focus(accountComboBox);
	}
	
	/**
	 * Pré-remplit l'interface et déclenche la recherche correspondante
	 * @param account le compte {@link Account}
	 * @param dateDebut la date de début
	 * @param dateFin la date de fin
	 * @param libelle le libellé de la transaction
	 * @param category la catégorie {@link Category}
	 * @param subCategory la sous-catégorie {@link SubCategory}
	 */
	public void initComponentsWithCriteria(Account account, Date dateDebut, Date dateFin, String libelle, Category category, SubCategory subCategory){
		
		this.accountComboBox.getSelectionModel().select(account);
		this.transactionStartDatePicker.setValue(DateUtil.date2LocalDate(dateDebut));
		this.transactionEndDatePicker.setValue(DateUtil.date2LocalDate(dateFin));
		this.categoryComboBox.getSelectionModel().select(category);
		this.subCategoryComboBox.getSelectionModel().select(subCategory);
		
		searchTransactions();
	}
	
	/**
	 * Place le focus sur un composant
	 * @param component
	 */
	private void focus(Node component){
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
		    	if(null != component){
		    		component.requestFocus();
		    	}
		    }
		});
	}
	
	@SuppressWarnings("unchecked")
	private void initTableView(){
		accountLabelColumn = new TableColumn<>("Compte");
		accountLabelColumn.setPrefWidth(100);
		accountLabelColumn.setCellValueFactory(accountLabelColumn -> accountLabelColumn.getValue().getAccountProperty());
		accountLabelColumn.setCellFactory(transactionSubCategoryLblColumn -> {
																				return new TableCell<TransactionViewDTO, Account>()
																				{
																					@Override
																			        protected void updateItem(Account item, boolean empty) {
																						super.updateItem(item, empty);
																						if(null != item){
																							setText(item.getLabel());
																						} else {
																							setText("");
																						}
																					};
																				};
																			});
		
		transactionDateColumn = new TableColumn<>("Date");
		transactionDateColumn.setPrefWidth(80);
		transactionDateColumn.setCellValueFactory(transactionDateColumn -> transactionDateColumn.getValue().getDateProperty());
		transactionDateColumn.setCellFactory(transactionDateColumn -> {
																				return new TableCell<TransactionViewDTO, java.util.Date>()
																				{
																					@Override
																			        protected void updateItem(java.util.Date item, boolean empty) {
																						super.updateItem(item, empty);
																						if(null != item){
																							setText(new SimpleDateFormat("dd/MM/yyyy").format(item));
																						} else {
																							setText("");
																						}
																					};
																				};
																			});
		
		transactionLabelColumn = new TableColumn<>("Libellé");
		transactionLabelColumn.setPrefWidth(280);
		transactionLabelColumn.setCellValueFactory(transactionLabelColumn -> transactionLabelColumn.getValue().getLabelProperty());
		
		transactionAmountColumn = new TableColumn<>("Montant");
		transactionAmountColumn.setPrefWidth(70);
		transactionAmountColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
		transactionAmountColumn.setCellValueFactory(transactionAmountColumn -> transactionAmountColumn.getValue().getAmountProperty().asObject());
		
		transactionCurrencyColumn = new TableColumn<>("Devise");
		transactionCurrencyColumn.setPrefWidth(60);
		transactionCurrencyColumn.setCellValueFactory(transactionCurrencyColumn -> transactionCurrencyColumn.getValue().getCurrencyProperty());
		
		transactionCategoryLblColumn = new TableColumn<>("Catégorie");
		transactionCategoryLblColumn.setPrefWidth(105);
		transactionCategoryLblColumn.setCellValueFactory(transactionCategoryLblColumn -> transactionCategoryLblColumn.getValue().getCategoryProperty());
		transactionCategoryLblColumn.setCellFactory(transactionSubCategoryLblColumn -> {
																							return new TableCell<TransactionViewDTO, Category>()
																							{
																								@Override
																						        protected void updateItem(Category item, boolean empty) {
																									super.updateItem(item, empty);
																									if(null != item){
																										setText(item.getLabel());
																									} else {
																										setText("");
																									}
																								};
																							};
																						});
				
		transactionSubCategoryLblColumn = new TableColumn<>("Sous-catégorie");
		transactionSubCategoryLblColumn.setPrefWidth(166);
		transactionSubCategoryLblColumn.setCellValueFactory(transactionSubCategoryLblColumn -> transactionSubCategoryLblColumn.getValue().getSubCategoryProperty());	
		transactionSubCategoryLblColumn.setCellFactory(transactionSubCategoryLblColumn -> {
																									return new TableCell<TransactionViewDTO, SubCategory>()
																									{
																										@Override
																								        protected void updateItem(SubCategory item, boolean empty) {
																											super.updateItem(item, empty);
																											if(null != item){
																												setText(item.getLabel());
																											} else {
																												setText("");
																											}
																										};
																									};
																								});
				
		actionColumn = new TableColumn<>("");
		actionColumn.setPrefWidth(75);
		actionColumn.setStyle("-fx-alignment: CENTER;");
		actionColumn.setSortable(false);
		actionColumn.setCellValueFactory(actionColumn -> actionColumn.getValue().getIdProperty().asObject());
		actionColumn.setCellFactory(actionColumn -> {
														return new TableCell<TransactionViewDTO, Integer>()
														{
															Image editImg = new Image(getClass().getResourceAsStream("/fr/coward/main/resources/icons/edit3.png"));
															ImageView editImgView = new ImageView(editImg);
															JFXButton editBtn = new JFXButton("",editImgView);
															
															{
																editBtn.setOnAction((ActionEvent event) -> {
																	TransactionViewDTO item = getTableView().getItems().get(getIndex());
																	int initialAccountId = item.getAccountId();
																	MenuContext.getInstance().addInitObj(TransactionEditPaneController.TRANSACTION_PARAM, item);
																	ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonData.OK_DONE);
																	ButtonType cancelButtonType = new ButtonType("Annuler", ButtonData.CANCEL_CLOSE);
																	Dialog<ButtonType> editDialog = new Dialog<>();
//																	editDialog.initModality(Modality.NONE); 
																	editDialog.setTitle("Formulaire d'édition");
																	editDialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);
//																	ButtonBar buttonBar = (ButtonBar)editDialog.getDialogPane().lookup(".button-bar");
//																	buttonBar.getButtons().forEach(b->b.getStyleClass().add("popUp-button"));
//																	buttonBar.getButtons().forEach(b->b.setStyle("-fx-font-weight: bold;-fx-background-color: #008000;-fx-text-fill: WHITE;"));
																	
																	FXMLLoader loader = FXMLUtil.getLoader("ui/components/TransactionEditPane.fxml");
																	Parent formulaire = FXMLUtil.getFXMLParent(loader);
																	formulaire.getStylesheets().addAll(Main.getStyleSheets());
																	formulaire.getStyleClass().add("bottom-line");
																	
																	// A la clôture de la popUp : est-ce par le bouton Enregistrer ? Annuler ? La croix ?
																	editDialog.setOnCloseRequest((DialogEvent dialogEvent) -> {
																		TransactionEditPaneController controller = FXMLUtil.getController(loader);
																		ButtonType dialogResult = ((Dialog<ButtonType>)dialogEvent.getSource()).getResult();
																		if (dialogResult == saveButtonType) {
																			// Si fermeture par le bouton enregistrer
																			if(controller.save()) {
																				if(initialAccountId != controller.getTransaction().getAccountId()){
																					getTableView().getItems().remove(item);
																					showResults(true);
																				}
																			} else {
																				// Si les données de la popUp ne passent pas la validation, on empêche la fermeture
																				dialogEvent.consume();
																			}
																		}
																	});
																	
																	editDialog.getDialogPane().setContent(formulaire);
//																	ScenicView.show(editDialog.getDialogPane());
																	editDialog.showAndWait();
										                        });
										                    }
															
															Image deleteImg = new Image(getClass().getResourceAsStream("/fr/coward/main/resources/icons/delete3.png"));
															ImageView deleteImgView = new ImageView(deleteImg);
															JFXButton deleteBtn = new JFXButton("",deleteImgView);
															
															{
																deleteBtn.setOnAction((ActionEvent event) -> {
																	TransactionViewDTO item = getTableView().getItems().get(getIndex());
																	ButtonType confirmButtonType = new ButtonType("Confirmer", ButtonData.OK_DONE);
																	ButtonType cancelButtonType = new ButtonType("Annuler", ButtonData.CANCEL_CLOSE);
																	Dialog<ButtonType> confirmDeleteDialog = new Dialog<>();
																	confirmDeleteDialog.setTitle("Demande de confirmation");
																	confirmDeleteDialog.setContentText("Etes-vous sûr de vouloir supprimer cette transaction ?");
																	confirmDeleteDialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, cancelButtonType);
																	Optional<ButtonType> result = confirmDeleteDialog.showAndWait();
																	if (result.isPresent() && ButtonData.OK_DONE.equals(result.get().getButtonData())) {
																		getTableView().getItems().remove(item);
																		item.delete();
																		showResults(true);
																	}
										                        });
										                    }
															
															@Override
													        protected void updateItem(Integer item, boolean empty) {
																super.updateItem(item, empty);
																if (empty) {
										                            setGraphic(null);
										                        } else {
										                        	int iconSize = 16;
										                        	editImgView.setFitHeight(iconSize);
																	editImgView.setFitWidth(iconSize);
																	editBtn.getStyleClass().add("transparent");
																	editBtn.maxHeight(iconSize);
																	editBtn.setTooltip(new Tooltip("Editer"));
																	
										                        	deleteImgView.setFitHeight(iconSize);
																	deleteImgView.setFitWidth(iconSize);
																	deleteBtn.getStyleClass().add("transparent");
																	deleteBtn.maxHeight(iconSize);
																	deleteBtn.setTooltip(new Tooltip("Supprimer"));
										                        	
										                        	HBox buttonsHBox = new HBox();
										                    		buttonsHBox.getChildren().addAll(editBtn, deleteBtn);
										                    		buttonsHBox.maxHeight(iconSize);
										                            setGraphic(buttonsHBox);
										                        }
															};
														};
													});
		
		transactionDtoTableView.getColumns().addAll(accountLabelColumn, 
													transactionDateColumn, 
													transactionLabelColumn, 
													transactionAmountColumn,
													transactionCurrencyColumn,
													transactionCategoryLblColumn,
													transactionSubCategoryLblColumn,
													actionColumn);
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
	
	public void searchTransactionButton_onActionHandler(ActionEvent event){
		searchTransactions();
	}
	
	public void searchTransactions(){
		
		transactionDtoTableView.getItems().clear();
		
		Account selectedAccount = accountComboBox.getSelectionModel().getSelectedItem();
		Date startDate = (null != transactionStartDatePicker.getValue()) ? DateUtil.localDate2Date(transactionStartDatePicker.getValue()) : null;
		Date endDate = (null != transactionEndDatePicker.getValue()) ? DateUtil.localDate2Date(transactionEndDatePicker.getValue()) : null;
		String label = transactionLabelTextField.getText();
		SubCategory selectedsubCategory = subCategoryComboBox.getSelectionModel().getSelectedItem();
		Category selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();
		
		List<TransactionViewDTO> transactionDtoResults = TransactionService.findAllDTOByComplexCriteria(selectedAccount, startDate, endDate, label, selectedCategory, selectedsubCategory);
		ObservableList<TransactionViewDTO> itemsDto = FXCollections.observableArrayList();
		for(TransactionViewDTO transactionDto : transactionDtoResults){
			itemsDto.add(transactionDto);
		}
		transactionDtoTableView.setItems(itemsDto);
		
		showResults(true);
	}
	
	public void clearTransactionButton_onActionHandler(ActionEvent event){
		
		accountComboBox.getSelectionModel().clearSelection();
		transactionStartDatePicker.setValue(null);
		transactionEndDatePicker.setValue(null);
		transactionLabelTextField.setText(null);
		categoryComboBox.getSelectionModel().clearSelection();
		subCategoryComboBox.getSelectionModel().clearSelection();
		showResults(false);
	}
	
	
	/**
	 * Charge les catégories déclarées
	 */
	private void loadCategories(){
		
		List<Category> loadedCategoryList = CategoryService.findAll();
		
		ObservableList<Category> items = FXCollections.observableArrayList();
		
		for(Category category : loadedCategoryList){
			items.add(category);
		}
		categoryComboBox.setItems(items);
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

	private void showResults(boolean show){
		
		resultCountLabel.setText(String.valueOf(transactionDtoTableView.getItems().size()));
		
		resultHbox.setVisible(show);
		transactionDtoTableView.setVisible(show);
		
	}
}
