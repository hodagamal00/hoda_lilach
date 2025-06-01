package il.OCSFMediatorExample.server;

import il.OCSFMediatorExample.entities.Item;
import il.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimpleServer extends AbstractServer {

	private final List<ConnectionToClient> subscribers = new CopyOnWriteArrayList<>();

	public SimpleServer(int port) {
		super(port);
	}

	@Override
	protected void clientConnected(ConnectionToClient client) {
		super.clientConnected(client);
		subscribers.add(client);
		System.out.println("Client connected and added to subscribers: " + client);
	}

	@Override
	protected synchronized void clientDisconnected(ConnectionToClient client) {
		subscribers.remove(client);
		System.out.println("Client disconnected and removed from subscribers: " + client);
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		System.out.println("Server received: " + msg);

		try {
			if ("getCatalog".equals(msg)) {
				List<Item> items = CatalogDAO.getAllItems();
				client.sendToClient(items);
			}

			// Format: updatePrice:<id>:<newPrice>
			else if (msg instanceof String && ((String) msg).startsWith("updatePrice:")) {
				String[] parts = ((String) msg).split(":");
				int id = Integer.parseInt(parts[1]);
				double newPrice = Double.parseDouble(parts[2]);

				boolean updated = CatalogDAO.updateItemPrice(id, newPrice);
				if (updated) {
					client.sendToClient("Price updated successfully for item ID " + id);

					// Notify all clients with updated catalog
					List<Item> updatedCatalog = CatalogDAO.getAllItems();
					broadcastToSubscribers(updatedCatalog); // ✅ now declared correctly below
				} else {
					client.sendToClient("Price update failed for item ID " + id);
				}
			}

		} catch (IOException | NumberFormatException e) {
			e.printStackTrace();
		}
	}

	private void broadcastToSubscribers(Object msg) {
		for (ConnectionToClient client : subscribers) {
			try {
				client.sendToClient(msg);
			} catch (IOException e) {
				System.err.println("Failed to notify client: " + client);
			}
		}
	}
}
