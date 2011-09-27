/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.fast.ibatis.popup.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import net.sf.fast.ibatis.build.AbstractCodeBuilder;
import net.sf.fast.ibatis.build.HandleType;
import net.sf.fast.ibatis.build.dao.DAOCustomBuilder;
import net.sf.fast.ibatis.build.dao.DAODeleteBuilder;
import net.sf.fast.ibatis.build.dao.DAOInsertBuilder;
import net.sf.fast.ibatis.build.dao.DAOIntegerBuilder;
import net.sf.fast.ibatis.build.dao.DAOListBuilder;
import net.sf.fast.ibatis.build.dao.DAOUpdateBuilder;
import net.sf.fast.ibatis.build.dao.DAOVoidBuilder;
import net.sf.fast.ibatis.build.service.ServiceCustomBuilder;
import net.sf.fast.ibatis.build.service.ServiceDeleteBuilder;
import net.sf.fast.ibatis.build.service.ServiceInsertBuilder;
import net.sf.fast.ibatis.build.service.ServiceIntegerBuilder;
import net.sf.fast.ibatis.build.service.ServiceListBuilder;
import net.sf.fast.ibatis.build.service.ServiceUpdateBuilder;
import net.sf.fast.ibatis.build.service.ServiceVoidBuilder;
import net.sf.fast.ibatis.dialog.FastIbatisDialog;
import net.sf.fast.ibatis.i18n.Fasti18n;
import net.sf.fast.ibatis.model.FastIbatisConfig;
import net.sf.fast.ibatis.util.Utils;
import net.sf.fast.ibatis.util.XmlUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
/**
 * <p>
 * generate the code action.
 * </p>
 * @author dan.zheng
 * @version 1.0
 */
