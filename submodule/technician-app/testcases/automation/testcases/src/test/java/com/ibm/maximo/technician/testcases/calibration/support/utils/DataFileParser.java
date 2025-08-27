package com.ibm.maximo.technician.testcases.calibration.support.utils;

import org.json.JSONObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DataFileParser {

	public static JSONObject parseJsonFile(String filePath) throws IOException {
        return (JSONObject) new JSONObject(
    		Files.readString(
				Paths.get(filePath),
				StandardCharsets.UTF_8
			)
		);
    }
	
	public static JSONObject parseJsonFile(Path filePath) throws IOException {
        return (JSONObject) new JSONObject(
    		Files.readString(
				filePath,
				StandardCharsets.UTF_8
			)
		);
	}
}
