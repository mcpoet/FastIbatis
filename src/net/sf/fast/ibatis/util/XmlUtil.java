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
				if(idValue!=null&&idValue.length()>0) {
					theMap.put(idValue,filterFromtables(selectPart.asXML()));
				}
			}
		} catch (Exception e) {

		}
		return theMap;
	}
	public static String filterFromtables(String xmlText) {
		String[] additive = new String[]{" ","\n"};				
		String lowXmlText = xmlText.toLowerCase();
		int pos = -1;
		for(int i=0;i<additive.length;i++)
			for(int j=0;j<additive.length;j++) {
				String key = additive[i]+"from"+additive[j];
				pos = lowXmlText.indexOf(key);
				if(pos>=0)
					break;
			}
		
		return null;
	}
}
