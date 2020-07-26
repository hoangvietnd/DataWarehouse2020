package download;

import com.chilkatsoft.CkGlobal;
import com.chilkatsoft.CkScp;
import com.chilkatsoft.CkSsh;

public class DownloadFileFromServer {
	static {
		try {
			System.loadLibrary("chilkat");
		} catch (UnsatisfiedLinkError e) {
			System.err.println("Native code library failed to load.\n" + e);
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		CkGlobal glob = new CkGlobal();
		glob.UnlockBundle("Waiting...");
		String hostname = "drive.ecepvn.org";
		int port = 2227;
		
		CkSsh ssh = new CkSsh();
		ssh.Connect(hostname, port);
		
		String username = "guest_access";
		String password = "123456";
		boolean authenticatePw = ssh.AuthenticatePw(username, password);
		System.out.println(authenticatePw);
		
		CkScp scp = new CkScp();
		scp.UseSsh(ssh);
		scp.put_SyncMustMatch("sinhvien_*.*");
		String remote_dir = "/volume1/ECEP/song.nguyen/DW_2020/data";
		String local_dir = "data";
		scp.SyncTreeDownload(remote_dir, local_dir, 0, false);
		
		//disconnect
		ssh.Disconnect();

	}

}
