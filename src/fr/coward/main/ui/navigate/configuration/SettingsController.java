package fr.coward.main.ui.navigate.configuration;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import fr.coward.main.model.Session;
import fr.coward.main.model.Settings;
import fr.coward.main.model.services.SettingsService;
import fr.coward.main.model.services.SettingsService.Option;
import fr.coward.main.theme.Theme;
import fr.coward.main.ui.navigate.CreateFormController;

public class SettingsController extends CreateFormController {
	
	@FXML
	private JFXComboBox<Theme> themeComboBox;
	
	@FXML
	private JFXButton saveButton;
	
	/* (non-Javadoc)
	 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		super.initialize(location, resources);

	}


	@Override
	public void initComponents(boolean isFirstInit) {

		/* THEME */		
		themeComboBox.getItems().addAll(Theme.values());
		
		Theme selectedTheme = Theme.getFromFileName(Session.getInstance().getThemeProperty().getValue());
		
		themeComboBox.getSelectionModel().select(selectedTheme);
	}
	
	public void saveButton_onActionHandler(ActionEvent event){
		save();
	}


	@Override
	public void validate() throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void doSave() throws Exception {
		
		Theme selectedTheme = themeComboBox.getSelectionModel().getSelectedItem();
		
		SettingsService.update(new Settings(Option.THEME.name(), selectedTheme.getFileName()));
		
		setMessage("Préférences enregistrées. Veuillez redémarrer pour que les changements soient pris en compte.");
	}
	
	
}
