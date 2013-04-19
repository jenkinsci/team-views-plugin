/*
 * The MIT License
 *
 * Copyright 2013 Sony Mobile Communications AB. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.sonymobile.jenkins.plugins.teamview;

import com.thoughtworks.xstream.XStream;
import hudson.Functions;
import hudson.XmlFile;
import hudson.model.MyViewsProperty;
import hudson.model.User;
import hudson.model.View;
import hudson.security.AccessDeniedException2;
import hudson.util.XStream2;
import jenkins.model.Jenkins;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility methods for reading users. Most utility methods in {@link hudson.model.User} are private, so there are copies
 * of those here. Let's keep our fingers crossed that they don't change too much in core down the road.
 *
 * @author Robert Sandell &lt;robert.sandell@sonymobile.com&gt;
 */
public final class UserUtil {

    /**
     * Used to load/save user configuration.
     */
    private static final XStream XSTREAM = new XStream2();

    static {
        XSTREAM.alias("user", User.class);
        XSTREAM.alias("views", LinkedList.class);
    }

    /**
     * Utility constructor.
     */
    private UserUtil() {
    }

    /**
     * Reads the user from disk and returns the {@link MyViewsProperty} inside.
     *
     * @param idOrFullName the user id.
     * @return the views property.
     *
     * @throws IOException                  if so.
     * @throws TransformerException         if something goes wrong in the XML to String transformation.
     * @throws SAXException                 if something goes wrong in the initial parsing of the config file.
     * @throws ParserConfigurationException if a DocumentBuilder cannot be created.
     */
    public static List<View> unmarshalViews(String idOrFullName) throws IOException, ParserConfigurationException,
                                                                        SAXException, TransformerException {
        XmlFile xml = getConfigFile(idOrFullName);
        if (xml.getFile().exists()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xml.getFile());
            Element element = doc.getDocumentElement();
            NodeList tags = element.getElementsByTagName("hudson.model.MyViewsProperty");
            if (tags != null && tags.getLength() > 0) {
                Element propElement = (Element)tags.item(0);
                NodeList views = propElement.getElementsByTagName("views");
                if (views != null && views.getLength() > 0) {
                    Element viewNode = (Element)views.item(0);
                    viewNode = cleanFirstLevelOwner(viewNode);
                    StringWriter sw = new StringWriter();
                    Transformer t = TransformerFactory.newInstance().newTransformer();
                    t.transform(new DOMSource(viewNode), new StreamResult(sw));
                    String viewsXmlString = sw.toString();
                    return (List<View>)XSTREAM.fromXML(viewsXmlString);
                } else {
                    return Collections.emptyList();
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Removes any nodes with the tag name "owner" from the first level of children.
     *
     * @param viewNode the node who's children to free of their owner
     * @return the same element.
     */
    private static Element cleanFirstLevelOwner(Element viewNode) {
        NodeList childNodes = viewNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node instanceof Element) {
                Element child = (Element)node;
                NodeList owners = child.getElementsByTagName("owner");
                for (int j = 0; j < owners.getLength(); j++) {
                    Node o = owners.item(j);
                    child.removeChild(o);
                }
            }
        }
        return viewNode;
    }

    /**
     * Checks if there is a config.xml file for the provided user id.
     *
     * @param idOrFullName the user name
     * @return true if so.
     */
    public static boolean userExists(String idOrFullName) {
        File f = getConfigFileFor(idOrFullName);
        return f != null && f.exists();
    }

    /**
     * Gets the directory where Hudson stores user information.
     *
     * @return jenkins-root/users
     */
    protected static File getRootDir() {
        return new File(Jenkins.getInstance().getRootDir(), "users");
    }

    /**
     * The user configuration file.
     *
     * @param idOrFullName the user id
     * @return The config file for the user
     */
    public static XmlFile getConfigFile(String idOrFullName) {
        if (idOrFullName == null) {
            return null;
        }
        return new XmlFile(XSTREAM, getConfigFileFor(idOrFullName));
    }

    /**
     * The config file for the user.
     *
     * @param idOrFullName the user id
     * @return the file pointer, even if it doesn't exist
     */
    protected static File getConfigFileFor(String idOrFullName) {
        String id = toStorageId(idOrFullName);
        if (id == null) {
            return null;
        }
        return new File(getRootDir(), id + "/config.xml");
    }

    /**
     * Performs a bunch of string replacing to get to what the user id directory would be named. Copied from {@link
     * hudson.model.User#get(String, boolean)}
     *
     * @param idOrFullName the user id
     * @return the converted string
     */
    protected static String toStorageId(String idOrFullName) {
        if (idOrFullName == null) {
            return null;
        }
        String id = idOrFullName.replace('\\', '_').replace('/', '_').replace('<', '_')
                .replace('>', '_');  // 4 replace() still faster than regex
        if (Functions.isWindows()) {
            id = id.replace(':', '_');
        }
        return id;
    }

    /**
     * Checks that the current user has access to the provided user's views.
     *
     * @param idOrFullName id of the user to get the views from.
     */
    public static void checkViewsReadPermission(String idOrFullName) {
        User u = User.get(idOrFullName);
        if (u != null) {
            //getViews performs a hasPermission of each view
            //So the list returned is the list we can read
            //It probably always contains the all view if we can read it at all.
            if (u.getProperty(MyViewsProperty.class).getViews().size() <= 0) {
                throw new AccessDeniedException2(Jenkins.getAuthentication(), View.READ);
            }
        }
    }
}
