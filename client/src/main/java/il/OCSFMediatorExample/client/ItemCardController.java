package il.OCSFMediatorExample.client;

import il.OCSFMediatorExample.entities.Item;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;

public class ItemCardController {

    @FXML private ImageView itemImage;
    @FXML private Label itemName;
    @FXML private Label itemPrice;

    private Item item;

    public void setItem(Item item) {
        this.item = item;
        itemName.setText(item.getName());
        itemPrice.setText(String.format("₪ %.2f", item.getPrice()));

        if (item.getImageData() != null) {
            ByteArrayInputStream bis = new ByteArrayInputStream(item.getImageData());
            itemImage.setImage(new Image(bis));
        }
    }

    public Item getItem() {
        return item;
    }
}
