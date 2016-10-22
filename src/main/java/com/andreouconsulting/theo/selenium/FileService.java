package com.andreouconsulting.theo.selenium;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;


/**
 * Service to create a new CSV file. 
 * 
 * @author theo@andreouconsulting.com
 *
 */
public class FileService {

	/**
	 * Writes to provided balances on the file.
	 * 
	 * @param balances
	 * @param fileName
	 * @throws IOException 
	 */
	public static void writeToFile(Map<String,Double> balances, String fileName) throws IOException{
		List<String> lines = Lists.newArrayList();
		for(String account : balances.keySet()){
			StringBuffer buffer = new StringBuffer();
			buffer.append(account);
			buffer.append(",");
			buffer.append(balances.get(account));
			lines.add(buffer.toString());
		}
		Collections.sort(lines);
		Path file = Paths.get(fileName);
		Files.write(file, lines, Charset.forName("UTF-8"));
	}
}
