package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class utils {

	public static Stage currentStage(ActionEvent event) {
		return (Stage) ((Node) event.getSource()).getScene().getWindow(); // Obter o Stage onde meu controller que recebeu o evento está
	}
	
	public static Integer tryParseToInt(String str) {
		try {
			return Integer.parseInt(str);   // Ou converte o número ou retorna nulo, pra não ocorrer exceção
		}
			catch (NumberFormatException e) {
				return null;
			}
	}
}
