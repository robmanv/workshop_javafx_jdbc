package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Seller entity;
	
	private SellerService service;
	
	// Estou criando uma lista de objetos que desejam se inscrever para receber o evento de mudan�a de dados dessa classe (VIDE LISTENER / OBSERVER)
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
	
	public Seller getEntity() {
		return entity;
	}

	public void setSeller(Seller entity) {
		this.entity = entity;
	}
	
	public void setSellerService(SellerService service) {
		this.service = service;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {   // Objetos v�o se inscrever para receber eventos desta classe
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
			notifyDataChangeListeners();  // serve pra emitir evento na mudan�a de dados para todos os objetos da lista (classe que EMITE O EVENTO)
			Utils.currentStage(event).close();
		}
		catch (ValidationException e) {
			setErrorMessages(e.getErrors());  // vai pegar o meu throw exception do getFormData e usara o metodo getErrors pra trazer o Map com os erros adicionados nas valida��es do 
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

	private Seller getFormData() {          ///Pega os dados do formul�rio e retorna o objeto
		Seller obj = new Seller();
		
		ValidationException exception = new ValidationException("Validation error");
		
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {   /// o trim remove espa�os em branco no inicio e fim 
			exception.addError("name",  "Field can�t be empty");
		}
		
		obj.setCodigo(Utils.tryParseToInt(txtId.getText()));
		obj.setNome(txtName.getText());
		
		if (exception.getErrors().size() > 0) {  // se adicionei algum erro no Map que est� na classe ValidationException, vou lan�ar a exce��o exception, que extende runtime exception 
			throw exception;
		}
		
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
		
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getCodigo())); //Sempre tem que converter pra texto se necess�rio.
		txtName.setText(entity.getNome());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		if (entity.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault())); // transformar data sql em instant e pega o fuso horario do pc da pessoa que usar o sistema
		}
	}

	public void setErrorMessages(Map<String, String> errors) {
		Set <String> fields = errors.keySet();  //pra varrer o Map, crio um vetor com as chaves de string.
		
		if (fields.contains("name")) {      // verifico se no keyset possui a chave "name", se houver eu formato o labelErrorName com a mensagem de erro do "name"
			labelErrorName.setText(errors.get("name"));
		}
	}

}
