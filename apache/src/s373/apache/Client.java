/**
 * 
 */
package s373.apache;

import java.util.ArrayList;

/**
 * @author a
 *
 */
public class Client {

	Apache apache;
	
	
	public Client(String server){
		apache = new Apache(server);
		new Thread(apache).start();
	}
	
	public Client(String server, String php){
		apache = new Apache(server,php);
		new Thread(apache).start();
	}


	public Client(String server, int t){
		apache = new Apache(server, t);
		new Thread(apache).start();
	}
	
	public Client(String server, String php, int t){
		apache = new Apache(server,php,t);
		new Thread(apache).start();
	}

	
	
	
	public int POST(String args[]){
		return apache.POST(args);		
	}	
	
	
	public String[] available(){
		
		String ret[] = {null};
		ArrayList strs = new ArrayList();
		strs = apache.available();
		
		if(strs.size() > 0){
			ret = new String[strs.size()];
			for(int i=0; i<strs.size();i++){
				ret[i] = (String) strs.get(i);
			}
		}
		
		return ret;
		
	}
	
}
