/**
 * 
 */
package s373.apache;

import java.util.ArrayList;

/**
 * @author a
 * 
 */
public class ApacheClient {

	public Apache apache;
	public String statusStr = "";
	public String replyStr = "";
	public String requestStr = "";
	Thread apacheThread;

	public ApacheClient(String server) {
		apache = new Apache(server);
		apacheThread = new Thread(apache);// .start();
		apacheThread.start();
	}

	public ApacheClient(String server, String php) {
		apache = new Apache(server, php);
		apacheThread = new Thread(apache);// .start();
		apacheThread.start();
	}

	public ApacheClient(String server, int t) {
		apache = new Apache(server, t);
		apacheThread = new Thread(apache);// .start();
		apacheThread.start();
	}

	public ApacheClient(String server, String php, int t) {
		apache = new Apache(server, php, t);
		apacheThread = new Thread(apache);// .start();
		apacheThread.start();
	}

	public void setPath(String server, String php) {
		apache.setPath(server, php);
	}

	public void setPath(String server) {
		apache.setPath(server, "");
	}

	public String getInfo() {
		String info = "" + apache + "\n";
		statusStr = apache.getStatusStr();
		requestStr = apache.getRequestStr();
		replyStr = apache.getReplyStr();
		info += requestStr + "\n";
		info += statusStr + "\n";
		info += replyStr + "\n";
		return info;
	}

	public int qPOST(String args[]) {
		int result = apache.qPOST(args);
		return result;
	}

	public String POST(String args[]) {
		return apache.POST(args);
	}

	public String[] available() {

		String ret[] = { null };
		ArrayList strs = new ArrayList();
		strs = apache.available();

		if (strs.size() > 0) {
			ret = new String[strs.size()];
			for (int i = 0; i < strs.size(); i++) {
				ret[i] = (String) strs.get(i);
			}
		}

		return ret;

	}

}
