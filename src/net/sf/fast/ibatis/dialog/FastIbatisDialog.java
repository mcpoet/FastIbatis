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
package net.sf.fast.ibatis.dialog;

import net.sf.fast.ibatis.build.HandleType;
import net.sf.fast.ibatis.i18n.Fasti18n;
import net.sf.fast.ibatis.model.FastIbatisConfig;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * <p>
 * The main configuration dialog.
 * </p>
 * 
 * @author trami
 * @version 1.0
 */
public class FastIbatisDialog extends Dialog {
	private static final int UNIT_WIDTH = 200;
	/** the flag to generate the DAO code. */
	boolean isDAOGenerate = true;
	/** the flag to generate the Service code. */
	boolean isServiceGenerate;
	/** 
	 * The return type including default combo's Text
	 * and user customization. 
	 */
	String returnType;
	/**
	 * The parameter type including default combo's text 
	 * and user customization. 
	 */
	String paramType;
	/** 
	 * The comments on the method
	 * It will become the javadoc.
	 */
	String methodComment;

	/**
	 * simple getter method of the field comment.
	 * @return the method's comment.
	 */
	public String getMethodComment() {
		return methodComment;
	}
	
	/**
	 * simple setter method of the field comment.
	 * @param methodComment the method's comment.
	 */
	public void setMethodComment(String methodComment) {
		this.methodComment = methodComment;
	}

	/**
	 * Default constructor
	 * @param parent the workspace shell enviroment.
	 */
	public FastIbatisDialog(Shell parent) {
		super(parent);
	}

	/**
	 * Makes the dialog visible.
	 * 
	 * @return FastIbatisConfig the configuration value.
	 */
	public FastIbatisConfig open(String methodName, HandleType handleType) {
		FastIbatisConfig fc = new FastIbatisConfig();
		GridData gd = new GridData(GridData.FILL_BOTH);
		Shell parent = getParent();
		final Shell shell = new Shell(parent, SWT.TITLE | SWT.BORDER
				| SWT.APPLICATION_MODAL);
		shell.setSize(0, 0);
		shell.setText(Fasti18n.getString("title"));

		shell.setLayout(new GridLayout(2, false));

		Label label = new Label(shell, SWT.NULL);
		label.setText(Fasti18n.getString("pending_function_name"));

		final Text text = new Text(shell, SWT.READ_ONLY | SWT.BORDER);
		text.setText(methodName);
		text.setLayoutData(gd);

		Label paramLabelType = new Label(shell, SWT.NULL);
		paramLabelType.setText(Fasti18n.getString("param_type"));

		final Combo paramCombo = new Combo(shell, SWT.DROP_DOWN);		
		paramCombo.setLayoutData(gd);
		paramCombo.setItems(new String[] { "Map" });
		paramCombo.setText("Map");

		Label returnLabelType = new Label(shell, SWT.NULL);
		returnLabelType.setText(Fasti18n.getString("return_type"));

		final Combo returnCombo = new Combo(shell, SWT.DROP_DOWN);
		returnCombo.setLayoutData(gd);
		returnCombo.setItems(new String[] { "Integer", "List", "void" });
		returnCombo.setText("Integer");

		// if the method is not the select,the return type can not be customed
		if (handleType != HandleType.SELECT) {
			returnCombo.setEnabled(false);
			if (handleType == HandleType.INSERT)
				returnCombo.setText("void");
		}

		Button daoCheck = new Button(shell, SWT.CHECK);
		daoCheck.setText(Fasti18n.getString("to_dao"));
		daoCheck.setSelection(true);
		//the dao must be generated
		daoCheck.setEnabled(false);

		Button serviceCheck = new Button(shell, SWT.CHECK);
		serviceCheck.setText(Fasti18n.getString("to_service"));

		Label labelComment = new Label(shell, SWT.NULL);
		labelComment.setText(Fasti18n.getString("func_java_doc"));

		final Text comment = new Text(shell, SWT.MULTI | SWT.WRAP
				| SWT.H_SCROLL | SWT.BORDER | SWT.V_SCROLL);
		comment.setLayoutData(new GridData(UNIT_WIDTH, 100));
		final Button buttonOK = new Button(shell, SWT.PUSH);
		buttonOK.setText(Fasti18n.getString("ok"));
		buttonOK.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		Button buttonCancel = new Button(shell, SWT.PUSH);
		buttonCancel.setText(Fasti18n.getString("cancel"));

		daoCheck.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				isDAOGenerate = !isDAOGenerate;
			}
		});

		serviceCheck.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				isServiceGenerate = !isServiceGenerate;
			}
		});

		buttonCancel.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				isDAOGenerate = false;
				isServiceGenerate = false;
				shell.dispose();
			}
		});
		buttonOK.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				methodComment = comment.getText();
				returnType = returnCombo.getText();
				paramType = paramCombo.getText();
				shell.dispose();
			}
		});
		shell.addListener(SWT.Traverse, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail == SWT.TRAVERSE_ESCAPE)
					event.doit = false;
			}
		});
		centerDialog(shell);
		shell.pack();
		// shell.computeSize(SWT.DEFAULT,SWT.DEFAULT);
		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		fc.setServiceGenerate(this.isServiceGenerate);
		fc.setDAOGenerate(this.isDAOGenerate);
		fc.setReturnType(this.returnType);
		fc.setParamType(this.paramType);
		fc.setMethodComment(this.methodComment);
		return fc;
	}
	private void centerDialog(Shell shell) {
		int width = shell.getMonitor().getClientArea().width;
		int height = shell.getMonitor().getClientArea().height;
		int x = shell.getSize().x;
		int y = shell.getSize().y;
		if (x > width) {
			shell.getSize().x = width;
		}
		if (y > height) {
			shell.getSize().y = height;
		}
		shell.setLocation((width - x) / 2, (height - y) / 2);

	}

	public static void main(String[] args) {
		Shell shell = new Shell();
		FastIbatisDialog dialog = new FastIbatisDialog(shell);
		dialog.open("foo ", HandleType.SELECT);
		System.out.println("isDAOGenerate " + dialog.isDAOGenerate());
		System.out.println("isServiceGenerate " + dialog.isServiceGenerate());
		System.out.println("the param type" + dialog.getParamType());
	}
	/**
	 * simple getter method of the field isDAOGenerate.
	 * @return the flag to generate the DAO code.
	 */
	public boolean isDAOGenerate() {
		return isDAOGenerate;
	}
	/**
	 * simple setter method of the field isDAOGenerate.
	 * @param isDAOGenerate the flag to generate the DAO code.
	 */
	public void setDAOGenerate(boolean isDAOGenerate) {
		this.isDAOGenerate = isDAOGenerate;
	}
	/**
	 * simple getter method of the field isServiceGenerate.
	 * @return the flag to generate the Service code.
	 */
	public boolean isServiceGenerate() {
		return isServiceGenerate;
	}
	/**
	 * simple setter method of the field isServiceGenerate.
	 * @param isServiceGenerate the flag to generate the Service code.
	 */
	public void setServiceGenerate(boolean isServiceGenerate) {
		this.isServiceGenerate = isServiceGenerate;
	}
	/**
	 * simple getter method of the field returnType.
	 * @return The return type.
	 */
	public String getReturnType() {
		return returnType;
	}
	/**
	 * simple setter method of the field returnType.
	 * @param returnType the flag to generate the Service code.
	 */
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	/**
	 * simple getter method of the field isDAOGenerate.
	 * @return the parameter type.
	 */
	public String getParamType() {
		return paramType;
	}
	/**
	 * simple setter method of the field paramType.
	 * @param the parameter type.
	 */
	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

}
