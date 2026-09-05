package com.hackhub;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class HackHub {

    public static void main(String[] args) {
        TeamService team = new TeamService();
        InvitoService inviti = new InvitoService();
        System.out.println("=== HackHub - Parte 3: inviti + State ===\n");

        Utente alice = new Utente("alice", "alice@mail.it", "pw1", "Alice", "Rossi");
        Utente bob   = new Utente("bob",   "bob@mail.it",   "pw2", "Bob",   "Bianchi");
        Utente carla = new Utente("carla", "carla@mail.it", "pw3", "Carla", "Neri");

        Team t = team.creaTeam(alice, "Byte Squad", "Team di prova", 4);


        Invito invito = team.invita(t, alice, bob);
        System.out.println("[Invita] " + invito + " (stato = " + invito.getStato().nome() + ")");
        inviti.accetta(invito);
        System.out.println("[Accetta] stato = " + invito.getStato().nome()
                + " | membri = " + t.getMembri());


        Invito invito2 = team.invita(t, alice, carla);
        inviti.rifiuta(invito2);
        System.out.println("[Rifiuta] stato = " + invito2.getStato().nome());
    }
}



enum RuoloUtente { VISITATORE, UTENTE, MEMBRO_TEAM, TEAM_LEADER, ORGANIZZATORE, GIUDICE, MENTORE }

enum StatoTeam { ATTIVO, COMPLETO, ELIMINATO }






