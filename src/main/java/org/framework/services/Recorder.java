package org.framework.services;

import lombok.Getter;
import org.javatuples.Pair;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public abstract class Recorder {
	private static Map<String, Pair<FileWriter, String>> fileWritersMap = new HashMap<>();


	public static void watch(String fileId, String filePath, boolean cleanFile) {
		try {
			if (cleanFile == true) {
				new FileWriter(filePath, false).close();
			}
			fileWritersMap.put(fileId, Pair.with(new FileWriter(filePath, true), filePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void clean(String fileId) {
		try {
			new FileWriter(fileWritersMap.get(fileId).getValue1(), false).close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void close(String fileId) {
		try {
			fileWritersMap.get(fileId).getValue0().close();
			fileWritersMap.remove(fileId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void record(String fileId, String message) {
		try {
			var writer = fileWritersMap.get(fileId).getValue0();
			writer.write(message);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
