package il.OCSFMediatorExample.entities;

import java.io.Serializable;

public class Warning implements Serializable {

	private String message;

	public Warning(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
