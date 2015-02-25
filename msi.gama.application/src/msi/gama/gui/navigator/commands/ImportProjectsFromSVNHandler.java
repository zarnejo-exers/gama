/*********************************************************************************************
 * 
 * 
 * 'ImportProjectsFromSVNHandler.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.navigator.commands;

import java.io.*;
import java.net.*;
import java.util.List;
import msi.gama.gui.svn.SVNAccess;
import org.eclipse.core.commands.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.tmatesoft.svn.core.*;

public class ImportProjectsFromSVNHandler extends AbstractHandler {

	IWorkbenchPage page;

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		Job job = new Job("Updating Shared Models") {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				final SVNAccess access = new SVNAccess();
				String url = "https://gama-models.googlecode.com/svn/trunk/";
				monitor.beginTask("Importing shared models from " + url, 5000);
				try {
					access.createAccessToRepositoryLocation(url);
					monitor.worked(500);
					URL platformUrl = Platform.getInstanceLocation().getURL();

					String pathFolderDocs = platformUrl.getPath() + ".svn_models";
					String pathFolderSnapshots = pathFolderDocs + File.separator + "snapshots";
					File folder = new File(pathFolderSnapshots);

					if ( !folder.exists() ) {
						folder.mkdir();
					}

					// DataOutputStream dos = null;

					List<SVNDirEntry> entries = access.getDocsAndSnapshotsEntries(url, monitor);
					if ( entries.isEmpty() ) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

							@Override
							public void run() {
								MessageDialog.openError(Display.getDefault().getActiveShell(),
									"Error while updating library", "Check your internet connexion");
							}
						});
					}
					for ( SVNDirEntry entry : entries ) {
						String s = entry.getURL().toString();
						URL urlFile = new URL(s);
						URLConnection urlc = urlFile.openConnection();

						InputStream is = urlc.getInputStream();
						/* html file */
						if ( entry.getName().endsWith(".html") ) {
							File htmlFile = new File(pathFolderDocs + File.separator + entry.getName());
							DataOutputStream dos = new DataOutputStream(new FileOutputStream(htmlFile));

							String line = "";
							StringWriter writer = new StringWriter();
							try {
								InputStreamReader streamReader = new InputStreamReader(is);
								/* The buffer for the readline */
								BufferedReader buffer = new BufferedReader(streamReader);
								try {
									writer.write("<!-- location:" + entry.getURL() + " -->");
									writer.write(line + "\n");
									while ((line = buffer.readLine()) != null) {
										writer.write(line + "\n");
									}
								} finally {
									buffer.close();
									streamReader.close();
								}
							} catch (IOException ioe) {
								ioe.printStackTrace();
							}

							dos.writeBytes(writer.toString());

							/* pic file */
						} else {
							FileOutputStream fos = null;
							try {
								File picFile = new File(folder + File.separator + entry.getName());
								fos = new FileOutputStream(picFile);
								byte[] buffer = new byte[512];
								int n = 0;
								while (-1 != (n = is.read(buffer))) {
									fos.write(buffer, 0, n);
								}
							} catch (IOException e) {
								e.printStackTrace();
							} finally {
								if ( fos != null ) {
									fos.close();
								}
							}
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				} catch (SVNException e) {
					e.printStackTrace();
				}

				if ( !monitor.isCanceled() ) {
					monitor.done();
					return Status.OK_STATUS;
				}
				return Status.CANCEL_STATUS;
			}

		};
		job.setUser(true);
		job.schedule();

		// job.addJobChangeListener(new JobChangeAdapter() {
		//
		// @Override
		// public void done(final IJobChangeEvent event) {
		// RefreshHandler.run();
		// }
		//
		// });

		return null;
	}

}
