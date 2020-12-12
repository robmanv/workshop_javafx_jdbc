package gui;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {

	private SellerService service;
	private ObservableList<Seller> obsList;
	
	@FXML
	private TableView<Seller> tableViewSeller;
	
	@FXML
	private TableColumn<Seller, Integer> tableColumnCodigo;
	
	@FXML
	private TableColumn<Seller, String> tableColumnNome;
	
	@FXML
	private TableColumn<Seller, String> tableColumnEmail;
	
	@FXML
	private TableColumn<Seller, Date> tableColumnBirthDate;
	
	@FXML
	private TableColumn<Seller, Double> tableColumnBaseSalary;
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT;
	
	@FXML
	private Button btNew;
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnREMOVE;
	
	
	@FXML 
	private void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Seller obj = new Seller();
		createDialogForm(obj, "/gui/SellerForm.fxml", parentStage);
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		
	}

	private void initializeNodes() {
		tableColumnCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo")); // ATENÇÃO: Usado pelo ObservableList, pega exatamente o nome do campo do GETTER E SETTER da CLASSE
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));     // Pode ser minusculo ou maiusculo, no primeiro caracter 
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));       // Exatamente o nome do atributo do SELLER
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));  // Exatamente o nome do atributo do SELLER 
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));   // Exatamente o nome do atributo do SELLER
		Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);
		
		
		Stage stage = (Stage) Main.getMainsScene().getWindow(); 
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty()); //relaciono a mudança do tamanho da table do Departament com o tamanho da tela Main.
		tableViewSeller.prefWidthProperty().bind(stage.widthProperty()); //relaciono a mudança do tamanho da table do Departament com o tamanho da tela Main.
	}
	
	public void setSellerService(SellerService service) {
		this.service = service;
	}
	
	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		
		List<Seller> list = service.findAll(); //Trazer todos os departamentos mockados do serviço
		obsList = FXCollections.observableArrayList(list);
		tableViewSeller.setItems(obsList);
		
		initEditButtons(); // Isso vai adicionar um novo texto com botão edit, e sempre que clicar vai abrir o SellerFOrm.
		initRemoveButtons(); // criar os botões de remove
	}

	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) {
//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
//			Pane pane = loader.load();
//			
//			SellerFormController controller = loader.getController(); // Pegar o controlador da tela que carregou, o formulário.
//			controller.setSeller(obj); // injetar o objeto Seller no meu controlador SellerFormController
//			controller.setSellerService(new SellerService()); // injetar a dependencia do SellerService (que tem o acesso ao BD, na classe controller)
//			controller.subscribeDataChangeListener(this); // Inscrevendo o objeto ATUAL DEPARTMENTLISTCONTROLLER para ficar no LISTENER (alto DESACOPLAMENTO)
//			controller.updateFormData();
//			
//			Stage dialogStage = new Stage();
//			dialogStage.setTitle("Enter Seller data");
//			dialogStage.setScene(new Scene(pane));
//			dialogStage.setResizable(false); //pra não redimensionar a janela
//			dialogStage.initOwner(parentStage); 
//			dialogStage.initModality(Modality.WINDOW_MODAL); // evita clicar na tela anterior falando qual a tela pai
//			dialogStage.showAndWait();
//			
//		} catch (IOException e) {
//			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
//		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();
		
	}
	
	
	private void initEditButtons() {
			tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));  // estou definindo um valor inicial a partir do que está preenchido
			tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {          // estou definindo o campo como um objeto Seller, Seller
				private final Button button = new Button("edit");
				
				@Override
				protected void updateItem(Seller obj, boolean empty) {
					super.updateItem(obj, empty);
					
					if (obj == null) {
						setGraphic(null);
						return;
					}
					
					setGraphic(button); // vai adicionar o botão "edit" no formulário de lista chamando a tela de atualização do department
					button.setOnAction(
							event -> createDialogForm(
									obj, "/gui/SellerForm.fxml",Utils.currentStage(event)));
				}
			});
	}
	

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));  // estou definindo um valor inicial a partir do que está preenchido
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {          // estou definindo o campo como um objeto Seller, Seller
			private final Button button = new Button("remove");
			
			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				
				if (obj == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(button); // vai adicionar o botão "edit" no formulário de lista chamando a tela de atualização do department
				button.setOnAction(
						event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Seller obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?"); // o sim ou não vai pra variável resulta do Optional.
		
		if (result.get() == ButtonType.OK) {  // chama o objeto dentro do Optional, button
			if (service == null) {
				throw new IllegalStateException("Service was null");
			}
			
			try {
				service.remove(obj);
				updateTableView();
			} catch (DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
	
	

}
