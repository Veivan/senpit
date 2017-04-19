package client;

import java.io.File;
import java.io.IOException;

/**
 * Чтение jpg-файлов с диска и сохранение в DB.
 */
public class FotoImporter {

	// Настраивается вручную
	private int gender = 0; // @gender = 0 (FEMALE) or = 1 (MALE) or = null
							// (NEUTRAL)
	private int ptype_id = 2; // 1 - BANNERIMG 2 - PROFILEIMG

	DbConnectorSenPit dbConnector = new DbConnectorSenPit();

	public void ReadNSave() throws IOException {
		File[] fList;
		File F = new File("C:\\Temp\\1");
		fList = F.listFiles();

		for (int i = 0; i < fList.length; i++) {
			if (fList[i].isFile()) {
				String fName = fList[i].getCanonicalPath();
				System.out.println(String.valueOf(i) + " - " + fName);
				// Save foto to DB
				dbConnector.SaveImageFromFile(fName, gender, ptype_id);
			}
		}
	}

	public static void main(String[] args) throws IOException {
		FotoImporter fg = new FotoImporter();
		fg.ReadNSave();
	}

}
