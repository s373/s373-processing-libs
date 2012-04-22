/**
 * 
 */
package s373.apache;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 * @author andre sier
 * 
 *         Apache implementation of POST methods
 * 
 */
public class Apache implements Runnable {

	public String baseHttpUrl = "";
	public String phpUrl = "";
	public int threadTime = 100;// 250;
	public String fullPathUrl = "";
	public boolean running = false;

	public String statusStr = "";
	public String replyStr = "";
	public String requestStr = "";

	public boolean debug = true;

	// *action=loginUser
	// vars : userName

	public ArrayList posts = new ArrayList();
	public ArrayList results = new ArrayList();

	public Apache() {
		init();
	}

	// ///// main ariadne apache threader

	/**
	 * @param server
	 */
	public Apache(String server) {
		setPath(server, "");
		init();
	}

	public Apache(String server, int time) {
		threadTime = time;
		setPath(server, "");
		init();
	}

	/**
	 * @param server
	 * @param php
	 */
	public Apache(String server, String php) {
		setPath(server, php);
		init();
	}

	public Apache(String server, String php, int time) {
		threadTime = time;
		setPath(server, php);
		init();
	}

	public void init() {
		running = true;
		if (debug) {
			System.out.print("s373.apache initing @" + threadTime + "ms path: "
					+ fullPathUrl + "\n");
		}
	}

	public void setPath(String server, String php) {
		baseHttpUrl = server;
		phpUrl = php;
		fullPathUrl = baseHttpUrl + phpUrl;
		if (debug) {
			System.out.print("s373.apache path: " + fullPathUrl + "\n");
		}
	}

	public int getPostsSize() {
		return posts.size();
	}

	public int getResultsSize() {
		return results.size();
	}

	public void run() {
		// System. sleep(threadTime);

		while (running) {

			// if(debug){
			// System.out.print("s373.apache running @"+System.currentTimeMillis()+"\n");
			// }

			if (posts.size() > 0) {

				try {

					DefaultHttpClient httpClient = new DefaultHttpClient();
					// HttpPost httpPost = new HttpPost( fullPathUrl );

					// for(int loop = 0; loop < posts.size(); loop++){
					if (true) {

						int loop = 0;

						HttpPost httpPost = (HttpPost) posts.get(loop);

						if (debug) {
							System.out.print("s373.apache.request: "
									+ httpPost.getRequestLine() + "\n");
						}

						// response
						HttpResponse response = httpClient.execute(httpPost);
						HttpEntity entity = response.getEntity();

						statusStr = "" + response.getStatusLine();

						if (debug) {
							System.out.print("s373.apache.status: "
									+ response.getStatusLine() + "\n");
						}

						String re = inputStreamToString(entity.getContent())
								.toString();
						replyStr = "" + re;
						results.add(re);

						if (debug) {
							System.out.print("s373.apache.reply: " + re + "\n");
						}

						posts.remove(loop);

					}

					// When HttpClient instance is no longer needed,
					// shut down the connection manager to ensure
					// immediate deallocation of all system resources
					httpClient.getConnectionManager().shutdown();

				} catch (Exception e) {
					statusStr = e.toString();
					e.printStackTrace();
				}

			}

			try {
				Thread.sleep(threadTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		System.out.print("s373.apache thread closing down");
	}

	/**
	 * @param args
	 * @return
	 */
	public int qPOST(String args[]) {

		int res = -1;

		try {
			int numvars = args.length / 2;

			// DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(fullPathUrl);

			if (numvars > 0) {
				List nameValuePairs = new ArrayList(numvars);
				for (int i = 0; i < numvars; i++) {
					int idx = i * 2;
					nameValuePairs.add(new BasicNameValuePair(args[idx],
							args[idx + 1]));
				}

				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				posts.add(httpPost);

				res = 1;

			}

			return res;

		} catch (Exception e) {
			e.printStackTrace();
			return res;
		}

	}

	// instant
	public String POST(String args[]) {

		String re = "Error: ";

		try {
			int numvars = args.length / 2;

			if (numvars <= 0) {
				return re + " args : " + numvars + "\n";
			}

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(fullPathUrl);

			List nameValuePairs = new ArrayList(numvars);
			for (int i = 0; i < numvars; i++) {
				int idx = i * 2;
				nameValuePairs.add(new BasicNameValuePair(args[idx],
						args[idx + 1]));
			}

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));// !

			if (debug) {
				System.out.print("s373.apache.request: "
						+ httpPost.getRequestLine() + "\n");
			}

			requestStr = "" + httpPost.getRequestLine();

			// exe & response
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();

			if (debug) {
				System.out.print("s373.apache.status: "
						+ response.getStatusLine() + "\n");
			}

			statusStr = "" + response.getStatusLine();
			re = inputStreamToString(entity.getContent()).toString();
			replyStr = "" + re;

			// results.add(re);

			if (debug) {
				System.out.print("s373.apache.reply: " + re + "\n");
			}

			// posts.remove(loop);

		} catch (Exception e) {
			statusStr = e.getMessage();
			e.printStackTrace();
		}

		return re;

	}

	/**
	 * @return
	 */

	public ArrayList available() {

		ArrayList strs = new ArrayList();
		int num = results.size();

		if (num <= 0)
			return strs;

		for (int i = 0; i < num; i++) {
			// String str = (String)results.get(i);
			strs.add(results.get(i));
		}

		results.clear();

		return strs;
	}

	/**
	 * @return the phpUrl
	 */
	public String getPhpUrl() {
		return phpUrl;
	}

	/**
	 * @param phpUrl
	 *            the phpUrl to set
	 */
	public void setPhpUrl(String phpUrl) {
		this.phpUrl = phpUrl;
	}

	/**
	 * @return the baseHttpUrl
	 */
	public String getBaseHttpUrl() {
		return baseHttpUrl;
	}

	/**
	 * @param baseHttpUrl
	 *            the baseHttpUrl to set
	 */
	public void setBaseHttpUrl(String baseHttpUrl) {
		this.baseHttpUrl = baseHttpUrl;
	}

	/**
	 * @return the threadTime
	 */
	public int getThreadTime() {
		return threadTime;
	}

	/**
	 * @param threadTime
	 *            the threadTime to set
	 */
	public void setThreadTime(int threadTime) {
		this.threadTime = threadTime;
	}

	/**
	 * @param content
	 * @return
	 */
	// // Fast Implementation
	private StringBuilder inputStreamToString(InputStream is) {
		String line = "";
		StringBuilder total = new StringBuilder();

		// Wrap a BufferedReader around the InputStream
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		try {
			// Read response until the end
			while ((line = rd.readLine()) != null) {
				total.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Return full string
		return total;
	}

	/**
	 * @return the statusStr
	 */
	public String getStatusStr() {
		return statusStr;
	}

	/**
	 * @param statusStr
	 *            the statusStr to set
	 */
	public void setStatusStr(String statusStr) {
		this.statusStr = statusStr;
	}

	/**
	 * @return the replyStr
	 */
	public String getReplyStr() {
		return replyStr;
	}

	/**
	 * @param replyStr
	 *            the replyStr to set
	 */
	public void setReplyStr(String replyStr) {
		this.replyStr = replyStr;
	}

	/**
	 * @return the requestStr
	 */
	public String getRequestStr() {
		return requestStr;
	}

	/**
	 * @param requestStr
	 *            the requestStr to set
	 */
	public void setRequestStr(String requestStr) {
		this.requestStr = requestStr;
	}

}
