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
package net.sf.fast.ibatis.build;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.fast.ibatis.model.FastIbatisConfig;
import net.sf.fast.ibatis.util.Utils;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.internal.core.util.SimpleDocument;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

/**
 * <p>
 * The main code builder.
 * it will generate the method definition including method name
 * method parameters and return type.
 * </p>
 * 
 * @author trami
 * @version 1.0
 */
public abstract class AbstractCodeBuilder implements CodeBuilder {
	/**
	 * <p>
	 * the method content generated in the subclass.
	 * </p>
	 * @param ast the ast
	 * @param fc the configuration to generate
	 * @return the code block
	 */
	public abstract Block createBlock(AST ast,FastIbatisConfig fc);
	/**
	 * <p>
	 * Now we can generate 2 Categories
	 * </p>
	 * @return 1 = Service Category.
	 *         2 = DAO Category.
	 */
	public abstract int getGenerateCategory();
	
	@SuppressWarnings("unchecked")
	private MethodDeclaration createMethodDeclaration(AST ast,TypeDeclaration type,FastIbatisConfig fc) {
		MethodDeclaration methodDeclaration = ast.newMethodDeclaration();
		methodDeclaration.setConstructor(false);
		List modifiers = methodDeclaration.modifiers();
		modifiers.add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		methodDeclaration.setName(ast.newSimpleName(fc.getMethodName()));
		String _str = fc.getReturnType();
		if(_str==null||_str.equalsIgnoreCase("void"))
			methodDeclaration.setReturnType2(ast.newPrimitiveType(PrimitiveType.VOID));
		else
			methodDeclaration.setReturnType2(ast.newSimpleType(ast.newSimpleName(_str)));
		SingleVariableDeclaration variableDeclaration = ast.newSingleVariableDeclaration();
		variableDeclaration.setType(ast.newSimpleType(ast.newSimpleName(fc.getParamType())));
		variableDeclaration.setName(ast.newSimpleName("param"));
		methodDeclaration.parameters().add(variableDeclaration);
		return methodDeclaration;
	}

	public Map<String,String> generateCode(FastIbatisConfig fc) {
		Map<String,String> ret = new HashMap<String,String>();
		try {
			GenerateFileType interfaceType = null;
			GenerateFileType implType = null;
			if(getGenerateCategory()==1) {
				interfaceType = GenerateFileType.Service_Interface;
				implType = GenerateFileType.Service_Implementation;
			}
			else if(getGenerateCategory()==2) {
				interfaceType = GenerateFileType.DAO_Interface;
				implType = GenerateFileType.DAO_Implementation;
			}
			ICompilationUnit SrvInterface_cu = JavaCore.createCompilationUnitFrom(Utils.getSuitableIFile(fc.getXmlFilePath(),fc.getXmlFileName(),interfaceType));
			String javaFileName = Utils.convertIbatisXml2JavaFile(fc.getXmlFileName(),interfaceType);
			final String finalJavaPath = Utils.reduceJavaFilePath(fc.getXmlFilePath(), fc.getXmlFileName(), javaFileName);
			String code = generateCode(SrvInterface_cu,fc);
			ret.put(finalJavaPath, code);
			ICompilationUnit Srv_cu = JavaCore.createCompilationUnitFrom(Utils.getSuitableIFile(fc.getXmlFilePath(),fc.getXmlFileName(),implType));
			String javaFileName1 = Utils.convertIbatisXml2JavaFile(fc.getXmlFileName(),implType);
			final String finalJavaPath1 = Utils.reduceJavaFilePath(fc.getXmlFilePath(), fc.getXmlFileName(), javaFileName1);
			String code1 = generateCode(Srv_cu,fc);
			ret.put(finalJavaPath1, code1);
		}catch(Exception e) {
			
		}
		return ret;
	}
	@SuppressWarnings("unchecked")
	private String generateCode(ICompilationUnit cu,FastIbatisConfig fc) {
		if(cu==null||Utils.isMethodExist(cu,fc.getMethodName()))
			return null;
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(cu);
		// parser.setResolveBindings(false);
		CompilationUnit astRoot = (CompilationUnit) parser.createAST(null);
		AST ast = astRoot.getAST();// AST.newAST(AST.JLS3);
		TypeDeclaration type = ((TypeDeclaration) astRoot.types().get(0));
		MethodDeclaration methodDeclaration = createMethodDeclaration(ast,type,fc);
		if(type.isInterface()) {
			
		} else {
			Block block = createBlock(ast,fc);
			methodDeclaration.setBody(block);
		}
		Javadoc jc = Utils.getJavadoc(ast,fc.getMethodComment());
		if(jc!=null)
			methodDeclaration.setJavadoc(jc);
		type.bodyDeclarations().add(methodDeclaration);
		String str = astRoot.toString();
		IDocument document = new SimpleDocument(str);
		//writeContent(fileName,newSource);
		Map options = cu.getJavaProject().getOptions(true);
		CodeFormatter formatter = new DefaultCodeFormatter(options);
		int indentationLevel= 0;//StringUtils.inferIndentationLevel(" ", getTabSize());
		TextEdit textEdit = formatter.format(CodeFormatter.K_COMPILATION_UNIT,
				str,
				 0,
				 str.length(),
				 indentationLevel,
				 "\n");
		try {
			textEdit.apply(document, TextEdit.NONE);
			String result = document.get();
			return result;
		} catch (Exception e) {
			
		}
		return null;
	}
}