public class FastibatisCodeActionDelegate implements IEditorActionDelegate,
		IViewActionDelegate {

	private Shell shell;
	private String ibatisSqlMapIdName;

	/**
	 * Constructor for Action1.
	 */
	public FastibatisCodeActionDelegate() {
		super();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		try {
			final IFile file = ((FileEditorInput) PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor().getEditorInput()).getFile();
			HandleType handleType = getHandleType(file);
			if (handleType == HandleType.NONE) {
				MessageDialog.openWarning(shell, "Warning", Fasti18n.getString("not_the_effective_node"));
				return;
			}
			String similarSQL = getSimilarSQLId(ibatisSqlMapIdName,file);
			if(similarSQL!=null) {
				boolean b = MessageDialog.openConfirm(shell, Fasti18n.getString("Confirm"),String.format("%s", Fasti18n.getString("similar_sql")));
				if(!b)
					return;
			}
			FastIbatisDialog dialog = new FastIbatisDialog(shell);
			final FastIbatisConfig fc = dialog.open(ibatisSqlMapIdName,
					handleType);
			fc.setMethodName(ibatisSqlMapIdName);
			if (!dialog.isDAOGenerate() && !dialog.isServiceGenerate()) {
				return;
			}

			ProgressMonitorDialog progressDlg = new ProgressMonitorDialog(shell);
			progressDlg.run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					monitor.beginTask(Fasti18n.getString("start_working")+"...", 1);
					try {
						generateFastIbatisCode(fc, file);
						if (file != null)
							file.getProject().refreshLocal(
									IResource.DEPTH_INFINITE, monitor);
						monitor.worked(1);
					} catch (CoreException e) {
						e.printStackTrace();
					} finally {
						monitor.done();
					}

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/**
	 * get from tables to avoid writing resemble sql.
	 * @return
	 */
	private String getSimilarSQLId(String id,IFile file) {
		try {
			
		}catch(Exception e) {
			
		}
		return "";
	}
	private void generateFastIbatisCode(FastIbatisConfig fc, IFile file) {
		try {
			String xmlFileName = file.getName();
			String modelName = Utils.getModelName(xmlFileName);
			fc.setXmlFileName(xmlFileName);
			fc.setModelName(modelName);
			String xmlFilePath = file.getLocation().makeAbsolute().toFile()
					.getAbsolutePath();
			fc.setXmlFilePath(xmlFilePath);
			HandleType handleType = getHandleType(file);
			if (handleType == HandleType.SELECT) {
				handleSelectMethod(fc);
			} else if (handleType == HandleType.UPDATE) {
				handleUpdateMethod(fc);
			} else if (handleType == HandleType.DELETE) {
				handleDeleteMethod(fc);
			} else if (handleType == HandleType.INSERT) {
				handleInsertMethod(fc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private HandleType getHandleType(IFile file) {
		String xmlFilePath = file.getLocation().makeAbsolute().toFile()
				.getAbsolutePath();
		return XmlUtil.getSelectedIdType(xmlFilePath, ibatisSqlMapIdName);
	}

	private void handleInsertMethod(FastIbatisConfig fc) {
		AbstractCodeBuilder dao_cb = new DAOInsertBuilder();
		generateCode(dao_cb.generateCode(fc));
		if (fc.isServiceGenerate()) {
			AbstractCodeBuilder cb = new ServiceInsertBuilder();
			generateCode(cb.generateCode(fc));
		}
	}

	private void handleDeleteMethod(FastIbatisConfig fc) {
		AbstractCodeBuilder dao_cb = new DAODeleteBuilder();
		generateCode(dao_cb.generateCode(fc));
		if (fc.isServiceGenerate()) {
			AbstractCodeBuilder cb = new ServiceDeleteBuilder();
			generateCode(cb.generateCode(fc));
		}
	}

	private void handleUpdateMethod(FastIbatisConfig fc) {
		AbstractCodeBuilder dao_cb = new DAOUpdateBuilder();
		generateCode(dao_cb.generateCode(fc));
		if (fc.isServiceGenerate()) {
			AbstractCodeBuilder cb = new ServiceUpdateBuilder();
			generateCode(cb.generateCode(fc));
		}
	}

	private void handleSelectMethod(FastIbatisConfig fc) {
		if (fc.getReturnType().equals("Integer")) {
			AbstractCodeBuilder dao_cb = new DAOIntegerBuilder();
			generateCode(dao_cb.generateCode(fc));
			if (fc.isServiceGenerate()) {
				AbstractCodeBuilder cb = new ServiceIntegerBuilder();
				generateCode(cb.generateCode(fc));
			}
		} else if (fc.getReturnType().equals("List")) {
			AbstractCodeBuilder dao_cb = new DAOListBuilder();
			generateCode(dao_cb.generateCode(fc));
			if (fc.isServiceGenerate()) {
				AbstractCodeBuilder cb = new ServiceListBuilder();
				generateCode(cb.generateCode(fc));
			}
		} else if (fc.getReturnType().equals("void")) {
			AbstractCodeBuilder dao_cb = new DAOVoidBuilder();
			generateCode(dao_cb.generateCode(fc));
			if (fc.isServiceGenerate()) {
				AbstractCodeBuilder cb = new ServiceVoidBuilder();
				generateCode(cb.generateCode(fc));
			}
		} else {
			AbstractCodeBuilder dao_cb = new DAOCustomBuilder();
			generateCode(dao_cb.generateCode(fc));
			if (fc.isServiceGenerate()) {
				AbstractCodeBuilder cb = new ServiceCustomBuilder();
				generateCode(cb.generateCode(fc));
			}
		}
	}

	private void generateCode(Map<String, String> result) {
		if (result != null) {
			for (String key : result.keySet())
				if (key != null && result.get(key) != null)
					Utils.writeContent(key, result.get(key));
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		TextSelection selectdIbatisText = (TextSelection) selection;
		ibatisSqlMapIdName = selectdIbatisText.getText();
	}

	public void init(IViewPart view) {

	}


	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		shell = targetEditor.getSite().getShell();
	}
}
