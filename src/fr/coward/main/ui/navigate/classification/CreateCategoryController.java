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
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import fr.coward.main.model.Category;
import fr.coward.main.model.services.CategoryService;
import fr.coward.main.ui.components.DeletableTableView;
import fr.coward.main.ui.navigate.CreateFormController;
import fr.coward.main.ui.utils.StringUtil;

public class CreateCategoryController extends CreateFormController {
	
	@FXML
	private JFXTextField categoryLabelTextField;
	@FXML
	private ColorPicker categoryColorPicker;
	@FXML
	private DeletableTableView<Category> categoryTableView;
	@FXML
	private JFXButton saveButton;
	
	private Category currentCategory;
	
	@FXML
    private TableColumn<Category, String> categoryLabelColumn;

	
	/* (non-Javadoc)
	 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		super.initialize(location, resources);
		
		this.categoryTableView.getDeletedItemProperty().addListener(new ChangeListener<Category>() {

			@Override
			public void changed(ObservableValue<? extends Category> observable,
					Category oldValue, Category newValue) {
				// Supprimer la nouvelle r�gle de la base de donn�es
				CategoryService.delete(newValue);
				// La suppression de l'�l�ment dans la liste est g�r�e par le composant
				// r�-initialiser l'�cran
				initComponents(true);
			}
		});
		
		categoryLabelColumn = new TableColumn<>("Libell�");
		categoryLabelColumn.setPrefWidth(910);
		categoryLabelColumn.setSortable(true);
		categoryLabelColumn.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().getLabel()));
		categoryTableView.registerColumns(categoryLabelColumn);
	
	}
	
	public void saveButton_onActionHandler(ActionEvent event){
		save();
	}
	
	public void categoryList_onClickHandler(MouseEvent event){
		Category selectedItem = this.categoryTableView.getSelectionModel().getSelectedItem();
		this.initFormFor(selectedItem);
	}
	
	public void initFormFor(Category category){
		
		currentCategory = category;
		
		if(null != category){
			this.categoryLabelTextField.setText(category.getLabel());
			
			if(null != category.getColor())	{
				this.categoryColorPicker.setValue(Color.valueOf(category.getColor()));
			} else {
				this.categoryColorPicker.setValue(Color.WHITE);
			}
			
		} else {
			initComponents(false);
		}
	}
	
	/**
	 * 
	 */
	private void refreshList(){
		
		List<Category> loadedCategoryList = CategoryService.findAll();
		
		if(loadedCategoryList.isEmpty()){
			loadedCategoryList.add(new Category("Aucun enregistrement trouv�."));
		}
		
		categoryTableView.getItems().clear();
		ObservableList<Category> items = FXCollections.observableArrayList();
		for(Category category : loadedCategoryList){
			items.add(category);
		}
		categoryTableView.setItems(items);
	}


	@Override
	public void initComponents(boolean isFirstInit) {
		
		currentCategory = null;
		
		refreshList();
		
		categoryLabelTextField.textProperty().setValue("");
		
		focus(categoryLabelTextField);
	}

	@Override
	public void validate() throws Exception {
		
		String categoryLabel = categoryLabelTextField.textProperty().getValue().trim();
		
		if(!StringUtil.isNotNullNotEmpty(categoryLabel)){
			throw new Exception("Veuillez saisir un libell�.");
		}
		
	}

	@Override
	public void doSave() throws Exception {
		
		String categoryLabel = categoryLabelTextField.textProperty().getValue().trim();
		String categoryColor = categoryColorPicker.getValue().toString();
		
		if(null == currentCategory){
			currentCategory = new Category();
		}
			
		currentCategory.setLabel(categoryLabel);
		currentCategory.setColor(categoryColor);
		boolean success = CategoryService.save(currentCategory);

		// Si compte ajout�
		if (success){
			if(currentCategory.getId() != 0){
				 
				initComponents(false);
				
			}
		}
		else {
			throw new Exception("Une cat�gorie existe d�j� pour ce libell�. Veuillez saisir un autre libell�.");
		}
	}
}
