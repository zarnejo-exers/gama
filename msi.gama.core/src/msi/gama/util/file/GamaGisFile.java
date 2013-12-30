/**
 * Created by drogoul, 12 déc. 2013
 * 
 */
package msi.gama.util.file;

import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Class GamaGisFile.
 * 
 * @author drogoul
 * @since 12 déc. 2013
 * 
 */
public abstract class GamaGisFile extends GamaFile<Integer, GamaShape> {

	// The code to force reading the GIS data as already projected
	public static final int ALREADY_PROJECTED_CODE = 0;
	protected IProjection gis;
	protected Integer initialCRSCode = null;

	// Faire les tests sur ALREADY_PROJECTED ET LE PASSER AUSSI A GIS UTILS ???

	/**
	 * Returns the CRS defined with this file (in a ".prj" file or elsewhere)
	 * @return
	 */
	protected CoordinateReferenceSystem getExistingCRS(final IScope scope) {
		if ( initialCRSCode != null ) { return scope.getSimulationScope().getProjectionFactory().getCRS(initialCRSCode); }
		CoordinateReferenceSystem crs = getOwnCRS();
		if ( crs == null ) {
			crs = scope.getSimulationScope().getProjectionFactory().getDefaultInitialCRS();
		}
		return crs;
	}

	/**
	 * @return
	 */
	protected abstract CoordinateReferenceSystem getOwnCRS();

	protected void computeProjection(final IScope scope, final Envelope env) {
		CoordinateReferenceSystem crs = getExistingCRS(scope);
		gis = scope.getSimulationScope().getProjectionFactory().fromCRS(crs, env);
	}

	public GamaGisFile(final IScope scope, final String pathName, final Integer code) {
		super(scope, pathName);
		initialCRSCode = code;
	}

	/**
	 * Method flushBuffer()
	 * @see msi.gama.util.file.GamaFile#flushBuffer()
	 */
	@Override
	protected void flushBuffer() throws GamaRuntimeException {
		// Not yet done for GIS files
	}

	public IProjection getGis(final IScope scope) {
		if ( gis == null ) {
			fillBuffer(scope);
		}
		return gis;
	}

}
