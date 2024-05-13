package org.utilities;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

@SupportedAnnotationTypes("org.utilities.CSet") // Replace with your actual package and annotation
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CSetAnnotationProcessor extends AbstractProcessor {
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (Element element : roundEnv.getElementsAnnotatedWith(CSet.class)) {
			if (element instanceof TypeElement classElement) {
				for (Element fieldElement : classElement.getEnclosedElements()) {
					if (fieldElement.getKind().isField()) {
						VariableElement variableElement = (VariableElement) fieldElement;
						String fieldName = variableElement.getSimpleName().toString();
						String setterName = "set" + this.capitalize(fieldName);

						String setterMethod = this.generateSetterMethod(
								classElement.asType().toString(),
								fieldName,
								variableElement.asType().toString()
						);

						try {
							JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(
									classElement.getQualifiedName() + "Generated");
							try (PrintWriter writer = new PrintWriter(sourceFile.openWriter())) {
								writer.println("package " + classElement.getEnclosingElement() + ";");
								writer.println("public class " + classElement.getSimpleName() + "Generated {");
								writer.println(setterMethod);
								writer.println("}");
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return true;
	}

	private String capitalize(String input) {
		return input.toUpperCase().charAt(0) + input.substring(1);
	}

	private String generateSetterMethod(String className, String fieldName, String fieldType) {
		return "public " + className + " " + "set" + this.capitalize(fieldName) + "(" + fieldType + " " + fieldName + ") {\n" +
				"   this." + fieldName + " = " + fieldName + ";\n" +
				"   return this;\n" +
				"}";
	}
}
