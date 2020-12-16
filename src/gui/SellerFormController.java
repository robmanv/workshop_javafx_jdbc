package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Seller entity;
	
	private SellerService service;
	
	private DepartmentService departmentService;
	
	// Estou criando uma lista de objetos que desejam se inscrever para receber o evento de mudança de dados dessa classe (VIDE LISTENER / OBSERVER)
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	private DatePicker dpBirthDate;
	
	@FXML
	private TextField txtBaseSalary;
	
	@FXML
	private ComboBox<Department> comboBoxDepartment;

	@FXML
	private Label labelErrorName;
	
	@FXML
	private Label labelErrorEmail;
	
	@FXML
	private Label labelErrorBirthDate;
	
	@FXML
	private Label labelErrorBaseSalary;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	private ObservableList<Department> obsList; //Carregar lista com os departamentos do Banco de Dados (vou criar o método loadAssociatedObjects)
	
	public Seller getEntity() {
		return entity;
	}

	public void setSeller(Seller entity) {
		this.entity = entity;
	}
	
	public void setServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {   // Objetos vão se inscrever para receber eventos desta classe
		dataChangeListeners.add(listener);
	}
	@FXML
	public void onBtSaveAction(ActionEvent event) {
//		System.out.println("onBtSaveAction");
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();  // serve pra emitir evento na mudança de dados para todos os objetos da lista (classe que EMITE O EVENTO)
			Utils.currentStage(event).close();
		}
		catch (ValidationException e) {
			setErrorMessages(e.getErrors());  // vai pegar o meu throw exception do getFormData e usara o metodo getErrors pra trazer o Map com os erros adicionados nas validações do 
			                                  // getFormData, e o setErrorMessages vai mover o texto pra label da tela
			                                  // Toda classe personalizada pra tratar erros extende a runtimeException, pra poder dar um throw no erro e tratar de forma personalizada
		}
		catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Seller getFormData() {          ///Pega os dados do formulário e retorna o objeto
		Seller obj = new Seller();
		
		ValidationException exception = new ValidationException("Validation error");
		
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {   /// o trim remove espaços em branco no inicio e fim 
			exception.addError("name",  "Field can´t be empty");
		}
		
		obj.setCodigo(Utils.tryParseToInt(txtId.getText()));
		obj.setNome(txtName.getText());

		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {   /// o trim remove espaços em branco no inicio e fim 
			exception.addError("email",  "Field can´t be empty");
		}
		
		obj.setEmail(txtEmail.getText());
		
		if (dpBirthDate.getValue() == null) {
			exception.addError("birthDate",  "Field can´t be empty");
		} else {
			Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setBirthDate(Date.from(instant));
		}
		
		if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals("")) {   /// o trim remove espaços em branco no inicio e fim 
			exception.addError("baseSalary",  "Field can´t be empty");
		}
		
		obj.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));
		
		if (exception.getErrors().size() > 0) {  // se adicionei algum erro no Map que está na classe ValidationException, vou lançar a exceção exception, que extende runtime exception 
			throw exception;
		}
		
		obj.setDepartment(comboBoxDepartment.getValue());
		
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		
	}
	
	private void initializeNodes() {                       // Corrigir em tempo real texto digitado
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 70);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		initializeComboBoxDepartment();
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getCodigo())); //Sempre tem que converter pra texto se necessário.
		txtName.setText(entity.getNome());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		if (entity.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault())); // transformar data sql em instant e pega o fuso horario do pc da pessoa que usar o sistema
		}
		
		if (entity.getDepartment() == null) {
			// Se for vendedor novo sem departamento, pegarei 1o elemento do combo
			comboBoxDepartment.getSelectionModel().selectFirst();
		} else {
			comboBoxDepartment.setValue(entity.getDepartment());
		}
		
	}

	public void loadAssociatedObjects() {
		if (departmentService == null) {
			throw new IllegalStateException("DepartmentService was null");
		}
		
		List<Department> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list); //Carregar obsList com a lista de objetos 
		comboBoxDepartment.setItems(obsList); // setar o obslist no combo
	}
	
	public void setErrorMessages(Map<String, String> errors) {
		Set <String> fields = errors.keySet();  //pra varrer o Map, crio um vetor com as chaves de string.
		
		if (fields.contains("name")) {      // verifico se no keyset possui a chave "name", se houver eu formato o labelErrorName com a mensagem de erro do "name"
			labelErrorName.setText(errors.get("name"));
		} else {
			labelErrorName.setText("");
		}
		
		// labelErrorName.setText(fields.contains("name") ? errors.get("name") : ""); // dá pra trocar todo o if acima por essa linha
		
		if (fields.contains("email")) {      // verifico se no keyset possui a chave "name", se houver eu formato o labelErrorName com a mensagem de erro do "name"
			labelErrorEmail.setText(errors.get("email"));
		} else {
			labelErrorEmail.setText("");
		}
		
		if (fields.contains("baseSalary")) {      // verifico se no keyset possui a chave "name", se houver eu formato o labelErrorName com a mensagem de erro do "name"
			labelErrorBaseSalary.setText(errors.get("baseSalary"));
		} else {
			labelErrorBaseSalary.setText("");
		}

		if (fields.contains("birthDate")) {      // verifico se no keyset possui a chave "name", se houver eu formato o labelErrorName com a mensagem de erro do "name"
			labelErrorBirthDate.setText(errors.get("birthDate"));
		} else {
			labelErrorBirthDate.setText("");
		}
}
	
	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}

}
