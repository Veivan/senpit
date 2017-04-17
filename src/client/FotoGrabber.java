package client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class FotoGrabber {

	// Настраивается вручную
	private String fname = "D:/Work/Projects_Java/GrabFoto/pixabay.com_ru_photos__cat.html";
	private int gender = 0; // @gender = 0 (FEMALE) or = 1 (MALE) or = null
							// (NEUTRAL)
	private int ptype_id = 2; // 1 - BANNERIMG 2 - PROFILEIMG

	DbConnectorSenPit dbConnector = new DbConnectorSenPit();

	public void GrabNSave() {
		List<String> links = new ArrayList<String>();

		Scanner in;
		try {
			in = new Scanner(new FileReader(fname));
			while (in.hasNext()) {
				String data = in.next();
				String mask = "(https://cdn.pixabay.com).+(.jpg)";
				if (data.matches(mask))
					links.add(data);
				// System.out.println(data);
			}
			in.close();
			//System.out.println(links.size());

			for (int i = 0; i < links.size(); i++) {
				String pictureLink = links.get(i);
				System.out.println(pictureLink);
				byte[] picture = GetPicture(pictureLink);
				
				// Save foto to DB
				dbConnector.SaveImage(picture, gender, ptype_id);
				
				/* Save foto to file
				String pfName = "D:/Work/Projects_Java/GrabFoto"
						+ pictureLink.substring(pictureLink.lastIndexOf("/"));
				System.out.println(pfName);
				SavePicture(picture, pfName);
				*/
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Finita");

	}

	private static byte[] GetPicture(String pictureLink) throws Exception {
		URL connection = new URL(pictureLink);
		HttpsURLConnection urlconn = (HttpsURLConnection) connection
				.openConnection();
		urlconn.setRequestMethod("GET");
		urlconn.connect();

		try (ByteArrayOutputStream result = new ByteArrayOutputStream()) {
			byte[] buffer = new byte[1024];
			int length;
			while ((length = urlconn.getInputStream().read(buffer)) != -1) {
				result.write(buffer, 0, length);
			}
			return result.toByteArray();
		} catch (Exception e) {

			System.out.println(e.getMessage());
		} finally {
			urlconn.disconnect();
		}
		return null;
	}

	private static void SavePicture(byte[] picture, String filename) {
		FileOutputStream fos = null;
		// write binary stream into file
		File file = new File(filename);
		try {
			fos = new FileOutputStream(file);
			fos.write(picture);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
