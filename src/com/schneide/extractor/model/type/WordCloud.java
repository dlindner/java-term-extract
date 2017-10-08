package com.schneide.extractor.model.type;

import java.awt.Color;
import java.awt.Dimension;
import java.io.OutputStream;
import java.util.List;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.RectangleBackground;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;
import com.kennycason.kumo.placement.LinearWordPlacer;

public class WordCloud {
	
	private final List<WordFrequency> frequencies;

	public WordCloud(final List<WordFrequency> frequencies) {
		super();
		this.frequencies = frequencies;
	}
	
	public void writePNGTo(OutputStream output) {
		final Dimension dimension = new Dimension(1280, 720);
		final com.kennycason.kumo.WordCloud wordCloud = new com.kennycason.kumo.WordCloud(dimension, CollisionMode.RECTANGLE);
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
		wordCloud.build(this.frequencies);
		wordCloud.writeToStreamAsPNG(output);
	}
	
	public static WordCloud from(final List<String> words, final int shownWords) {
		final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
		frequencyAnalyzer.clearNormalizers();
		frequencyAnalyzer.clearFilters();
		frequencyAnalyzer.setWordFrequenciesToReturn(shownWords);
		frequencyAnalyzer.setMaxWordLength(1000);
		final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(words);
		return new WordCloud(wordFrequencies);
	}
}
