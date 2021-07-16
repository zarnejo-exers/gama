/*********************************************************************************************
 *
 * 'AttributesEditorsView.java, in plugin ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling
 * and simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.views.inspectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import ummisco.gama.ui.experiment.parameters.EditorsList;
import ummisco.gama.ui.interfaces.IParameterEditor;
import ummisco.gama.ui.parameters.AbstractEditor;
import ummisco.gama.ui.views.ExpandableItemsView;

public abstract class AttributesEditorsView<T> extends ExpandableItemsView<T> {

	protected EditorsList<T> editors;

	@Override
	public String getItemDisplayName(final T obj, final String previousName) {
		if (editors == null) return "";
		return editors.getItemDisplayName(obj, previousName);
	}

	@SuppressWarnings ({ "rawtypes", "unchecked" })
	@Override
	protected Composite createItemContentsFor(final T data) {
		final Map<String, IParameterEditor<?>> parameters = editors.getCategories().get(data);
		final Composite compo = new Composite(getViewer(), SWT.NONE);
		final GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 0;
		compo.setLayout(layout);
		compo.setBackground(getViewer().getBackground());
		if (parameters != null) {
			final List<AbstractEditor> list = new ArrayList(parameters.values());
			Collections.sort(list);
			for (final AbstractEditor<?> gpParam : list) {
				gpParam.createComposite(compo);
				if (!editors.isEnabled(gpParam)) { gpParam.setActive(false); }
			}
		}

		return compo;
	}

	@Override
	public void reset() {
		super.reset();
		editors = null;
	}

	@Override
	public void removeItem(final T obj) {
		if (editors == null) return;
		editors.removeItem(obj);
	}

	@Override
	public void pauseItem(final T obj) {
		if (editors == null) return;
		editors.pauseItem(obj);
	}

	@Override
	public void resumeItem(final T obj) {
		if (editors == null) return;
		editors.resumeItem(obj);
	}

	@Override
	public void focusItem(final T obj) {
		if (editors == null) return;
		editors.focusItem(obj);
	}

	@Override
	public List<T> getItems() {
		if (editors == null) return Collections.EMPTY_LIST;
		return editors.getItems();
	}

	@Override
	public void updateItemValues() {
		if (editors != null) { editors.updateItemValues(); }
	}

}
