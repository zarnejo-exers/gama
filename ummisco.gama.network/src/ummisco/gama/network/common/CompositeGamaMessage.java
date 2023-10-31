/*******************************************************************************************************
 *
 * CompositeGamaMessage.java, in ummisco.gama.network, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.network.common;

import msi.gama.extensions.messaging.GamaMessage;
import msi.gama.runtime.IScope;
import ummisco.gama.serializer.implementations.BinarySerialisation;
import ummisco.gama.serializer.implementations.ISerialisationConstants;

/**
 * The Class CompositeGamaMessage.
 */
public class CompositeGamaMessage extends GamaMessage {

	/** The deserialize content. */
	protected Object deserializeContent;

	/**
	 * Instantiates a new composite gama message.
	 *
	 * @param scope
	 *            the scope
	 * @param message
	 *            the message
	 */
	public CompositeGamaMessage(final IScope scope, final GamaMessage message) {
		super(scope, message.getSender(), message.getReceivers(), message.getContents(scope));
		this.contents = BinarySerialisation.saveToString(scope, message.getContents(scope),
				ISerialisationConstants.BINARY_FORMAT, true);
		// this.contents = StreamConverter.convertNetworkObjectToStream(scope, message.getContents(scope));
		this.emissionTimeStamp = message.getEmissionTimestamp();
		this.setUnread(true);
		deserializeContent = null;
	}

	/**
	 * Instantiates a new composite gama message.
	 *
	 * @param scope
	 *            the scope
	 * @param sender
	 *            the sender
	 * @param receivers
	 *            the receivers
	 * @param content
	 *            the content
	 * @param deserializeContent
	 *            the deserialize content
	 * @param timeStamp
	 *            the time stamp
	 */
	private CompositeGamaMessage(final IScope scope, final Object sender, final Object receivers, final Object content,
			final Object deserializeContent, final int timeStamp) {
		super(scope, sender, receivers, content);
		this.emissionTimeStamp = timeStamp;
		this.setUnread(true);
		this.deserializeContent = deserializeContent;
	}

	@Override
	public Object getContents(final IScope scope) {
		this.setUnread(false);
		if (deserializeContent == null) {
			deserializeContent = BinarySerialisation.createFromString(scope, (String) contents);
			// deserializeContent = StreamConverter.convertNetworkStreamToObject(scope, (String) contents);//

		}
		return deserializeContent;
	}
}
