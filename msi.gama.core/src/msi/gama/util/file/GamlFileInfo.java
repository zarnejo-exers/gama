package msi.gama.util.file;

import static org.apache.commons.lang.StringUtils.join;
import static org.apache.commons.lang.StringUtils.splitByWholeSeparatorPreserveAllTokens;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class GamlFileInfo extends GamaFileMetaData {

	public static String BATCH_PREFIX = "***";
	public static String ERRORS = "errors detected";

	private final Collection<String> experiments;
	private final Collection<String> imports;
	private final Collection<String> uses;
	public final boolean invalid;

	public GamlFileInfo(final long stamp, final Collection<String> imports, final Collection<String> uses,
			final Collection<String> exps) {
		super(stamp);
		invalid = stamp == Long.MAX_VALUE;
		this.imports = imports;
		this.uses = uses;
		this.experiments = exps;
	}

	public Collection<String> getImports() {
		return imports == null ? Collections.EMPTY_LIST : imports;
	}

	public Collection<String> getUses() {
		return uses == null ? Collections.EMPTY_LIST : uses;
	}

	public Collection<String> getExperiments() {
		return experiments == null ? Collections.EMPTY_LIST : experiments;
	}

	public GamlFileInfo(final String propertyString) {
		super(propertyString);
		final String[] values = split(propertyString);
		imports = Arrays.asList(splitByWholeSeparatorPreserveAllTokens(values[1], SUB_DELIMITER));
		uses = Arrays.asList(splitByWholeSeparatorPreserveAllTokens(values[2], SUB_DELIMITER));
		experiments = Arrays.asList(splitByWholeSeparatorPreserveAllTokens(values[3], SUB_DELIMITER));
		invalid = values[4].equals("TRUE");
	}

	/**
	 * Method getSuffix()
	 * 
	 * @see msi.gama.util.file.GamaFileMetaInformation#getSuffix()
	 */
	@Override
	public String getSuffix() {
		if (invalid)
			return ERRORS;
		final int expCount = experiments.size();
		if (expCount > 0) {
			return "" + (expCount == 1 ? "1 experiment" : expCount + " experiments");
		}

		return "no experiment";
	}

	@Override
	public String toPropertyString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(super.toPropertyString()).append(DELIMITER);
		sb.append(join(imports, SUB_DELIMITER)).append(DELIMITER);
		sb.append(join(uses, SUB_DELIMITER)).append(DELIMITER);
		sb.append(join(experiments, SUB_DELIMITER)).append(DELIMITER);
		sb.append(invalid ? "TRUE" : "FALSE").append(DELIMITER);
		return sb.toString();

	}

	@Override
	public String getDocumentation() {
		return "GAML model file with " + getSuffix();
	}

}