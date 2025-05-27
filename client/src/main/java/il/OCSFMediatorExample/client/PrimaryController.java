package il.OCSFMediatorExample.client;

import il.OCSFMediatorExample.entities.Item;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PrimaryController {

	@FXML private GridPane gridPane;
	@FXML private ScrollPane scrollPane;

	@FXML
	public void initialize() {
		EventBus.getDefault().register(this);
		SimpleClient.getClient().sendToServerSafe("getCatalog");
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onCatalogReceived(List<Item> items) {
		Platform.runLater(() -> {
			gridPane.getChildren().clear();
			int column = 0;
			int row = 0;

			for (Item item : items) {
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/il/OCSFMediatorExample/client/item_card.fxml"));
					VBox card = loader.load();

					ImageView imageView = (ImageView) card.lookup("#itemImage");
					Text name = (Text) card.lookup("#itemName");
					Text price = (Text) card.lookup("#itemPrice");

					// טעינת התמונה של הפריט - גרסה מעודכנת עם הדפסות DEBUG
					String imagePath = "/il/OCSFMediatorExample/client/Items_images/" + item.getImageUrl();
					System.out.println("Trying to load image: " + imagePath);

					InputStream imageStream = getClass().getResourceAsStream(imagePath);

					if (imageStream != null) {
						System.out.println("Image found and loaded: " + item.getImageUrl());
						Image image = new Image(imageStream);
						imageView.setImage(image);
					} else {
						System.err.println("Image NOT found for: " + item.getImageUrl());
						System.err.println("Did you copy image to target/classes? Is name correct?");

						try (InputStream defaultImageStream = getClass().getResourceAsStream("/il/OCSFMediatorExample/client/Items_images/default.png")) {
							if (defaultImageStream != null) {
								imageView.setImage(new Image(defaultImageStream));
								System.out.println("Loaded default image instead.");
							} else {
								System.err.println("Default image not found either. Please add default.png.");
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					name.setText(item.getName());
					price.setText(String.format("Price: %.2f₪", item.getPrice()));

					card.setOnMouseClicked(event -> {
						try {
							showItemDetails(item);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});

					gridPane.add(card, column, row);
					column++;
					if (column == 3) {
						column = 0;
						row++;
					}

				} catch (Exception e) {
					System.err.println("Error loading item card for item: " + item.getName() + ": " + e.getMessage());
					e.printStackTrace();
				}
			}
		});
	}

	private void showItemDetails(Item item) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/il/OCSFMediatorExample/client/secondary.fxml"));
		Parent root = loader.load();
		SecondaryController secondaryController = loader.getController();
		secondaryController.setItem(item);

		Stage stage = new Stage();
		stage.setScene(new Scene(root));
		stage.setTitle("Item Details: " + item.getName());
		stage.show();
	}

	public void onClose() {
		EventBus.getDefault().unregister(this);
	}
}
