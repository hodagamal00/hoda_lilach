package il.OCSFMediatorExample.client;

import il.OCSFMediatorExample.entities.Item;
import javafx.application.Platform; // *** וודאי שורה זו קיימת! ***
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

	// מתודה המופעלת כאשר רשימת פריטים מתקבלת מהשרת
	@Subscribe(threadMode = ThreadMode.MAIN) // EventBus מנסה להפעיל את זה על התרד הראשי
	public void onCatalogReceived(List<Item> items) {
		// *** חובה לעטוף עדכוני GUI ב-Platform.runLater() ***
		Platform.runLater(() -> {
			gridPane.getChildren().clear(); // מנקה את ה-GridPane לפני הוספת פריטים חדשים
			int column = 0;
			int row = 0;

			for (Item item : items) {
				try {
					// טוען את קובץ ה-FXML עבור כרטיס פריט בודד
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/il/OCSFMediatorExample/client/item_card.fxml"));
					VBox card = loader.load(); // ה-VBox הוא ה-Root של item_card.fxml

					// מאתר את הרכיבים בתוך כרטיס הפריט ומאכלס אותם בנתוני הפריט
					ImageView imageView = (ImageView) card.lookup("#itemImage");
					Text name = (Text) card.lookup("#itemName");
					Text price = (Text) card.lookup("#itemPrice");

					// טעינת התמונה של הפריט
					System.out.println("Attempting to load image from path: /Items_images/" + item.getImageUrl());
					InputStream imageStream = getClass().getResourceAsStream("/il/OCSFMediatorExample/client/Items_images/" + item.getImageUrl());					if (imageStream != null) {
						System.out.println("imageStream is null? " + (imageStream == null));

						Image image = new Image(imageStream);
						imageView.setImage(image);
					} else {
						System.out.println("Image not found for: " + item.getImageUrl());
						// אם התמונה לא נמצאה, מנסה לטעון תמונת ברירת מחדל
						try (InputStream defaultImageStream = getClass().getResourceAsStream("/Items_images/default.png")) {
							if (defaultImageStream != null) {
								imageView.setImage(new Image(defaultImageStream));
							} else {
								System.out.println("Default image (default.png) not found. Ensure default.png exists in /Items_images/.");
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					name.setText(item.getName());
					price.setText(String.format("Price: %.2f₪", item.getPrice()));

					// הגדרת Event Listener ללחיצה על כרטיס הפריט
					card.setOnMouseClicked(event -> {
						try {
							showItemDetails(item); // קורא למתודה לפתיחת חלון פרטי הפריט
						} catch (IOException e) {
							e.printStackTrace();
						}
					});

					// מוסיף את כרטיס הפריט ל-GridPane
					gridPane.add(card, column, row);
					column++;
					if (column == 3) { // מגדיר 3 פריטים בשורה
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

	// מתודה לפתיחת חלון חדש עם פרטי הפריט המלאים
	private void showItemDetails(Item item) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/il/OCSFMediatorExample/client/secondary.fxml"));
		Parent root = loader.load();
		SecondaryController secondaryController = loader.getController();
		secondaryController.setItem(item); // העברת אובייקט ה-Item

		Stage stage = new Stage();
		stage.setScene(new Scene(root));
		stage.setTitle("Item Details: " + item.getName());
		stage.show();
	}

	public void onClose() {
		EventBus.getDefault().unregister(this);
	}
}