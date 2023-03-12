import java.net.*;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.*;

public class ClientHandler extends Thread {
    Socket client;
    BufferedReader in;
    PrintWriter out;

    public ClientHandler(Socket client) {
        this.client = client;
    }

    public void validationCases(String documentPath, String schemaPath) throws SAXException, IOException {
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = factory.newSchema(new File(schemaPath));
        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(documentPath));
    }

    public void validation(String libro) {
        try {
            validationCases("biblioteche.xml", "biblioteche.xsd");
            validationCases("libri.xml", "libri.xsd");
            validationCases("prestiti.xml", "prestiti.xsd");
            validationCases("utenti.xml", "utenti.xsd");
            search(libro);
        } catch (SAXException | IOException e) {
            System.out.println("Documenti non validi");
            e.printStackTrace();
        }
    }

    public void borrowings(NodeList listaPrestiti, String id) {
        for (int i = 0; i < listaPrestiti.getLength(); i++) {
            Element prestito = ((Element) listaPrestiti.item(i));
            if (prestito.getElementsByTagName("id_libro").item(0).getTextContent().equals(id)) {
                String inizio = prestito.getElementsByTagName("inizio").item(0).getTextContent();
                String fine = "...";
                String idCopia = prestito.getElementsByTagName("id_libro").item(0).getTextContent();
                if (prestito.getElementsByTagName("fine").item(0) != null) {
                    fine = prestito.getElementsByTagName("fine").item(0).getTextContent();
                }
                String identificativo = "";
                if (prestito.getElementsByTagName("partitaIva").item(0) != null) {
                    identificativo = prestito.getElementsByTagName("partitaIva").item(0).getTextContent();
                } else {
                    identificativo = prestito.getElementsByTagName("codiceFiscale").item(0).getTextContent();
                }
                System.out.println("Prestito " + (i + 1) + "\n\tId copia: " + idCopia + "\n\tInizio: " + inizio
                        + "\n\tFine: " + fine
                        + "\n\tIdentificativo: " + identificativo);
            }
        }
    }

    public void searchByName(Element rootLibri, Element rootPrestiti, String libro) {
        NodeList listaLibri = rootLibri.getElementsByTagName("libro");
        NodeList listaPrestiti = rootPrestiti.getElementsByTagName("prestito");
        for (int i = 0; i < listaLibri.getLength(); i++) {
            Node titolo = ((Element) listaLibri.item(i)).getElementsByTagName("titolo").item(0);
            if (titolo.getTextContent()
                    .equalsIgnoreCase(libro)) {
                String id = titolo.getParentNode().getAttributes().getNamedItem("id").getTextContent();
                borrowings(listaPrestiti, id);
            }
        }
    }

    public void search(String libro) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse("libri.xml");
            Element rootLibri = document.getDocumentElement();
            document = builder.parse("prestiti.xml");
            Element rootPrestiti = document.getDocumentElement();
            searchByName(rootLibri, rootPrestiti, libro);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        String libro = "";
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            libro = in.readLine();
            System.out.println("Libro: " + libro);
        } catch (IOException e) {
            e.printStackTrace();
        }
        validation(libro);
    }
}
