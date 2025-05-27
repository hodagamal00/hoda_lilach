package il.OCSFMediatorExample.client;

import il.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.OCSFMediatorExample.entities.Item;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

public class SimpleClient extends AbstractClient {

	private static SimpleClient client;

	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient("192.168.252.47", 3000); // ❗ שנה את הכתובת אם את עובדת ברשת
			try {
				client.openConnection();
			} catch (Exception e) {
				System.err.println("CLIENT: Failed to connect to server.");
				e.printStackTrace();
			}
		}
		return client;
	}

	public SimpleClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		System.out.println("CLIENT: Message received from server: " + msg.toString());

		if (msg instanceof String) {
			String strMsg = (String) msg;

			if (strMsg.startsWith("#warning")) {
				EventBus.getDefault().post(new WarningEvent(strMsg));
			} else if (strMsg.startsWith("Price updated")) {
				System.out.println("✅ SERVER: " + strMsg); // הצלחה בעדכון מחיר
			} else if (strMsg.startsWith("Price update failed") || strMsg.startsWith("Invalid")) {
				System.err.println("❌ SERVER ERROR: " + strMsg);
			}

		} else if (msg instanceof List) {
			List<?> list = (List<?>) msg;
			if (!list.isEmpty() && list.get(0) instanceof Item) {
				@SuppressWarnings("unchecked")
				List<Item> items = (List<Item>) list;
				EventBus.getDefault().post(items);
			}
		}
	}

	public void sendToServerSafe(Object msg) {
		try {
			if (isConnected()) {
				sendToServer(msg);
			} else {
				System.err.println("CLIENT: Not connected to server. Cannot send message.");
			}
		} catch (IOException e) {
			System.err.println("CLIENT: Failed to send message to server: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
