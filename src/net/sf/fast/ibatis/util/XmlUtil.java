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

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.fast.ibatis.build.HandleType;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * <p>
 * the xml util to judge selection on ibatis xml file.
 * </p>
 * 
 * @author dan.zheng
 * @version 1.0
 */
public class XmlUtil {
	/**
	 * <p>
	 * the ibatis id attribute,the dao class will invoke by it.
	 * </p>
	 * 
	 * @param xmlFileName
	 *            the xml file name
	 * @param idName
	 *            the ibatis id attribute
	 * @return
	 */
	public static HandleType getSelectedIdType(String xmlFileName, String idName) {
		try {

			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(new File(xmlFileName));
			Element selectNode = (Element) document
					.selectSingleNode("//sqlMap/select[@id='" + idName + "']");
			Element updateNode = (Element) document
					.selectSingleNode("//sqlMap/update[@id='" + idName + "']");
			Element deleteNode = (Element) document
					.selectSingleNode("//sqlMap/delete[@id='" + idName + "']");
			Element insertNode = (Element) document
					.selectSingleNode("//sqlMap/insert[@id='" + idName + "']");
			if (selectNode != null)
				return HandleType.SELECT;
			else if (updateNode != null)
				return HandleType.UPDATE;
			else if (deleteNode != null)
				return HandleType.DELETE;
			else if (insertNode != null)
				return HandleType.INSERT;
		} catch (Exception e) {

		}
		return HandleType.NONE;
	}

	/**
	 * get the select from table sets.
	 * 
	 * @param xmlFileName
	 * @return
	 */
	public static Map<String, String> getSelectFromtableSets(String xmlFileName) {
		Map<String, String> theMap = new HashMap<String, String>();
		try {
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(new File(xmlFileName));
			Element root = document.getRootElement();
			for (Iterator i = root.elementIterator("select"); i.hasNext();) {
				Element selectPart = (Element) i.next();
				String idValue = selectPart.attributeValue("id");
				String tbs = filterFromtables(selectPart.asXML());
				if(idValue!=null&&tbs!=null) {
					theMap.put(idValue,tbs);
				}
			}
		} catch (Exception e) {

		}
		return theMap;
	}
	
	public static void main(String args[]) {
		String sql = "select * from table1";
		String sql2 = "select * from a1,b1 where a1.id = b1.id";		
		String str = filterFromtables(sql2);
		System.out.println(str);
	}
	public static String filterFromtables(String xmlText) {
		String[] additive = new String[]{" from "," from\n","\nfrom ","\nfrom\n"};				
		String lowXmlText = xmlText.toLowerCase();
		lowXmlText = lowXmlText.replaceAll(", ",",");
		lowXmlText = lowXmlText.replaceAll(" ,",",");
		StringBuffer sb = new StringBuffer();
		int pos = -1;
		for(int i=0;i<additive.length;i++) {
			String key = additive[i];
			pos = lowXmlText.indexOf(key);
			if(pos>=0) {
				pos += key.length();
				break;
			}
		}
		if(pos==-1)
			return null;
		boolean isContainComma = true;
		int startPos = formatStartPos(lowXmlText,pos);
		while(isContainComma) {
			StringBuffer xb = new StringBuffer();
			for(int i=startPos;i<lowXmlText.length();i++) {
				if(lowXmlText.charAt(i)==',') {
					isContainComma = true;
					xb.append(lowXmlText.charAt(i));					
					startPos = i+1;
					break;
				} else if(lowXmlText.charAt(i)==' ') {
					isContainComma = false;
					startPos = i+1;
					break;
				} else {
					xb.append(lowXmlText.charAt(i));
					isContainComma = false;
				}
			}
			sb.append(xb);
		}
		return sb.toString();
	}
	public static int formatStartPos(String text,int pos) {
		int startPos = pos;
		for(int i=startPos;i<text.length();i++) {
			if(isSpecialChar(text.charAt(i)))
				continue;
			else {
				startPos=i;
				break;
			}
		}
		return startPos;
	}
	public static boolean isSpecialChar(char c) {
		String[] prototype = new String[]{" ","\n"};
		for(int i=0;i<prototype.length;i++) {
			if(String.valueOf(c).equals(prototype[i]))
				return true;
		}
		return false;
	}
}
