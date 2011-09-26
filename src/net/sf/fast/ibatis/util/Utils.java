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
package net.sf.fast.ibatis.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.StringTokenizer;

import net.sf.fast.ibatis.build.GenerateFileType;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
/**
 * <p>
 * the Util class.
 * </p>
 * @author dan.zheng
 * @version 1.0
 */
public class Utils {
	/**
	 * strip the file extension.
	 * @param baseName the base file name.
	 * @return the stripped file name.
	 */
	public static String stripFileExtension(String baseName) {
		String fileName = baseName;

		if (baseName == null || baseName.trim().length() <= 0) {
			return fileName;
		}

		// get the report design name, then extract the name without
		// file extension and set it to fileName; otherwise do noting and
		// let fileName with the default name
		int dotIndex = baseName.lastIndexOf('.');
		if (dotIndex > 0) {
			fileName = baseName.substring(0, dotIndex);
		}

		return fileName;
	}
	/**
	 * remove the xml tag
	 * @param xmlPath the xml path.
	 * @param xmlFile the xml file name.
	 * @param javaFileName the java file name.
	 * @return
	 */
	public static String reduceJavaFilePath(String xmlPath, String xmlFile,
			String javaFileName) {
		int pos = xmlPath.indexOf("xml");
		if (pos > -1) {
			String str1 = xmlPath.substring(0, pos);
			return str1 + javaFileName;
		}
		return null;
	}

	/**
	 * generate the service and dao file according to ibatis xml file name.
	 * @param xmlFile the xml file name.
	 * @param type the generate file type
	 * @return
	 */
	public static String convertIbatisXml2JavaFile(String xmlFile, GenerateFileType type) {
		if (xmlFile == null || xmlFile.length() == 0)
			return null;
		if (xmlFile.indexOf("SqlMap") > -1) {// if the file name is not with _SqlMap ending,don't do it
			return null;
		}
		xmlFile = stripFileExtension(xmlFile);
		StringBuffer sb = new StringBuffer();
		if (xmlFile.indexOf("_") == -1) {
			sb.append(upFirstChar(xmlFile.toLowerCase()));
		} else {
			String[] arrs = xmlFile.split("_");
			if (arrs != null) {
				for (String key : arrs) {
					sb.append(upFirstChar(key.toLowerCase()));
				}
			}
		}
		String modelStr = sb.toString();
		if (type == GenerateFileType.Service_Interface) {// service
			return "service" + File.separator + modelStr + "Service.java";
		} else if (type == GenerateFileType.Service_Implementation) {// ServiceImpl
			return "service" + File.separator + "impl" + File.separator
					+ modelStr + "ServiceImpl.java";
		} else if (type == GenerateFileType.DAO_Interface) {// DAO
			return "dao" + File.separator + modelStr + "DAO.java";
		} else if (type == GenerateFileType.DAO_Implementation) {// DAOImpl
			return "dao" + File.separator + modelStr + "DAOImpl.java";
		}
		return null;
	}
	/**
	 * convert the comment content to javadoc.
	 * @param ast the eclipse ast.
	 * @param comment the method comment content.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Javadoc getJavadoc(AST ast, String comment) {
		if (comment != null && comment.length() > 0) {
			Javadoc jc = ast.newJavadoc();
			TagElement tag = ast.newTagElement();
			TextElement te = ast.newTextElement();
			tag.fragments().add(te);
			te.setText(formatJavadoc(comment));
			jc.tags().add(tag);
			return jc;
		}
		return null;
	}
	/**
	 * format the javadoc
	 * @param comment comment the method comment content.
	 * @return the formatted javadoc.
	 */
	private static String formatJavadoc(String comment) {
		StringBuffer sb = new StringBuffer("");
		StringTokenizer st = new StringTokenizer(comment, "\n");
		for (int i = 0; st.hasMoreTokens(); i++) {
			String _tmp = st.nextToken().trim();
			if(i>0&&_tmp!=null&&_tmp.length()>0)
				sb.append("* ").append(_tmp).append("\n");
			else if(i==0)
				sb.append(_tmp).append("\n");
		}
		return sb.toString().trim();
	}
	public static IFile getSuitableIFile(String xmlFilePath,
			String xmlFileName, GenerateFileType type) {
		String a1 = Utils.convertIbatisXml2JavaFile(xmlFileName, type);
		String a2 = Utils.reduceJavaFilePath(xmlFilePath, xmlFileName, a1);
		IFile a3 = Utils.getModelFileFromPath(a2);
		return a3;
	}
	/**
	 * judge the pending generated method is exist.
	 * @param cu generally,it is means the java class. 
	 * @param outMethodName the method name
	 * @return
	 */
	public static boolean isMethodExist(ICompilationUnit cu,
			String outMethodName) {
		IType classType = cu.findPrimaryType();
		try {
			IMethod[] methods = classType.getMethods();
			if (methods != null) {
				for (IMethod method : methods) {
					String methodName = method.getElementName();
					if (methodName != null && methodName.equals(outMethodName))
						return true;
				}
			}
		} catch (Exception e) {

		}
		return false;
	}
	/**
	 * write the generated code content to the specific file name.
	 * @param fileName the generated file name.
	 * @param content the java class code content.
	 */
	public static void writeContent(String fileName, String content) {
		try {
			FileWriter fw = new FileWriter(fileName);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}
	/**
	 * resovle the xml file and return the model name.
	 * @param xmlFile the ibatis xml file.
	 * @return return the resolved model name.
	 */
	public static String getModelName(String xmlFile) {
		if (xmlFile == null || xmlFile.length() == 0)
			return null;
		if (xmlFile.indexOf("SqlMap") > -1) {
			return null;
		}
		xmlFile = stripFileExtension(xmlFile);
		StringBuffer sb = new StringBuffer();
		if (xmlFile.indexOf("_") == -1) {
			sb.append(upFirstChar(xmlFile.toLowerCase()));
		} else {
			String[] arrs = xmlFile.split("_");
			if (arrs != null) {
				for (String key : arrs) {
					sb.append(upFirstChar(key.toLowerCase()));
				}
			}
		}
		String modelStr = sb.toString();
		return modelStr;
	}
	/**
	 * uppercase the first character of string.
	 * @param str the string
	 * @return the string with first character uppercase.
	 */
	public static String upFirstChar(String str) {
		if (str == null || str.length() == 0)
			return str;
		StringBuffer sb = new StringBuffer();
		if (str != null && str.length() > 0) {
			String ss = str.charAt(0) + "";
			sb.append(ss.toUpperCase());
		}
		sb.append(str.substring(1));
		return sb.toString();
	}
	/**
	 * lowercase the first character of string.
	 * @param str the string
	 * @return the string with first character lowercase.
	 */
	public static String lowFirstChar(String str) {
		if (str == null || str.length() == 0)
			return str;
		StringBuffer sb = new StringBuffer();
		if (str != null && str.length() > 0) {
			String ss = str.charAt(0) + "";
			sb.append(ss.toLowerCase());
		}
		sb.append(str.substring(1));
		return sb.toString();
	}
	/**
	 * resolve the file path to get the model file.
	 * @param filePath the file path.
	 * @return the model file.
	 */
	public static IFile getModelFileFromPath(String filePath) {
		IFile file = null;
		try {
			if (filePath != null) {
				file = ResourcesPlugin
						.getWorkspace()
						.getRoot()
						.getFileForLocation(
								new org.eclipse.core.runtime.Path(filePath));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
}
