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
				client.sendToClient(items);
			} else {
				client.sendToClient("Unknown command");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}