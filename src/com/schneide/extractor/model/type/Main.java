package com.schneide.extractor.model.type;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

public class Main {
	
	public static void main(String[] arguments) throws IOException {
		final File rootDirectory = rootDirectoryFrom(arguments);
		final WordExtract extract = new WordExtract("void");
		final List<String> extracted = extract.from(rootDirectory);
		
		final long identifier = System.currentTimeMillis();
		final File resultingList = new File("cloud-" + identifier + ".txt");
		final File resultingImage = new File("cloud-" + identifier + ".png");

		try (final PrintWriter list = new PrintWriter(resultingList)) {
			extracted.forEach(list::println);
		}	

		final WordCloud wordCloud = WordCloud.from(extracted, 300);
		try (final OutputStream image = new BufferedOutputStream(new FileOutputStream(resultingImage))) {
			wordCloud.writePNGTo(image);
		}
	}
	
	private static File rootDirectoryFrom(String[] arguments) {
		if (arguments.length < 1) {
			return new File(".");
		}
		return new File(arguments[0]);
	}
}
