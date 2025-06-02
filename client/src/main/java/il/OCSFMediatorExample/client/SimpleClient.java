package il.OCSFMediatorExample.client;

import il.OCSFMediatorExample.client.ocsf.ObservableClient;
import il.OCSFMediatorExample.entities.Item;
import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class SimpleClient {

	private static ObservableClient client;

	public static ObservableClient getClient() {
		if (client == null) {
			client = new ObservableClient("tcp://7.tcp.eu.ngrok.io:19057 -> localhost:3000 ", 3000);

			client.addObserver(new Observer() {
				@Override
				public void update(Observable o, Object msg) {
					if (msg instanceof List<?>) {
						List<?> list = (List<?>) msg;
						if (!list.isEmpty() && list.get(0) instanceof Item) {
							@SuppressWarnings("unchecked")
							List<Item> items = (List<Item>) list;
							EventBus.getDefault().post(items);
						}
					} else if (msg instanceof String) {
						String strMsg = (String) msg;
						System.out.println("SERVER: " + strMsg);
					}
				}
			});

			try {
				client.openConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return client;
	}

	public static void sendToServerSafe(Object msg) {
		try {
			getClient().sendToServer(msg);
		} catch (Exception e) {
			System.err.println("Failed to send message to server: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
