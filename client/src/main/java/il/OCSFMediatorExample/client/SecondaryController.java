package il.OCSFMediatorExample.client;

import il.OCSFMediatorExample.entities.Item;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class SecondaryController {

    @FXML private ImageView itemDetailImage;
    @FXML private Label itemNameLabel;
    @FXML private Label itemIdLabel;
    @FXML private Label itemPriceLabel;
    @FXML private TextField newPriceField;
    @FXML private Button updatePriceButton;
    @FXML private Button backToCatalogButton;

    private Item item;

    public void setItem(Item item) {
        this.item = item;

        itemNameLabel.setText("Name: " + item.getName());
        itemIdLabel.setText("ID: " + item.getId());
        itemPriceLabel.setText(String.format("Current Price: ₪ %.2f", item.getPrice()));

        if (item.getImageData() != null) {
            ByteArrayInputStream bis = new ByteArrayInputStream(item.getImageData());
            Image image = new Image(bis);
            itemDetailImage.setImage(image);
        }
    }

    @FXML
    private void onUpdatePriceClicked() {
        String input = newPriceField.getText();
        try {
            double newPrice = Double.parseDouble(input);
            String message = "updatePrice:" + item.getId() + ":" + newPrice;
            SimpleClient.sendToServerSafe(message);
            itemPriceLabel.setText(String.format("Current Price: ₪ %.2f", newPrice));
        } catch (NumberFormatException e) {
            itemPriceLabel.setText("Invalid price!");
        }
    }

    @FXML
    private void onBackToCatalogClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/il/OCSFMediatorExample/client/primary.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) itemIdLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
