package com.adel.lifechoicebot;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ParserEventi {

    public static List<Evento> caricaEventiDaXml() {
        List<Evento> eventi = new ArrayList<>();

        try {
            InputStream is = ParserEventi.class.getClassLoader().getResourceAsStream("eventi.xml");
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

            NodeList listaEventi = doc.getElementsByTagName("evento");

            for (int i = 0; i < listaEventi.getLength(); i++) {
                Element e = (Element) listaEventi.item(i);

                int id = Integer.parseInt(e.getAttribute("id"));
                String tipo = e.getElementsByTagName("tipo").item(0).getTextContent();
                String descrizione = e.getElementsByTagName("testo").item(0).getTextContent();

                Element sceltaAElement = (Element) e.getElementsByTagName("sceltaA").item(0);
                String testoA = sceltaAElement.getElementsByTagName("testo").item(0).getTextContent();
                int saluteA = Integer.parseInt(sceltaAElement.getElementsByTagName("salute").item(0).getTextContent());
                int felicitaA = Integer.parseInt(sceltaAElement.getElementsByTagName("felicita").item(0).getTextContent());
                int denaroA = Integer.parseInt(sceltaAElement.getElementsByTagName("denaro").item(0).getTextContent());
                int energiaA = Integer.parseInt(sceltaAElement.getElementsByTagName("energia").item(0).getTextContent());
                String risultatoA = sceltaAElement.getElementsByTagName("risultato").item(0).getTextContent();

                Element sceltaBElement = (Element) e.getElementsByTagName("sceltaB").item(0);
                String testoB = sceltaBElement.getElementsByTagName("testo").item(0).getTextContent();
                int saluteB = Integer.parseInt(sceltaBElement.getElementsByTagName("salute").item(0).getTextContent());
                int felicitaB = Integer.parseInt(sceltaBElement.getElementsByTagName("felicita").item(0).getTextContent());
                int denaroB = Integer.parseInt(sceltaBElement.getElementsByTagName("denaro").item(0).getTextContent());
                int energiaB = Integer.parseInt(sceltaBElement.getElementsByTagName("energia").item(0).getTextContent());
                String risultatoB = sceltaBElement.getElementsByTagName("risultato").item(0).getTextContent();

                Scelta sceltaA = new Scelta(testoA, saluteA, felicitaA, denaroA, energiaA, risultatoA);
                Scelta sceltaB = new Scelta(testoB, saluteB, felicitaB, denaroB, energiaB, risultatoB);
                Evento evento;
                switch(tipo.toLowerCase()) {
                    case "positivo":
                        evento = new EventoPositivo(id, descrizione, sceltaA, sceltaB);
                        break;
                    case "negativo":
                        evento = new EventoNegativo(id, descrizione, sceltaA, sceltaB);
                        break;
                    case "speciale":
                        evento = new EventoSpeciale(id, descrizione, sceltaA, sceltaB);
                        break;
                    default:
                        evento = new EventoNormale(id, descrizione, sceltaA, sceltaB);
                        break;
                }
                eventi.add(evento);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return eventi;
    }
}
