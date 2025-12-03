package com.adel.lifechoicebot;

public class EventoSpeciale extends Evento {
    public EventoSpeciale(int id, String descrizione, Scelta sceltaA, Scelta sceltaB) {
        super(id, descrizione, sceltaA, sceltaB);
    }

    @Override
    public String getTipo() {
        return "speciale";
    }
}
