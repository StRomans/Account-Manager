package fr.coward.main.ui.navigate.classification;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseEvent;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;

import fr.coward.main.model.Category;
import fr.coward.main.model.SubCategory;
import fr.coward.main.model.services.CategoryService;
import fr.coward.main.model.services.SubCategoryService;
import fr.coward.main.ui.components.DeletableTableView;
import fr.coward.main.ui.navigate.CreateFormController;
import fr.coward.main.ui.utils.StringUtil;

public class CreateSubCategoryController extends CreateFormController {
	
	@FXML
	private JFXTextField subCategoryLabelTextField;
	@FXML
	private JFXComboBox<Category> categoryComboBox;
	
	@FXML
	private DeletableTableView<SubCategory> subCategoryList;
	@FXML
	private JFXButton saveButton;
	
	private SubCategory currentSubCategory;
	
	@FXML
    private TableColumn<SubCategory, String> categoryLabelColumn;
	@FXML
    private TableColumn<SubCategory, String> subCategoryLabelColumn;

	
	/* (non-Javadoc)
	 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		super.initialize(location, resources);
		
		this.subCategoryList.getDeletedItemProperty().addListener(new ChangeListener<SubCategory>() {

			@Override
			public void changed(ObservableValue<? extends SubCategory> observable,
					SubCategory oldValue, SubCategory newValue) {
				// Supprimer la nouvelle règle de la base de données
				SubCategoryService.delete(newValue);
				// La suppression de l'élément dans la liste est gérée par le composant
				// ré-initialiser l'écran
				initComponents(true);
			}
		});
		
		int columnSize = 448;
		
		categoryLabelColumn = new TableColumn<>("Catégorie");
		categoryLabelColumn.setPrefWidth(columnSize);
		categoryLabelColumn.setSortable(true);
		categoryLabelColumn.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().getCategory().getLabel()));
		
		subCategoryLabelColumn = new TableColumn<>("Sous-Catégorie");
		subCategoryLabelColumn.setPrefWidth(columnSize);
		subCategoryLabelColumn.setSortable(true);
		subCategoryLabelColumn.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().getLabel()));
		
		
		subCategoryList.registerColumns(subCategoryLabelColumn, categoryLabelColumn);
		
	}
	
	public void saveButton_onActionHandler(ActionEvent event){
		save();
	}
	
	public void subCategoryList_onClickHandler(MouseEvent event){
		SubCategory selectedItem = this.subCategoryList.getSelectionModel().getSelectedItem();
		this.initFormFor(selectedItem);
	}
	
	public void initFormFor(SubCategory subCategory){
		
		currentSubCategory = subCategory;
		
		if(null != subCategory){
			this.subCategoryLabelTextField.setText(subCategory.getLabel());
			this.categoryComboBox.getSelectionModel().select(subCategory.getCategory());
		} else {
			initComponents(false);
		}
	}
	
	/**
	 * Charge la liste des catégories
	 */
	private void loadCategories(){
		
		List<Category> loadedCategoryList = CategoryService.findAll();
		
		ObservableList<Category> items = FXCollections.observableArrayList();
		for(Category category : loadedCategoryList){
			items.add(category);
		}
		categoryComboBox.setItems(items);
		
		// sélectionner la catégorie si unique
		if(items.size() == 1){
			categoryComboBox.getSelectionModel().select(items.get(0));
		}
	}
	
	
	
	/**
	 * Charge la liste des sous-catégories
	 */
	private void refreshList(){
		
		List<SubCategory> loadedSubCategoryList = SubCategoryService.findAll();
		
		if(loadedSubCategoryList.isEmpty()){
			loadedSubCategoryList.add(new SubCategory("Aucun enregistrement trouvé."));
		}
		subCategoryList.getItems().clear();
		ObservableList<SubCategory> items = FXCollections.observableArrayList();
		
		for(SubCategory subCategory : loadedSubCategoryList){
			items.add(subCategory);
		}
		subCategoryList.setItems(items);
	}

	@Override
	public void initComponents(boolean isFirstInit) {
		
		currentSubCategory = null;
		
		if(isFirstInit) loadCategories();
		
		focus(subCategoryLabelTextField);

		subCategoryLabelTextField.textProperty().setValue("");
		categoryComboBox.getSelectionModel().clearSelection();
		
		refreshList();
	}

	@Override
	public void validate() throws Exception {
		
		String subCategoryLabel = subCategoryLabelTextField.textProperty().getValue().trim();

		if(!StringUtil.isNotNullNotEmpty(subCategoryLabel)){
			throw new Exception("Veuillez renseigné un libellé.");
		}
		if(categoryComboBox.getSelectionModel().getSelectedItem() == null){
			throw new Exception("Veuillez selectionner une catégorie.");
		}
	}

	@Override
	public void doSave() throws Exception {

		String subCategoryLabel = subCategoryLabelTextField.textProperty().getValue().trim();

		Category selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();
		
		if(null == currentSubCategory){
			currentSubCategory = new SubCategory();
		}
		
		currentSubCategory.setLabel(subCategoryLabel);
		currentSubCategory.setCategory(selectedCategory);
		
		boolean success = SubCategoryService.save(currentSubCategory);
		
		if(success){
			if(currentSubCategory.getId() != 0){
				// Feedback utilisateur sur le succès
				setMessage("La sous-catégorie a bien été enregistrée.");
				// Vider les champs sauf le compte
				initComponents(false);
				
			}
		}
		 else {
			// Feedback utilisateur sur l'échec de son action
			throw new Exception("Cette sous-catégorie est déjà définie pour cette catégorie. Veuillez modifier le libellé.");
		}

	}
}
