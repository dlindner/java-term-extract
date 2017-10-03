package com.schneide.extractor.model.type;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.bg.RectangleBackground;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.image.AngleGenerator;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.filter.Filter;
import com.kennycason.kumo.palette.ColorPalette;
import com.kennycason.kumo.placement.LinearWordPlacer;
import com.kennycason.kumo.placement.RectangleWordPlacer;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.JavaType;

public class Main {
	
	public static void main(String[] arguments) {
		final File rootDirectory = rootDirectoryFrom(arguments);
		final JavaProjectBuilder builder = new JavaProjectBuilder();
		builder.addSourceTree(rootDirectory);
		
		final List<String> extract = new ArrayList<>();
		for (JavaSource	each : builder.getSources()) {
			final List<JavaClass> classes = each.getClasses();
			for (JavaClass currentClass : classes) {
				extract.addAll(extractFrom(currentClass));
			}
		}
		
		
		final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
		frequencyAnalyzer.clearNormalizers();
		frequencyAnalyzer.clearFilters();
		frequencyAnalyzer.addFilter(new Filter() {
			@Override
			public boolean test(String text) {
				return (!"void".equals(text));
			}
		});
		frequencyAnalyzer.setWordFrequenciesToReturn(300);
		frequencyAnalyzer.setMaxWordLength(1000);
		final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(extract);
		final Dimension dimension = new Dimension(1280, 720);
		final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.RECTANGLE);
		wordCloud.setPadding(0);
		wordCloud.setBackground(
				new RectangleBackground(dimension));
		wordCloud.setColorPalette(
				new ColorPalette(
						new Color(0x4055F1),
						new Color(0x408DF1),
						new Color(0x40AAF1),
						new Color(0x40C5F1),
						new Color(0x40D3F1),
						new Color(0xFFFFFF)));
		wordCloud.setFontScalar(
				new SqrtFontScalar(6, 72));
		wordCloud.setBackgroundColor(Color.white);
		wordCloud.setWordPlacer(new LinearWordPlacer());
		wordCloud.build(wordFrequencies);
		wordCloud.writeToFile("result-" + System.currentTimeMillis() + ".png");
	}
	
	private static Collection<? extends String> extractFrom(JavaClass currentClass) {
		final TypeList result = new TypeList();
		currentClass.getFields().stream()
								.map(f -> f.getDeclaringClass())
								.forEach(result::add);
		final List<JavaMethod> methods = currentClass.getMethods();
		for (JavaMethod method : methods) {
			result.add(method.getReturnType());
			method.getParameterTypes().forEach(result::add);
		}
		return result.asCollection();
	}
	
	private static class TypeList {
		private final List<String> strings;

		public TypeList() {
			super();
			this.strings = new ArrayList<>();
		}
		
		public void add(JavaClass clazz) {
			this.strings.add(clazz.getSimpleName());
		}
		
		public void add(JavaType type) {
			final String representation = type.getValue();
			final String baseType = representation.replace("[]", "");
			if (baseType.length() < 2) {
				return;
			}
			this.strings.add(baseType);
		}
		
		public Collection<String> asCollection() {
			return new ArrayList<>(this.strings);
		}
	}

	private static File rootDirectoryFrom(String[] arguments) {
		if (arguments.length < 1) {
			return new File(".");
		}
		return new File(arguments[0]);
	}

}
