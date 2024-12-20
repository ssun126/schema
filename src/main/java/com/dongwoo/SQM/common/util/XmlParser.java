package com.dongwoo.SQM.common.util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

public class XmlParser {

	private Document xmlDoc = null;

	public XmlParser( InputStream requestXml ){

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder builder = null;

		try{
			builder = factory.newDocumentBuilder(); //factory를 사용해서 DOM Parser를 얻는다.
			xmlDoc = builder.parse(requestXml); //InputStream을 parsing하기 위해 DOM사용한다.
		}catch(SAXException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	public String[] getByTagName(String tagName){
		if(!(xmlDoc == null)){
			NodeList nodes = xmlDoc.getElementsByTagName(tagName);

			if(nodes.getLength() > 0){
				String[] contents = new String[nodes.getLength()];
				for(int i = 0 ; i < nodes.getLength(); i++){
					Node node = nodes.item(i).getLastChild();
					//contents[i] = nodes.item(i).getTextContent();// JDK1.6 일경우
					// JDK1.5이하일 경우
					//if( node.getNodeType() == 3 ){ // 3이면 Text
					//	contents[i] = node.getNodeValue(); //Text노드의 경우 문자열을 반환한다.
					//}
				}
				return contents;
			}
		}
		return null;
	}

    public static String[] getXPathString(String strXml, String nodeExpNm1, String nodeExpNm2)
    		throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {

		/*
		//파일에서 직접 파싱할경우
		InputSource   is = new InputSource(new FileReader("C:/TEMP/test.xml"));
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

		//URL에서 파싱할경우
		Document document =DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("http://xxx.com");
		*/

		//String[] rtnXml = null;

		//String xml 문자열에서 파싱할경우
    	strXml = strXml.replace(" ", "");
		InputSource   is = new InputSource(new StringReader(strXml));
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

		// xpath 생성
		XPath  xpath = XPathFactory.newInstance().newXPath();

		//String expression = "//*/person";
		String expression = nodeExpNm1;
		NodeList  nodes = (NodeList) xpath.compile(expression).evaluate(document, XPathConstants.NODESET);
		if(nodes.getLength() > 0) {
			String[] contents = new String[nodes.getLength()];
			for( int i=0; i<nodes.getLength(); i++ ){

				//String ssn=nodes.item(idx).getAttributes().item(0).getTextContent();//node textContext
				//System.out.println("ssn................"+ssn);

				//expression = "//*[@ssn="+ssn+"]/firstName";
				String expression2 = nodeExpNm2;
				String nodeName = xpath.compile(expression2).evaluate(document);
				contents[i] = nodeName;
				//System.out.println(expression2 + "................"+nodeName);

			}
			return contents;
		}
    	return null;
    }

    public static NodeList getXPathNodeList(String strXml, String nodeExpNm)
    		throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {

		/*
		//파일에서 직접 파싱할경우
		InputSource   is = new InputSource(new FileReader("C:/TEMP/test.xml"));
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

		//URL에서 파싱할경우
		Document document =DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("http://xxx.com");
		*/

    	//NodeList rtnXml = null;

		//String xml 문자열에서 파싱할경우
    	strXml = strXml.replace(" ", "");
		InputSource   is = new InputSource(new StringReader(strXml));
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

		// xpath 생성
		XPath  xpath = XPathFactory.newInstance().newXPath();

		//String expression = "//*/person";
		String expression = nodeExpNm;
		NodeList  nodeList = (NodeList) xpath.compile(expression).evaluate(document, XPathConstants.NODESET);

    	return nodeList;
    }
}
