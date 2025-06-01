package il.OCSFMediatorExample.client;

import il.OCSFMediatorExample.entities.Item;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;

public class PrimaryController {

	@FXML private GridPane gridPane;
	@FXML private ScrollPane scrollPane;

	private List<Item> currentItems;

	@FXML
	public void initialize() {
		EventBus.getDefault().register(this);
		SimpleClient.sendToServerSafe("getCatalog");

		// إعادة ترتيب الكروت عند تغيير حجم النافذة
		scrollPane.widthProperty().addListener((obs, oldVal, newVal) -> {
			if (currentItems != null) {
				renderCatalog(currentItems);
			}
		});
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onCatalogReceived(List<Item> items) {
		Platform.runLater(() -> {
			currentItems = items;
			renderCatalog(items);
		});
	}

	private void renderCatalog(List<Item> items) {
		gridPane.getChildren().clear();
		int column = 0;
		int row = 0;

		double cardWidth = 250; // تقريبي: عرض البطاقة + مسافات
		int cardsPerRow = Math.max(1, (int) (scrollPane.getWidth() / cardWidth));

		for (Item item : items) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/il/OCSFMediatorExample/client/item_card.fxml"));
				VBox card = loader.load();

				ItemCardController controller = loader.getController();
				controller.setItem(item);

				card.setOnMouseClicked(e -> showItemDetails(item));

				gridPane.add(card, column, row);
				column++;
				if (column >= cardsPerRow) {
					column = 0;
					row++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
