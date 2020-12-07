package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class utils {

	public static Stage currentStage(ActionEvent event) {
		return (Stage) ((Node) event.getSource()).getScene().getWindow(); // Obter o Stage onde meu controller que recebeu o evento est�
	}
	
	public static Integer tryParseToInt(String str) {
		try {
			return Integer.parseInt(str);   // Ou converte o n�mero ou retorna nulo, pra n�o ocorrer exce��o
		}
			catch (NumberFormatException e) {
				return null;
			}
	}
}
