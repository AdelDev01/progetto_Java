package com.adel.lifechoicebot;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.Random;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Engine extends TelegramLongPollingBot {

    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);

    private static Map<Long, Giocatore> giocatori = new ConcurrentHashMap<>();
    private Map<Long, String> statoUtente = new ConcurrentHashMap<>();
    private Map<Long, Evento> eventoCorrente = new ConcurrentHashMap<>();
    private Map<Long, Integer> eventiVissuti = new ConcurrentHashMap<>();
    private Map<Long, Integer> anniPassati = new ConcurrentHashMap<>();
    private List<Evento> eventi = ParserEventi.caricaEventiDaXml();

    public Engine() {
        super(new DefaultBotOptions());
    }

    @Override
    public String getBotUsername() {
        return "progettoshatani_bot";
    }

    @Override
    public String getBotToken() {
        return "6295448198:AAEFJiqSwT8UVIJ8GLp9R-V5l_7NghaXVJE";
    }

    public void onUpdateReceived(Update update) {
    threadPool.submit(() -> gestisciUpdate(update));
    }

    private void gestisciUpdate(Update update)
    {
        if (update.hasMessage() && update.getMessage().hasText())
        {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String chatId = update.getMessage().getChatId().toString();
                Long chatIdLong = update.getMessage().getChatId();
                String receivedText = update.getMessage().getText();

                if (receivedText.equalsIgnoreCase("/nuovavita")) {
                    statoUtente.put(chatIdLong, "ATTESA_NOME");
                    sendMessage(chatId, "Benvenuto! Come ti chiami?");
                    return;
                }
                if (receivedText.equalsIgnoreCase("/start")) {
                    sendMessage(chatId, "Ciao! Premi /nuovavita per iniziare una nuova avventura.");
                    return;
                }
                if ("ATTESA_NOME".equals(statoUtente.get(chatIdLong))) {
                    if (receivedText.matches(".*\\d.*")) {
                        sendMessage(chatId, "Il nome non deve contenere numeri. Inserisci solo lettere:");
                        return;
                    }
                    statoUtente.put(chatIdLong, "ATTESA_ETA");
                    giocatori.put(chatIdLong, new Giocatore(receivedText, 0, 100, 1000, 100, 100));
                    sendMessage(chatId, "Quanti anni hai?");
                    return;
                }

                if ("ATTESA_ETA".equals(statoUtente.get(chatIdLong))) {
                    try {
                        int eta = Integer.parseInt(receivedText);
                        Giocatore giocatore = giocatori.get(chatIdLong);
                        giocatore.setEta(eta);
                        statoUtente.put(chatIdLong, "DESCRIZIONE_GIOCO");
                        sendDescrizioneEGioca(chatId);
                    } catch (NumberFormatException e) {
                        sendMessage(chatId, "Per favore, inserisci un numero valido per l'età.");
                    }
                    return;
                }

                // Dopo la descrizione, mostra solo il pulsante "Inizia"
                if ("DESCRIZIONE_GIOCO".equals(statoUtente.get(chatIdLong))) {
                    if (receivedText.equalsIgnoreCase("Inizia")) {
                        Evento evento = eventi.get(new Random().nextInt(eventi.size()));
                        eventoCorrente.put(chatIdLong, evento);
                        statoUtente.remove(chatIdLong);
                        sendEventoConScelte(chatId, evento);
                    } else {
                        // Mostra solo il pulsante "Inizia" finché non viene premuto
                        sendDescrizioneEGioca(chatId);
                    }
                    return;
                }

                if (receivedText.equalsIgnoreCase("Info") && giocatori.containsKey(chatIdLong)) {
                    Giocatore g = giocatori.get(chatIdLong);
                    String info = "Nome: " + g.getNome() +
                                "\nEtà: " + g.getEta() +
                                "\nSalute: " + g.getSalute() +
                                "\nFelicità: " + g.getFelicita() +
                                "\nDenaro: " + g.getDenaro() +
                                "\nEnergia: " + g.getEnergia();
                    sendMessage(chatId, info);
                    return;
                }

                if (eventoCorrente.containsKey(chatIdLong)) {
                    Evento evento = eventoCorrente.get(chatIdLong);
                    Giocatore giocatore = giocatori.get(chatIdLong);

                    if (receivedText.trim().equalsIgnoreCase(evento.getSceltaA().getTesto().trim())) {
                        aggiornaParametri(giocatore, evento.getSceltaA());
                        StringBuilder risposta = new StringBuilder();
                        risposta.append("-------------------------------\n");
                        risposta.append("RISULTATO\n");
                        risposta.append("-------------------------------\n\n");
                        risposta.append(evento.getSceltaA().getRisultato()).append("\n\n");
                        risposta.append("-------------------------------\n\n");
                        risposta.append(formatVariazioni(evento.getSceltaA()));
                        sendMessage(chatId, risposta.toString());

                        // Dopo aver inviato il risultato...
                        int eventi = eventiVissuti.getOrDefault(chatIdLong, 0) + 1;
                        eventiVissuti.put(chatIdLong, eventi);

                        int anni = anniPassati.getOrDefault(chatIdLong, 0);

                        if (eventi % 10 == 0 && anni < 5) {
                            anni++;
                            anniPassati.put(chatIdLong, anni);

                            giocatore.setEta(giocatore.getEta() + 1);

                            if (anni == 5) {
                                SendMessage endMsg = new SendMessage();
                                endMsg.setChatId(chatId);
                                endMsg.setText("Hai finito la simulazione, se vuoi iniziare una nuova vita digita /nuovavita");
                                endMsg.setReplyMarkup(new ReplyKeyboardRemove(true));
                                try {
                                    execute(endMsg);
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }
                                shutdown();
                                return;
                            } else {
                                sendMessage(chatId, "È iniziato un nuovo anno! Ora hai " + giocatore.getEta() + " anni.");
                            }
                        }

                        // Poi mostra il nuovo evento solo se non è finita la simulazione
                        if (anni < 5) {
                            Evento nuovoEvento = scegliEventoConProbabilita(this.eventi);
                            eventoCorrente.put(chatIdLong, nuovoEvento);
                            sendEventoConScelte(chatId, nuovoEvento);
                        }
                        return;
                    } else if (receivedText.trim().equalsIgnoreCase(evento.getSceltaB().getTesto().trim())) {
                        aggiornaParametri(giocatore, evento.getSceltaB());
                        StringBuilder risposta = new StringBuilder();
                        risposta.append("-------------------------------\n");
                        risposta.append("RISULTATO\n");
                        risposta.append("-------------------------------\n\n");
                        risposta.append(evento.getSceltaB().getRisultato()).append("\n\n");
                        risposta.append("-------------------------------\n\n");
                        risposta.append(formatVariazioni(evento.getSceltaB()));
                        sendMessage(chatId, risposta.toString());

                        int eventi = eventiVissuti.getOrDefault(chatIdLong, 0) + 1;
                        eventiVissuti.put(chatIdLong, eventi);

                        int anni = anniPassati.getOrDefault(chatIdLong, 0);

                        if (eventi % 10 == 0 && anni < 5) {
                            anni++;
                            anniPassati.put(chatIdLong, anni);

                            giocatore.setEta(giocatore.getEta() + 1);

                            if (anni == 5) {
                                SendMessage endMsg = new SendMessage();
                                endMsg.setChatId(chatId);
                                endMsg.setText("Hai finito la simulazione, se vuoi iniziare una nuova vita digita /nuovavita");
                                endMsg.setReplyMarkup(new ReplyKeyboardRemove(true));
                                try {
                                    execute(endMsg);
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }
                                shutdown();
                                return;
                            } else {
                                sendMessage(chatId, "È iniziato un nuovo anno! Ora hai " + giocatore.getEta() + " anni.");
                            }
                        }

                        
                        if (anni < 5) {
                            Evento nuovoEvento = scegliEventoConProbabilita(this.eventi);
                            eventoCorrente.put(chatIdLong, nuovoEvento);
                            sendEventoConScelte(chatId, nuovoEvento);
                        }
                        return;
                    }
                    return;
                }
            }
        }
    } 


    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendEventoConScelte(String chatId, Evento evento) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        // Titolo in base al tipo evento
        String tipoEvento = evento.getTipo();
        String titolo = "EVENTO " + tipoEvento.toUpperCase();

        StringBuilder testo = new StringBuilder();
        testo.append("----------------------------\n");
        testo.append(titolo).append("\n");
        testo.append("----------------------------\n\n");
        testo.append(evento.getDescrizione()).append("\n\n");
        testo.append("----------------------------\n\n");

        Scelta a = evento.getSceltaA();
        testo.append("A: ").append(a.getTesto())
             .append(" (")
             .append(formatVariazioni(a))
             .append(")\n\n");

        Scelta b = evento.getSceltaB();
        testo.append("B: ").append(b.getTesto())
             .append(" (")
             .append(formatVariazioni(b))
             .append(")");

        message.setText(testo.toString());

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new java.util.ArrayList<>();

        KeyboardRow rowA = new KeyboardRow();
        rowA.add(a.getTesto());
        keyboard.add(rowA);

        KeyboardRow rowB = new KeyboardRow();
        rowB.add(b.getTesto());
        keyboard.add(rowB);

        KeyboardRow rowInfo = new KeyboardRow();
        rowInfo.add("Info");
        keyboard.add(rowInfo);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendDescrizioneEGioca(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("----------------------------------\n" +
                "Benvenuto nel gioco della vita!\n" +
                "In questo gioco, prenderai decisioni che influenzeranno la tua vita virtuale.\n" +
                "Avrai a disposizione salute, felicità, denaro ed energia.\n" +
                "Ogni scelta avrà conseguenze sui tuoi parametri.\n" +
                "Pronto a iniziare? Premi il pulsante 'Inizia' per cominciare la tua avventura!\n" +
                "----------------------------------");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new java.util.ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Inizia");
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void aggiornaParametri(Giocatore giocatore, Scelta scelta) {
        giocatore.setSalute(Math.max(0, Math.min(100, giocatore.getSalute() + scelta.getSalute())));
        giocatore.setFelicita(Math.max(0, Math.min(100, giocatore.getFelicita() + scelta.getFelicita())));
        giocatore.setEnergia(Math.max(0, Math.min(100, giocatore.getEnergia() + scelta.getEnergia())));
        giocatore.setDenaro(Math.max(0, giocatore.getDenaro() + scelta.getDenaro()));
    }

    private String formatVariazioni(Scelta scelta) {
        StringBuilder sb = new StringBuilder();
        if (scelta.getFelicita() != 0) {
            sb.append((scelta.getFelicita() > 0 ? "+" : "")).append(scelta.getFelicita()).append(" felicità\n");
        }
        if (scelta.getEnergia() != 0) {
            sb.append((scelta.getEnergia() > 0 ? "+" : "")).append(scelta.getEnergia()).append(" energia\n");
        }
        if (scelta.getDenaro() != 0) {
            sb.append((scelta.getDenaro() > 0 ? "+" : "")).append(scelta.getDenaro()).append(" denaro\n");
        }
        if (scelta.getSalute() != 0) {
            sb.append((scelta.getSalute() > 0 ? "+" : "")).append(scelta.getSalute()).append(" salute\n");
        }
        return sb.toString().trim();
    }

    private Evento scegliEventoConProbabilita(List<Evento> eventi) {
        List<Evento> normali = new java.util.ArrayList<>();
        List<Evento> positivi = new java.util.ArrayList<>();
        List<Evento> negativi = new java.util.ArrayList<>();
        List<Evento> speciali = new java.util.ArrayList<>();

        for (Evento e : eventi) {
            switch (e.getTipo().toLowerCase()) {
                case "positivo": positivi.add(e); break;
                case "negativo": negativi.add(e); break;
                case "speciale": speciali.add(e); break;
                default: normali.add(e); break;
            }
        }

        int rand = new Random().nextInt(100);

        if (rand < 78 && !normali.isEmpty()) {
            return normali.get(new Random().nextInt(normali.size()));
        } else if (rand < 85 && !negativi.isEmpty()) {
            return negativi.get(new Random().nextInt(negativi.size()));
        } else if (rand < 96 && !positivi.isEmpty()) {
            return positivi.get(new Random().nextInt(positivi.size()));
        } else if (!speciali.isEmpty()) {
            return speciali.get(new Random().nextInt(speciali.size()));
        } else if (!normali.isEmpty()) {
            return normali.get(new Random().nextInt(normali.size()));
        } else {
            return eventi.get(new Random().nextInt(eventi.size()));
        }
    }

    public void shutdown() {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
