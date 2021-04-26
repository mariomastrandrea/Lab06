package it.polito.tdp.meteo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class EntryPoint extends Application 
{
    @Override
    public void start(Stage stage) throws Exception 
    {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Scene_lab06.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        @SuppressWarnings("unused")
		FXMLController controller = loader.getController();
        // MyModel newModel = new MyModel();
        // controller.setModel(newModel);
        
        stage.setTitle("Lab06 - Meteo");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        launch(args);
    }

}
