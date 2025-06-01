package il.OCSFMediatorExample.client;

import il.OCSFMediatorExample.entities.Item;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class PrimaryController {

	@FXML private GridPane gridPane;
	@FXML private ScrollPane scrollPane;
	@FXML private ComboBox<String> categoryFilter;

	private List<Item> fullCatalog;

	@FXML
	public void initialize() {
		EventBus.getDefault().register(this);
		SimpleClient.sendToServerSafe("getCatalog");
	}

	@Subscribe
	public void onCatalogReceived(List<Item> updatedCatalog) {
		Platform.runLater(() -> {
			fullCatalog = updatedCatalog;
			updateCatalogDisplay(updatedCatalog);
			updateCategoryFilter(updatedCatalog);
		});
	}

	private void updateCategoryFilter(List<Item> items) {
		List<String> categories = items.stream()
				.map(Item::getCategory)
				.distinct()
				.sorted()
				.collect(Collectors.toList());

		categories.add(0, "All");
		categoryFilter.setItems(FXCollections.observableArrayList(categories));
		categoryFilter.setPromptText("Select Category");
	}


	@FXML
	private void onCategorySelected() {
		String selected = categoryFilter.getValue();
		if (selected != null && !selected.equals("All")) {
			List<Item> filtered = fullCatalog.stream()
					.filter(item -> item.getCategory().equals(selected))
					.collect(Collectors.toList());
			updateCatalogDisplay(filtered);
		} else {
			updateCatalogDisplay(fullCatalog);
		}

	}

	private void updateCatalogDisplay(List<Item> items) {
		gridPane.getChildren().clear();
		int column = 0;
		int row = 0;

		for (Item item : items) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("item_card.fxml"));
				VBox card = loader.load();

				ItemCardController controller = loader.getController();
				controller.setData(item);

				card.setOnMouseClicked(event -> openItemDetails(item));  // ✅ أضف هذا

				gridPane.add(card, column, row);
				column++;
				if (column == 4) {
					column = 0;
					row++;
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void openItemDetails(Item item) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("secondary.fxml"));
			VBox itemDetailRoot = loader.load();

			SecondaryController controller = loader.getController();
			controller.setItem(item);

			scrollPane.getScene().setRoot(itemDetailRoot);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
