package com.schneide.extractor.model.type;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.JavaType;

public class WordExtract {
	
	private final Set<String> exclusions;
	
	public WordExtract(final String... exclusions) {
		super();
		this.exclusions = new HashSet<>();
		this.exclusions.addAll(Arrays.asList(exclusions));
	}
	
	public List<String> from(final File rootDirectory) {
		final JavaProjectBuilder builder = new JavaProjectBuilder();
		builder.setEncoding("utf-8");
		builder.addSourceTree(rootDirectory);
		final List<String> extract = new ArrayList<>();
		for (JavaSource	each : builder.getSources()) {
			final List<JavaClass> classes = each.getClasses();
			for (JavaClass currentClass : classes) {
				extract.addAll(extractFrom(currentClass));
			}
		}
		
		Stream<String> result = extract.stream();
		for (String exclusion : this.exclusions) {
			result = result.filter(text -> !text.equals(exclusion));
		}
		return result.collect(Collectors.toList());
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
}
