package com.example.bar_retina_app;


import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class UtilsConfigXML {

    private static final String FILE_NAME = "CONFIG.XML";

    public static boolean configExists(Context context) {
        File file = new File(context.getFilesDir(), FILE_NAME);
        return file.exists();
    }

    public static void saveConfig(Context context, String url, String camarero) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // Root element
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("config");
            doc.appendChild(rootElement);

            // URL element
            Element urlElement = doc.createElement("url");
            urlElement.appendChild(doc.createTextNode(url));
            rootElement.appendChild(urlElement);

            // Camarero element
            Element camareroElement = doc.createElement("camarero");
            camareroElement.appendChild(doc.createTextNode(camarero));
            rootElement.appendChild(camareroElement);

            // Write the content into file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir(), FILE_NAME));
            StreamResult result = new StreamResult(fos);

            transformer.transform(source, result);
            fos.close();

        } catch (Exception e) {
            Log.e("UtilsConfigXML", "Error saving config: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String[] readConfig(Context context) {
        String[] configData = new String[2]; // [0] = url, [1] = camarero
        try {
            File file = new File(context.getFilesDir(), FILE_NAME);
            if (!file.exists()) return null;

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();

            configData[0] = doc.getElementsByTagName("url").item(0).getTextContent();
            configData[1] = doc.getElementsByTagName("camarero").item(0).getTextContent();

            return configData;
        } catch (Exception e) {
            Log.e("UtilsConfigXML", "Error reading config: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
