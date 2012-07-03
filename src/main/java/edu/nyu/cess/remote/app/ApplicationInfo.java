package edu.nyu.cess.remote.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class ApplicationInfo {

	String applicationNames[];

	private final HashMap<String, HashMap<String, String>> applicationsInfo = new HashMap<String, HashMap<String, String>>();

	public void readFromFile(File file) {
		String[] applicationInfoLine;

		final int APPLICATION_NAME = 0, FILE_NAME = 1, ARGUMENTS = 2, FILE_PATH = 3;

		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			System.out.println("Reading application parameters from: " + file.getAbsolutePath());

			String line = null;
			while ((line = bufferedReader.readLine()) != null) {

				applicationInfoLine = line.split(",");

				if (applicationInfoLine.length >= 2) {

					HashMap<String, String> appInfo = new HashMap<String, String>();
					appInfo.put("name", applicationInfoLine[APPLICATION_NAME]);
					appInfo.put("file_name", applicationInfoLine[FILE_NAME]);

					if (applicationInfoLine.length >= 3) {
						appInfo.put("args", applicationInfoLine[ARGUMENTS]);
					}
					else {
						appInfo.put("args", "");
					}

					if (applicationInfoLine.length >= 4) {
						appInfo.put("path", applicationInfoLine[FILE_PATH]);
					}
					else {
						appInfo.put("path", "");
					}

					applicationsInfo.put(applicationInfoLine[APPLICATION_NAME], appInfo);
				}
			}

			applicationNames = applicationsInfo.keySet().toArray(new String[applicationsInfo.size()]);

			Arrays.sort(applicationNames);

			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.err.println("File not found." + ex.getMessage());
			applicationNames = new String[0];
		} catch (IOException ex) {
			System.err.println("IO Exception Occured.");
			applicationNames = new String[0];
		}

	}

	public String[] getApplicationNames() {
		return applicationNames;
	}

	public HashMap<String, String> getApplicationInformation(String applicationName) {
		return applicationsInfo.get(applicationName);
	}

	public HashMap<String, HashMap<String, String>> getAllApplicationsInformation() {
		return applicationsInfo;
	}
}
