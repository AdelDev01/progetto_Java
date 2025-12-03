package com.adel.lifechoicebot;

public class EventoNegativo extends Evento {
    public EventoNegativo(int id, String descrizione, Scelta sceltaA, Scelta sceltaB) {
        super(id, descrizione, sceltaA, sceltaB);
    }

    @Override
    public String getTipo() {
        return "negativo";
    }
}
