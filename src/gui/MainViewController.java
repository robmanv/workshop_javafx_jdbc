package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;
import model.services.SellerService;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemSeller;
	
	@FXML
	private MenuItem menuItemDepartment;
	
	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	private void onMenuItemSellerAction() {
		loadView("/gui/SellerList.fxml", (SellerListController controller) -> {
			controller.setSellerService(new SellerService());
			controller.updateTableView();
			
		});
	}
	
	@FXML
	private void onMenuItemDepartmentAction() {
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
			
		});
	}

	@FXML
	private void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {}); // x leva em nada, informo pq é obrigatório informar sempre vazio ou preenchido
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		
		
	}
	
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {  // IMPORTANTE: O synchronized garante que todas as instruções abaixo não serão interrompidas em multi-threading
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			Scene mainScene = Main.getMainsScene();
			
			
			// Explicativo do processo abaixo:
			// 1- Na hierarquia temos ScrollPane -> VBox -> MenuBar
			// 2- Pega a mainscene, static declarada no application Main, com getRoot() vai trazer o primeiro elemento Scene "Scrollpane" e getContent() pega o 1o conteúdo, VBox
			// 3- Na Vbox salva em mainVBox, pego o 1o elemento na children, no caso, menuBar, e salvo no tipo node (tudo entre <> é nodo), e salvo em mainMenu.
			// 4- Limpo todas as filhas (children) em mainVBox.
			// 5- Adiciono novamente só o menuBar salvo em mainMenu
			// 6- Adiciono todas as ocorrencias filhas (chieldren) em newVBox, no caso, está com o About.
			
			
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent(); // pega o primeiro elemento da view, no caso, o ScrollPane
			
			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			//NIVEL AVANÇADO:
			//Pego a tela MAIN do tipo FXMLLoader, salvando em um tipo GENERICS T
			//A variável CONSUMER vindo por parâmetro (lembrando em expressão lambda, CONSUMER executa mudança no objeto conforme função)
			// obs.: relembre das aulas de PREDICATE e CONSUMER em expressões lambda
			T controller = loader.getController(); // Retorna controle T
			initializingAction.accept(controller); // Executa função lambda recebida no método
			


		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Erro loading view", e.getMessage(), AlertType.ERROR);
		}
	}


}
