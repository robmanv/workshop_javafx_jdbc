package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemSeller;
	
	@FXML
	private MenuItem menuItemDepartment;
	
	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	private void onMenuItemSellerAction() {
		System.out.println("onMenuItemSellerAction");
	}
	
	@FXML
	private void onMenuItemDepartmentAction() {
		loadView2("/gui/DepartmentList.fxml");
	}

	@FXML
	private void onMenuItemAboutAction() {
		loadView("/gui/About.fxml");
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		
		
	}
	
	private synchronized void loadView(String absoluteName) {  // IMPORTANTE: O synchronized garante que todas as instruções abaixo não serão interrompidas em multi-threading
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

		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Erro loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	private synchronized void loadView2(String absoluteName) {  // IMPORTANTE: O synchronized garante que todas as instruções abaixo não serão interrompidas em multi-threading
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
			
			
			// Obtem o controle de FXMLLoader  declarado acima (Main)
			DepartmentListController controller = loader.getController();
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();

		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Erro loading view", e.getMessage(), AlertType.ERROR);
		}
	}
}
