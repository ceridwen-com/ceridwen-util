/*******************************************************************************
 * Copyright 2016 Matthew J. Dovey (www.ceridwen.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.ceridwen.util.xml;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class XmlUtilities {
	public static <T>String generateXML(T data) throws JAXBException {
		StringWriter writer = new StringWriter();
		
		generateXML(writer, data);
		
		return writer.toString();
	}
		
	public static <T>void generateXML(Writer writer, T data) throws JAXBException {
		if (data == null) {
			return;
		}
		JAXBContext jaxbContext = JAXBContext.newInstance(data.getClass());
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		jaxbMarshaller.marshal(data, writer);
	}
	
	@SuppressWarnings("unchecked")
	public static <T>T processXML(InputStream stream, Class<T> type) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(type);

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();	
		T obj = (T)jaxbUnmarshaller.unmarshal(stream);		
		
		return obj;
	}
}
