package view;


import java.io.File;

import javafx.beans.property.Property;
import javafx.beans.property.StringPropertyBase;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import data.persistant.Persistent;

public class PersistentDialogController extends AnchorPane{
	@FXML
	Button buttonBrowse;
	@FXML
	Button buttonOk;
	@FXML
	Button buttonCancel;
	
	@FXML
	Label labelWarning;
	@FXML
	Label labelFilepath;
	
	Property<String> file = new StringPropertyBase() {
		
		public String getName() {
			// TODO Auto-generated method stub
			return "file";
		}
		
		public Object getBean() {
			// TODO Auto-generated method stub
			return null;
		}
	};
	
	DirectoryChooser fileChooser = new DirectoryChooser();
	
	@FXML 
	private void initialize() {
		file.setValue("");
		labelWarning.setText("");
		labelFilepath.setText("");
		
		labelFilepath.textProperty().bind(file);
		
		buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
			
			public void handle(ActionEvent event) {
				System.exit(0);				
			}
		});
		
		buttonOk.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				if (labelFilepath.getText().equals("")) {
					labelWarning.setText("No path set!");
				} else {
					Persistent.setFilePath(file.getValue());
					System.exit(0);
				}
				
			}
		});
		
		buttonBrowse.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				File fileBrowser = fileChooser.showDialog(null);
				if (fileBrowser != null) {
					file.setValue(fileBrowser.getAbsolutePath());
				}
				
			}
		});

	}
	
	

}