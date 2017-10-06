package msi.ummisco.modelLibraryGenerator;

import static msi.gama.lang.gaml.validation.GamlModelBuilder.compile;
import static org.eclipse.emf.common.util.URI.createFileURI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gaml.compilation.GamlCompilationError;

public class ModelLibraryValidator {

	static final Predicate<Path> isModel = p -> {
		final String s = p.getFileName().toString();
		return s.endsWith(".gaml") || s.endsWith(".experiment");
	};
	static final List<GamlCompilationError> errors = new ArrayList<>();

	static void log(final String s) {
		System.out.println(s);
	}

	static public void start(final String pluginsFolder) throws IOException {
		HeadlessSimulationLoader.preloadGAMA();
		Files.walk(Paths.get(pluginsFolder)).filter(isModel).forEach(p -> compile(createFileURI(p.toString()), errors));
		errors.stream()/* .filter(e -> e.isError()) */
				.forEach(e -> log("Error in " + e.getURI().toFileString().replace(pluginsFolder, "") + ": " + e));
	}
}