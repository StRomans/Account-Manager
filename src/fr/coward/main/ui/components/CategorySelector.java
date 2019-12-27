package fr.coward.main.ui.components;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

import com.jfoenix.controls.JFXComboBox;

import fr.coward.main.model.Category;
import fr.coward.main.model.services.CategoryService;

public class CategorySelector extends HBox {
	
	public static final EventType<Event> CHANGED_VALUE = new EventType<Event>("CategorySelectorChangedValue");
	
	public static final Category ALL_CATEGORIES = new Category("Toutes catégories");
	
	JFXComboBox<Category> categorySelectorComboBox;

	public CategorySelector(){
		
		Font labelFont = new Font("System Bold", 14);
		
		Label periodSelectorLabel = new Label("Catégorie");
		periodSelectorLabel.setFont(labelFont);
		periodSelectorLabel.setPadding(new Insets(3, 0, 0, 0));
		
		categorySelectorComboBox = new JFXComboBox<Category>();
		this.loadCategories();
		this.setCategory(ALL_CATEGORIES);
		
		categorySelectorComboBox.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				refreshFilterValue();
			}
		});
		
		
		this.getChildren().addAll(periodSelectorLabel, categorySelectorComboBox);
		
		refreshFilterValue();
	}
	
	public void loadCategories(){
		categorySelectorComboBox.getItems().clear();
		
		categorySelectorComboBox.getItems().add(ALL_CATEGORIES);
		
		categorySelectorComboBox.getItems().addAll(CategoryService.findAll());
	}
	
	public void setCategory(Category category){
		
		if(null != category){
					
			categorySelectorComboBox.getSelectionModel().select(category);
			
			categorySelectorComboBox.setDisable(categorySelectorComboBox.getItems().size() == 1);
		} else {
			categorySelectorComboBox.getSelectionModel().select(ALL_CATEGORIES);
		}
		
		this.refresh();
	}
	
	public void refresh(){
		refreshFilterValue();
	}
	
	private void refreshFilterValue(){		
		fireEvent(new Event(null, this, CHANGED_VALUE));
	}

	public Category getSelectedCategory(){
		
		Category category = categorySelectorComboBox.getSelectionModel().getSelectedItem();
		
		return category.equals(ALL_CATEGORIES) ? null : category;
	}
}
