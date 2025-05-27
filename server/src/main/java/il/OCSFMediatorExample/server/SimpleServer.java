package il.OCSFMediatorExample.server;

import il.OCSFMediatorExample.entities.Item;
import il.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.List;

public class SimpleServer extends AbstractServer {

	public SimpleServer(int port) {
		super(port);
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		System.out.println("Server received: " + msg);

		try {
			if ("getCatalog".equals(msg)) {
				List<Item> items = CatalogDAO.getAllItems();
				System.out.println("Sending catalog with " + items.size() + " items.");
				client.sendToClient(items);

			} else if (msg instanceof String && ((String) msg).startsWith("#updatePrice")) {
				String[] parts = ((String) msg).split(" ");
				if (parts.length == 3) {
					try {
						int itemId = Integer.parseInt(parts[1]);
						double newPrice = Double.parseDouble(parts[2]);

						boolean success = CatalogDAO.updateItemPrice(itemId, newPrice);
						if (success) {
							System.out.println("Price updated for item ID: " + itemId + " to " + newPrice);
							client.sendToClient("Price updated successfully");

							// שולח מחדש את הקטלוג המעודכן
							List<Item> updatedItems = CatalogDAO.getAllItems();
							client.sendToClient(updatedItems);
						} else {
							client.sendToClient("Price update failed");
						}

					} catch (NumberFormatException e) {
						client.sendToClient("Invalid input for price update.");
					}
				} else {
					client.sendToClient("Invalid command format for #updatePrice");
				}

			} else {
				client.sendToClient("Unknown command");
			}
		} catch (IOException e) {
			System.err.println("Error handling client message:");
			e.printStackTrace();
		}
	}
}
