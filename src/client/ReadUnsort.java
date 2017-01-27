package client;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReadUnsort {

	public List<String> Parse() throws FileNotFoundException {
		List<String> txt = new ArrayList<String>();
		Scanner in = new Scanner(new FileReader("unsort"));
		while (in.hasNext()) {
			String data = in.next();
			String mask = "\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+";
			if (data.matches(mask))
				txt.add(data);
			// System.out.println(data);
		}
		in.close();
		return txt;
	}

	public static void MakeUProxy() {
		try (FileWriter writer = new FileWriter("uproxy.txt", false)) {
			ReadUnsort reader = new ReadUnsort();
			List<String> txt = reader.Parse();
			for (int i = 0; i < txt.size(); i++) {
				writer.write(txt.get(i));
				writer.append('\n');
			}
			writer.flush();
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
		System.out.println("Finish");
	}

	public static void main(String args[]) {
		MakeUProxy();
	}
}
