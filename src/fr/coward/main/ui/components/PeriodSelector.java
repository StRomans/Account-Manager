package fr.coward.main.ui.components;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.sun.xml.internal.ws.util.StringUtils;

import fr.coward.main.model.utils.Period;

public class PeriodSelector extends HBox {
	
	public static final EventType<Event> CHANGED_VALUE = new EventType<Event>("PeriodSelectorChangedValue");
	
	JFXComboBox<Period> periodSelectorComboBox;
	
	private JFXButton previousButton;
	private JFXButton nextButton;
	private Label periodSelectorValueLabel;
	
	private HBox periodValueControllerHBox;
	
	private GregorianCalendar currentCalendar;
	private Date startDate;
	private Date endDate;

	public PeriodSelector(){
		
		currentCalendar = new GregorianCalendar();
		startDate = null;
		endDate = null;
		
		Font labelFont = new Font("System Bold", 14);
		
		Label periodSelectorLabel = new Label("Période");
		periodSelectorLabel.setFont(labelFont);
		periodSelectorLabel.setPadding(new Insets(3, 0, 0, 0));
		
		periodSelectorComboBox = new JFXComboBox<Period>();
		periodSelectorComboBox.getItems().addAll(Period.values());
		periodSelectorComboBox.getSelectionModel().selectFirst();
		
		periodSelectorComboBox.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				refreshFilterValue();
			}
		});
		
		previousButton = new JFXButton("<");	
		previousButton.setTooltip(new Tooltip("Précédent"));
		periodSelectorValueLabel = new Label();
		periodSelectorValueLabel.setFont(labelFont);
		periodSelectorValueLabel.setPadding(new Insets(3, 0, 0, 0));
		nextButton = new JFXButton(">");
		nextButton.setTooltip(new Tooltip("Suivant"));
		
		periodValueControllerHBox = new HBox(10);
		periodValueControllerHBox.getChildren().addAll(previousButton, periodSelectorValueLabel, nextButton);
		
		this.getChildren().addAll(periodSelectorLabel, periodSelectorComboBox, periodValueControllerHBox);
		
		previousButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				goToPreviousPeriod();

			}
		});
		
		nextButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				goToNextPeriod();
			}
		});
		
		refreshFilterValue();
	}
	
	public void setValue(Period period, Date date){
		
		if(periodSelectorComboBox.getItems().contains(period)){
			currentCalendar.setTime(date);
			periodSelectorComboBox.getSelectionModel().select(period);
			
			refreshFilterValue();
		}
	}
	
	public void goToPreviousPeriod(){
		if(Period.MONTH.equals(getSelectedPeriod())){
			currentCalendar.add(GregorianCalendar.MONTH, -1);
			refreshFilterValue();

		} 
		else if (Period.YEAR.equals(getSelectedPeriod())){
			
			currentCalendar.add(GregorianCalendar.YEAR, -1);
			refreshFilterValue();
		}
	}
	
	public void goToNextPeriod(){
		if(Period.MONTH.equals(getSelectedPeriod())){					
			
			currentCalendar.add(GregorianCalendar.MONTH, 1);
			refreshFilterValue();

		} 
		else if (Period.YEAR.equals(getSelectedPeriod())){
			
			currentCalendar.add(GregorianCalendar.YEAR, 1);
			refreshFilterValue();
		}
	}
	
	public void setItems(Period ...periods){
		
		periodSelectorComboBox.getItems().clear();
		
		if(periods.length > 0){
			
			periodSelectorComboBox.getItems().addAll(periods);			
			periodSelectorComboBox.getSelectionModel().selectFirst();
			
			periodSelectorComboBox.setDisable(periods.length == 1);
		}
		
		this.refresh();
	}
	
	public void refresh(){
		refreshFilterValue();
	}
	
	private void enableDisableNextButton(){
		
		GregorianCalendar now = new GregorianCalendar();
		
		if(Period.MONTH.equals(getSelectedPeriod())){
			
			if(now.get(GregorianCalendar.YEAR) == currentCalendar.get(GregorianCalendar.YEAR)
					&& now.get(GregorianCalendar.MONTH) == currentCalendar.get(GregorianCalendar.MONTH)){
				nextButton.setDisable(true);
			} else {
				nextButton.setDisable(false);
			}

		} 
		else if (Period.YEAR.equals(getSelectedPeriod())){
			
			if(new GregorianCalendar().get(GregorianCalendar.YEAR) == currentCalendar.get(GregorianCalendar.YEAR)){
				nextButton.setDisable(true);
			} else {
				nextButton.setDisable(false);
			}
		}
	}
	
	public void hidePeriodValueControllers(){
		periodValueControllerHBox.setVisible(false);
	}
	
	private void refreshFilterValue(){
		
		if(Period.ALL.equals(getSelectedPeriod())){
			
			startDate = null;
			endDate = null;
			
			periodValueControllerHBox.setVisible(false);
			
		} else {
			
			if(Period.MONTH.equals(getSelectedPeriod())){
				Month currentMonth = Month.of(1+currentCalendar.get(GregorianCalendar.MONTH));
				String monthStr = StringUtils.capitalize(currentMonth.getDisplayName(TextStyle.FULL, Locale.getDefault()));
				
				periodSelectorValueLabel.setText(monthStr + " " + currentCalendar.get(GregorianCalendar.YEAR));
				
				GregorianCalendar workingCalendar = new GregorianCalendar();
				workingCalendar.setTime(currentCalendar.getTime());
				workingCalendar.set(GregorianCalendar.DAY_OF_MONTH, currentCalendar.getActualMinimum(GregorianCalendar.DAY_OF_MONTH));
				
				startDate = workingCalendar.getTime();
				
				workingCalendar.set(GregorianCalendar.DAY_OF_MONTH, currentCalendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
				
				endDate = workingCalendar.getTime();
				
			} 
			else if (Period.YEAR.equals(getSelectedPeriod())){
				
				periodSelectorValueLabel.setText(String.valueOf(currentCalendar.get(GregorianCalendar.YEAR)));
				
				GregorianCalendar workingCalendar = new GregorianCalendar();
				workingCalendar.setTime(currentCalendar.getTime());
				workingCalendar.set(GregorianCalendar.MONTH, GregorianCalendar.JANUARY);
				workingCalendar.set(GregorianCalendar.DAY_OF_MONTH, 1);
				
				startDate = workingCalendar.getTime();
				
				workingCalendar.set(GregorianCalendar.MONTH, GregorianCalendar.DECEMBER);
				workingCalendar.set(GregorianCalendar.DAY_OF_MONTH, 31);
				
				endDate = workingCalendar.getTime();
			}
			
			periodValueControllerHBox.setVisible(true);
		}
		
		enableDisableNextButton();
		
		fireEvent(new Event(null, this, CHANGED_VALUE));
	}

	public Period getSelectedPeriod(){
		return periodSelectorComboBox.getSelectionModel().getSelectedItem();
	}
	
	public Date getPeriodStartDate(){
		return this.startDate;
	}
	
	public Date getPeriodEndDate(){
		return this.endDate;
	}
	
	public String getPeriodLabel(){
		return this.periodSelectorValueLabel.getText();
	}
}
