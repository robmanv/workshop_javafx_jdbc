package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable {

	private DepartmentService service;
	private ObservableList<Department> obsList;
	
	@FXML
	private TableView<Department> tableViewDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnCodigo;
	
	@FXML
	private TableColumn<Department, String> tableColumnNome;
	
	@FXML
	private Button btNew;
	
	
	@FXML 
	private void onBtNewAction() {
		System.out.println("onBtNewAction");
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		
	}

	private void initializeNodes() {
		tableColumnCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo")); // ATENÇÃO: Usado pelo ObservableList, pega exatamente o nome do campo do GETTER E SETTER da CLASSE
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));     // Pode ser minusculo ou maiusculo, no primeiro caracter 
		
		Stage stage = (Stage) Main.getMainsScene().getWindow(); 
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty()); //relaciono a mudança do tamanho da table do Departament com o tamanho da tela Main.
		tableViewDepartment.prefWidthProperty().bind(stage.widthProperty()); //relaciono a mudança do tamanho da table do Departament com o tamanho da tela Main.
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		
		List<Department> list = service.findAll(); //Trazer todos os departamentos mockados do serviço
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsList);
	}

	
	

}
