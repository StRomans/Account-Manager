package fr.coward.main.ui.components;

import com.jfoenix.controls.JFXTextField;

import fr.coward.main.ui.utils.StringUtil;

public class NumericField extends JFXTextField {
	
	private String WHOLE_REGEX = "[+]?[0-9]*\\.?[0-9]+";
	private String ALLOWED_CHAR_REGEX = "[+0-9\\.]?";

	@Override
    public void replaceText(int start, int end, String text)
    {
        if (validate(text))
        {
            super.replaceText(start, end, text);
        }
    }

    @Override
    public void replaceSelection(String text)
    {
        if (validate(text))
        {
            super.replaceSelection(text);
        }
    }

    private boolean validate(String text)
    {    	
        return ("".equals(text) || text.matches(ALLOWED_CHAR_REGEX));
    }
    
    public boolean isValid(){
    	return super.getText().matches(WHOLE_REGEX);
    }

	public double getDouble() {
		
		double value = 0.0;
		
		if(StringUtil.isNotNullNotEmpty(this.getText()) && isValid()){
			value = Double.parseDouble(this.getText());
		}
		
		return value;
	}
}
