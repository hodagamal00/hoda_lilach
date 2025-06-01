package il.OCSFMediatorExample.client;

import il.OCSFMediatorExample.entities.Item;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public class PrimaryController {

	@FXML private GridPane gridPane;
	@FXML private ScrollPane scrollPane;

	@FXML
	public void initialize() {
		EventBus.getDefault().register(this);
		SimpleClient.sendToServerSafe("getCatalog");
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onCatalogReceived(List<Item> items) {
		Platform.runLater(() -> {
			gridPane.getChildren().clear();
			int column = 0;
			int row = 0;

			for (Item item : items) {
				VBox box = createItemBox(item);
				gridPane.add(box, column, row);

				column++;
				if (column == 2) {
					column = 0;
					row++;
				}
			}
		});
	}

	private VBox createItemBox(Item item) {
		VBox vbox = new VBox();
		vbox.setSpacing(5);

		ImageView imageView = new ImageView();
		imageView.setFitWidth(150);
		imageView.setFitHeight(150);
		imageView.setPreserveRatio(true);

		if (item.getImageData() != null) {
			ByteArrayInputStream bis = new ByteArrayInputStream(item.getImageData());
			imageView.setImage(new Image(bis));
		}

		Text name = new Text(item.getName());
		Text price = new Text(String.format("₪ %.2f", item.getPrice()));

		vbox.getChildren().addAll(imageView, name, price);

		// Optional: make the box clickable
		vbox.setOnMouseClicked(e -> showItemDetails(item));

		return vbox;
	}

	private void showItemDetails(Item item) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/il/OCSFMediatorExample/client/secondary.fxml"));
			Parent root = loader.load();

			SecondaryController controller = loader.getController();
			controller.setItem(item);

			Stage stage = (Stage) gridPane.getScene().getWindow();
			stage.setScene(new Scene(root));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
