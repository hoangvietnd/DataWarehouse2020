package download;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.chilkatsoft.CkGlobal;
import com.chilkatsoft.CkScp;
import com.chilkatsoft.CkSsh;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import connectionDatabase.BaseConnection;

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

		// disconnect
		ssh.Disconnect();

		// ghi log
//		DownloadFileFromServer dlf = new DownloadFileFromServer();
//		dlf.writeLog(0);

	}

	public static Map<String, String> writeLog(int idconfig) {
		Map<String, String> result = new HashMap<String, String>();

		int idLogTab = 0;
		int idConfig = 0;
		String status_file = "";
		String detail_information = "";
		String time_load_staging = "";
		String time_load_warehouse = "";
		String numCol_have_load = "";

		try {
			Connection connection_user = (Connection) BaseConnection.getMySQLConnection();
			connection_user.setAutoCommit(false);
			PreparedStatement prstmt = (PreparedStatement) connection_user.prepareStatement(
					"select * from logtab left join configtable on logtab.idconfig = configtable.idconfig where logtab.idconfig="
							+ idconfig + ";");
			prstmt.execute();
			ResultSet rs = prstmt.getResultSet();
			while (rs.next()) {
				idLogTab = rs.getInt(1);
				idConfig = rs.getInt(2);
				status_file = rs.getString(3);
				detail_information = rs.getString(4);
				time_load_staging = rs.getString(5);
				time_load_warehouse = rs.getString(6);
				numCol_have_load = rs.getString(7);

				result.put("idLogTab", String.valueOf(idLogTab));
				result.put("idConfig", String.valueOf(idConfig));
				result.put("status_file", status_file);
				result.put("detail_information", detail_information);
				result.put("time_load_staging", time_load_staging);
				result.put("time_load_warehouse", time_load_warehouse);
				result.put("numCol_have_load", numCol_have_load);
			}
			String sql = "UPDATE `control_database`.`logtab` SET `status_file` = 'ready_to_staging' WHERE (`idlogTab` = '"
					+ idconfig + "');";
			prstmt = (PreparedStatement) connection_user.prepareStatement(sql);
			prstmt.executeUpdate();
			connection_user.close();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return result;

	}

}
