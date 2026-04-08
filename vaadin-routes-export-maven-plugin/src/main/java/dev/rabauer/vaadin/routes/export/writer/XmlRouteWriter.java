package dev.rabauer.vaadin.routes.export.writer;

import dev.rabauer.vaadin.routes.export.model.RouteDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Writes route descriptors as indented UTF-8 XML.
 *
 * <p>Structure:
 * <pre>{@code
 * <routes>
 *   <route>
 *     <path>admin</path>
 *     <className>com.example.AdminView</className>
 *     <layouts>
 *       <layout>com.example.MainLayout</layout>
 *     </layouts>
 *     <roles>
 *       <role>ADMIN</role>
 *     </roles>
 *     <access>RESTRICTED</access>
 *     <securitySource>ANNOTATION</securitySource>
 *     <aliases>
 *       <alias>/old-admin</alias>
 *     </aliases>
 *     <dynamic>true</dynamic>
 *   </route>
 * </routes>
 * }</pre>
 * Fields that are {@code null} or empty are omitted.</p>
 */
public class XmlRouteWriter implements RouteWriter {

    @Override
    public void write(List<RouteDescriptor> routes, File outputFile) throws IOException {
        File parent = outputFile.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();

            Element root = doc.createElement("routes");
            doc.appendChild(root);

            for (RouteDescriptor route : routes) {
                root.appendChild(toElement(doc, route));
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            transformer.transform(new DOMSource(doc), new StreamResult(outputFile));
        } catch (Exception e) {
            throw new IOException("Failed to write XML output", e);
        }
    }

    private Element toElement(Document doc, RouteDescriptor route) {
        Element routeEl = doc.createElement("route");

        appendText(doc, routeEl, "path", route.getPath());
        appendText(doc, routeEl, "className", route.getClassName());

        if (route.getLayouts() != null && !route.getLayouts().isEmpty()) {
            Element layoutsEl = doc.createElement("layouts");
            for (String layout : route.getLayouts()) {
                appendText(doc, layoutsEl, "layout", layout);
            }
            routeEl.appendChild(layoutsEl);
        }

        if (route.getRoles() != null) {
            Element rolesEl = doc.createElement("roles");
            for (String role : route.getRoles()) {
                appendText(doc, rolesEl, "role", role);
            }
            routeEl.appendChild(rolesEl);
        }

        if (route.getAccess() != null) {
            appendText(doc, routeEl, "access", route.getAccess().name());
        }

        if (route.getSecuritySource() != null) {
            appendText(doc, routeEl, "securitySource", route.getSecuritySource().name());
        }

        if (route.getAliases() != null && !route.getAliases().isEmpty()) {
            Element aliasesEl = doc.createElement("aliases");
            for (String alias : route.getAliases()) {
                appendText(doc, aliasesEl, "alias", alias);
            }
            routeEl.appendChild(aliasesEl);
        }

        if (route.isDynamic()) {
            appendText(doc, routeEl, "dynamic", "true");
        }

        return routeEl;
    }

    private void appendText(Document doc, Element parent, String tagName, String value) {
        Element el = doc.createElement(tagName);
        el.setTextContent(value);
        parent.appendChild(el);
    }
}
