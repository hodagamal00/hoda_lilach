package il.OCSFMediatorExample.client;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;    // *** וודאי שורה זו קיימת! ***
import javafx.scene.image.Image;      // *** וודאי שורה זו קיימת! ***
import javafx.scene.image.ImageView;   // *** וודאי שורה זו קיימת! ***
import javafx.stage.Stage;
import il.OCSFMediatorExample.entities.Item;
import javafx.scene.control.TextField;


import java.io.InputStream;

public class SecondaryController {

    @FXML private ImageView itemDetailImage; // זהו ה-fx:id מה-FXML של פרטי הפריט
    @FXML private Label itemNameLabel;
    @FXML private Label itemIdLabel;
    @FXML private Label itemTypeLabel;
    @FXML private Label itemPriceLabel;
    @FXML private Label itemDescriptionLabel;
    @FXML private Button backButton;
    @FXML private TextField newPriceField;
    @FXML private Button updatePriceButton;


    private Item currentItem;

    public void setItem(Item item) {
        this.currentItem = item;
        if (item != null) {
            // טעינת התמונה הגדולה עבור חלון הפרטים
            InputStream imageStream = getClass().getResourceAsStream( item.getImageUrl());
            if (imageStream != null) {
                Image image = new Image(imageStream);
                itemDetailImage.setImage(image);
            } else {
                System.out.println("Detail image not found for: " + item.getImageUrl());
                try (InputStream defaultImageStream = getClass().getResourceAsStream("/Items_images/default.png")) {
                    if (defaultImageStream != null) {
                        itemDetailImage.setImage(new Image(defaultImageStream));
                    } else {
                        System.out.println("Default image (default.png) not found for details. Ensure it exists.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // *** וודאי ששורות אלה מדויקות לגמרי ***
            // *** וודאי שכל שדות ה-Label מוגדרים כ-@FXML private Label ... ***
            itemNameLabel.setText("Name: " + item.getName());
            itemIdLabel.setText("ID: " + item.getId());
            itemTypeLabel.setText("Category: " + item.getCategory());
            itemPriceLabel.setText(String.format("Price: %.2f₪", item.getPrice()));
            itemDescriptionLabel.setText("Description: " + item.getDescription());
        }
    }

    @FXML
    public void handleBackButtonAction(ActionEvent event) {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void handleUpdatePriceAction() {
        try {
            double newPrice = Double.parseDouble(newPriceField.getText());
            String msg = "#updatePrice " + currentItem.getId() + " " + newPrice;
            System.out.println("Sending message to server: " + msg); // 🧪 בדיקת הדפסה
            SimpleClient.getClient().sendToServerSafe(msg);
        } catch (NumberFormatException e) {
            System.err.println("Invalid price entered");
        }
    }


}