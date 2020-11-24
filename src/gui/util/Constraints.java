package gui.util;

import javafx.scene.control.TextField;

public class Constraints {
	public static void setTextFieldInteger(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && !newValue.matches("\\d*"))  {
				txt.setText(oldValue);
			}
		});
	}
	
	public static void setTextFieldMaxLength(TextField txt, int max) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && newValue.length() > max)  {
				txt.setText(oldValue);
			}
		});
	}
	
	public static void setTextFieldDouble (TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && !newValue.matches("\\d*([\\.]\\d*)?"))  {
				txt.setText(oldValue);
			}
		});
	}
	
	//Eu que criei, basicamente só valida numérico com barras
	public static void setTextFieldDate (TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && !newValue.matches("(\\d{1,2}\\/)"))  {
				if (txt.getLength() == 3) {
					txt.setText(oldValue);
				}
			}
			if (newValue != null && !newValue.matches("(\\d{1,2}\\/\\d{1,2}\\/)"))  {
				if (txt.getLength() == 6) {
					txt.setText(oldValue);
				}
			}
			if (newValue != null && !newValue.matches("(\\d{1,2}\\/\\d{1,2}\\/\\d{4})"))  {
				if (txt.getLength() == 10) {
					txt.setText(oldValue);
				}
			}
		});
	}
	

}
