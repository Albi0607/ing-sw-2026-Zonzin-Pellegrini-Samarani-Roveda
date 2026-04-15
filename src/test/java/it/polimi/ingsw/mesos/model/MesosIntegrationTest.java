package it.polimi.ingsw.mesos.model;
import it.polimi.ingsw.mesos.model.board.Board;
import it.polimi.ingsw.mesos.model.board.OfferTile;
import it.polimi.ingsw.mesos.model.board.TurnOrderTrack;
import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.card.building.BuildingCard;
import it.polimi.ingsw.mesos.model.card.building.BuildingEffect;
import it.polimi.ingsw.mesos.model.card.building.ResourceBonusEffect;
import it.polimi.ingsw.mesos.model.card.character.*;
import it.polimi.ingsw.mesos.model.card.event.*;
import it.polimi.ingsw.mesos.model.deck.*;
import it.polimi.ingsw.mesos.model.enums.*;
import it.polimi.ingsw.mesos.model.state.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class MesosIntegrationTest {
    private Game game;
    private Player marco;
    private Player sofia;

    @BeforeEach
    void setUp() {
        // Setup per 2 giocatori
        List<Player> players = List.of(
                new Player("Marco", Color.RED),
                new Player("Sofia", Color.BLUE)
        );
        game = new Game(players);
        game.startGame(); // Inizializza mazzi, board e risorse

        // Recuperiamo i riferimenti (l'ordine iniziale è casuale nel startGame)
        marco = game.getPlayers().get(0);
        sofia = game.getPlayers().get(1);
    }

    @Test
    void testCompletoRoundUno() {
        Board board = game.getBoard();

        //verifica cibo iniziale
        assertEquals(2, game.getPlayers().get(0).getFood(), "Il primo giocatore deve avere 2 cibo");
        assertEquals(3, game.getPlayers().get(1).getFood(), "Il secondo giocatore deve avere 3 cibo");

        // Verifica Tessere Offerta
        List<OfferTile> tiles = board.getTiles();
        assertTrue(tiles.size() > 0);
        assertTrue(tiles.stream().allMatch(t -> t.getId() >= 'B'), "Le tessere dovrebbero essere ordinate alfabeticamente");

        // Verifica Fila Superiore
        // N+4 (6 carte) + Edifici Era I (1 per 2 player)
        int expectedUpperSize = 6 + 1;
        assertEquals(expectedUpperSize, board.getUpperRow().size(), "La fila superiore deve avere N+4 carte + edifici");


        // Verifichiamo che siamo in PlacingState
        assertTrue(game.getCurrentState() instanceof PlacingState);
        PlacingState ps = (PlacingState) game.getCurrentState();

        // Il primo giocatore sceglie la tessera 'B'
        OfferTile tileB = board.getTile('B');
        Player active1 = ps.getActivePlayer();
        game.placeTotemOnOffer(active1, tileB);

        // Il secondo giocatore prova a scegliere la STESSA tessera (deve fallire)
        Player active2 = ps.getActivePlayer();
        assertThrows(Exception.class, () -> game.placeTotemOnOffer(active2, tileB),
                "Non si può scegliere una tessera già occupata");

        // Il secondo sceglie la tessera 'C'
        OfferTile tileC = board.getTile('C');
        game.placeTotemOnOffer(active2, tileC);


        // Passaggio a ResolvingState
        assertTrue(game.getCurrentState() instanceof ResolvingState);
        ResolvingState rs = (ResolvingState) game.getCurrentState();

        // REGOLA: Risolve prima chi è più a sinistra (Tessera B prima di C)
        Player resolvingFirst = rs.getActivePlayer(game);
        assertEquals(active1.getNickname(), resolvingFirst.getNickname(),
                "Deve risolvere prima chi ha la tessera con lettera minore");

        // TEST VINCOLO EVENTI: Il giocatore non può prendere un evento (se presente)
        // Cerchiamo un eventuale evento nella riga superiore
        for (int i = 0; i < board.getUpperRow().size(); i++) {
            if (board.getUpperRow().get(i).getAsEventCard() != null) {
                final int eventIndex = i;
                assertThrows(Exception.class, () -> rs.takeCard(game, eventIndex, true),
                        "Il giocatore non deve poter prendere carte evento");
                break;
            }
        }

        int personaggiInizialiActive1 = active1.getTribe().getCharactersCount();

        // 1. Cerchiamo l'indice della prima carta prendibile nella fila INFERIORE
        int validIndexLower = -1;
        for (int i = 0; i < board.getLowerRow().size(); i++) {
            Card c = board.getLowerRow().get(i);

            if (c.getAsEventCard()==null) {
                validIndexLower = i;
                break;
            }
        }

        assertTrue(validIndexLower != -1, "Dovrebbe esserci almeno una carta non-evento sotto");
        int sizePrimaLower = board.getLowerRow().size();

        rs.takeCard(game, validIndexLower, false);

        assertEquals(expectedUpperSize, board.getUpperRow().size(), "La fila superiore non deve cambiare se prendi da sotto");
        assertEquals(sizePrimaLower - 1, board.getLowerRow().size(), "La carta deve essere rimossa dalla fila dopo la presa");


        // VERIFICHE:
        assertEquals(personaggiInizialiActive1 + 1, active1.getTribe().getCharactersCount(),
                "La tribù di Marco dovrebbe essere cresciuta di 1");


        // Ora il turno dovrebbe passare a Sofia (Tessera C)
        Player prossimoGiocatore = rs.getActivePlayer(game);
        assertEquals(active2.getNickname(), prossimoGiocatore.getNickname(),
                "Dopo Marco, deve toccare a Sofia");

        // 1. Cerchiamo l'indice della prima carta prendibile nella fila SUPERIORE
        int validIndexUpper = -1;
        for (int i = 0; i < board.getUpperRow().size(); i++) {
            Card c = board.getUpperRow().get(i);

            if (c.getAsEventCard()==null) {
                validIndexUpper = i;
                break;
            }
        }

        assertTrue(validIndexUpper != -1, "Dovrebbe esserci almeno una carta non-evento sotto");

        Card cardScelta = board.getUpperRow().get(validIndexUpper);
        int personaggiPrima = active2.getTribe().getCharactersCount();
        int edificiPrima = active2.getTribe().getBuildingsCount();

        rs.takeCard(game, validIndexUpper, true);

        if (cardScelta instanceof TribeCard) {
            assertEquals(personaggiPrima + 1, active2.getTribe().getCharactersCount(), "Personaggio non aggiunto");
        } else if (cardScelta instanceof BuildingCard) {
            assertEquals(edificiPrima + 1, active2.getTribe().getBuildingsCount(), "Edificio non aggiunto");
        }


        assertEquals(7, board.getUpperRow().size(), "La board deve essere stata ricaricata per il Round 2");

        assertTrue(game.getCurrentState() instanceof PlacingState, "Il gioco dovrebbe aver cambiato stato dopo l'ultima risoluzione");

        for (OfferTile t : board.getTiles()) {
            assertTrue(t.isAvailable(), "Tutte le tessere offerta devono essere libere per il round successivo");
        }


        PlacingState nextPlacing = (PlacingState) game.getCurrentState();

        assertEquals(active1, nextPlacing.getActivePlayer());

    }


    @Test
    void testTransizioneEra_I_a_II() {
        assertEquals(Era.ERA_I, game.getCurrentEra());
        assertFalse(game.getBoard().getUpperRow().isEmpty());

        // 2. FORZIAMO IL TRIGGER: Mettiamo una carta di ERA_II in cima al mazzo
        // Usiamo un Hunter di Era II creato al volo per il test
        TribeCard triggerCard = new Hunter(Era.ERA_II, 2, false);

        // mettiamo la carta dell'Era II in cima
        game.getBoard().getTribeDeck().put(triggerCard);


        game.getBoard().refillRows(6, game);


        // L'Era del gioco deve essere cambiata
        assertEquals(Era.ERA_II, game.getCurrentEra(), "Il gioco dovrebbe essere passato all'Era II");

        // Gli edifici dell'Era I devono essere "scesi" nella fila inferiore
        boolean edificiInLower = game.getBoard().getLowerRow().stream()
                .anyMatch(c -> c instanceof BuildingCard && c.getEra() == Era.ERA_I);
        assertTrue(edificiInLower, "Gli edifici dell'Era I dovrebbero essere stati spostati nella fila inferiore");

        // La fila superiore deve ora contenere gli edifici dell'Era II
        boolean edificiEra2InUpper = game.getBoard().getUpperRow().stream()
                .anyMatch(c -> c instanceof BuildingCard && c.getEra() == Era.ERA_II);
        assertTrue(edificiEra2InUpper, "Dovrebbero esserci i nuovi edifici dell'Era II nella fila superiore");

        // Contiamo gli edifici di Era I
        long era1Buildings = game.getBoard().getLowerRow().stream()
                .filter(c -> c instanceof BuildingCard && c.getEra() == Era.ERA_I)
                .count();

        // Contiamo gli edifici di Era II
        long era2Buildings = game.getBoard().getUpperRow().stream()
                .filter(c -> c instanceof BuildingCard && c.getEra() == Era.ERA_II)
                .count();

    }



    @Test
    void testTransizioneEra_II_a_III() {

        Board board = game.getBoard();

        game.setCurrentEra(Era.ERA_II);

        board.getUpperRow().clear();
        board.getLowerRow().clear();


        BuildingCard era1 = new BuildingCard(Era.ERA_I, 2, 0, null);
        BuildingCard era2_a = new BuildingCard(Era.ERA_II, 4, 0, null);
        BuildingCard era2_b = new BuildingCard(Era.ERA_II, 6, 0, null);

        board.getLowerRow().add(era1);
        board.getUpperRow().add(era2_a);
        board.getUpperRow().add(era2_b);

        TribeCard era3Trigger = new Hunter(Era.ERA_III, 0, false);
        board.getTribeDeck().put(era3Trigger);

        System.out.println("--- PRIMA DELLA TRANSIZIONE ---");
        System.out.println("Fila Inferiore: " + board.getLowerRow().size() + " edifici (Era I)");
        System.out.println("Fila Superiore: " + board.getUpperRow().size() + " edifici (Era II)");
        board.refillRows(6, game);

        // 4. VERIFICA DEI RISULTATI
        System.out.println("\n--- DOPO LA TRANSIZIONE ---");
        long countEra1 = board.getLowerRow().stream().filter(c -> c.getEra() == Era.ERA_I).count();
        long countEra2 = board.getLowerRow().stream().filter(c -> c.getEra() == Era.ERA_II).count();

        System.out.println("Edifici Era I rimasti sotto: " + countEra1);
        System.out.println("Edifici Era II scesi sotto: " + countEra2);

        // ASSERTIONS
        assertEquals(Era.ERA_III, game.getCurrentEra(), "L'era deve essere la III");
        assertEquals(0, countEra1, "ERRORE: Gli edifici di Era I dovrebbero essere stati eliminati!");
        assertTrue(countEra2 > 0, "ERRORE: Gli edifici di Era II dovrebbero essere scesi sotto, non spariti!");
    }


    @Test
    void testMidRefillEraTransition() {
        Board board = game.getBoard();
        while (!board.getTribeDeck().isEmpty()) {
            board.getTribeDeck().draw();
        }
        board.getUpperRow().clear();
        board.getLowerRow().clear();

        board.getTribeDeck().put(new Hunter(Era.ERA_II, 0, false));
        board.getTribeDeck().put(new Hunter(Era.ERA_II, 0, false));
        board.getTribeDeck().put(new Hunter(Era.ERA_II, 0, false));
        board.getTribeDeck().put(new Hunter(Era.ERA_II, 0, false));


        board.getTribeDeck().put(new Hunter(Era.ERA_I, 0, false));
        board.getTribeDeck().put(new Hunter(Era.ERA_I, 0, false));

        assertEquals(6, board.getTribeDeck().size());

        TribeCard topCard = board.getTribeDeck().draw();
        assertEquals(Era.ERA_I, topCard.getEra());
        board.getTribeDeck().put(topCard);

        // Mettiamo un edificio Era I sopra che deve "scendere" a metà refill
        BuildingCard oldBuilding = new BuildingCard(Era.ERA_I, 0, 0, null);
        board.getUpperRow().add(oldBuilding);

        // Chiediamo 6 carte
        board.refillRows(6, game);

        long era1_tribes = board.getUpperRow().stream()
                .filter(c -> c instanceof TribeCard && c.getEra() == Era.ERA_I)
                .count();

        long era2_tribes = board.getUpperRow().stream()
                .filter(c -> c instanceof TribeCard && c.getEra() == Era.ERA_II)
                .count();

        long era2_buildings = board.getUpperRow().stream()
                .filter(c -> c instanceof BuildingCard && c.getEra() == Era.ERA_II)
                .count();

        assertEquals(2, era1_tribes, "Dovrebbero esserci 2 tribù di Era I");
        assertEquals(4, era2_tribes, "Dovrebbero esserci 4 tribù di Era II");
        assertEquals(2, era2_buildings, "Dovrebbero esserci i 2 nuovi edifici di Era II");

        // A. L'era deve essere cambiata
        assertEquals(Era.ERA_II, game.getCurrentEra());

        //L'edificio vecchio deve essere sceso nella LowerRow
        assertTrue(board.getLowerRow().contains(oldBuilding), "L'edificio Era I deve essere sceso sotto");

        //Devono esserci i nuovi edifici di Era II sopra
        boolean nuoviEdificiSopra = board.getUpperRow().stream()
                .anyMatch(c -> c instanceof BuildingCard && c.getEra() == Era.ERA_II);
        assertTrue(nuoviEdificiSopra, "Gli edifici Era II sono stati aggiunti sopra correttamente");

        //Conteggio totale carte sopra: 6 Tribù + Nuovi Edifici
        long edificiSopra = board.getUpperRow().stream().filter(c -> c instanceof BuildingCard).count();
        long tribuSopra = board.getUpperRow().stream().filter(c -> c instanceof TribeCard).count();

        assertEquals(6, tribuSopra, "Devono esserci esattamente 6 tribù sopra");
        assertEquals(2, edificiSopra, "Devono esserci i nuovi edifici sopra");

    }


    @Test
    void testEraTransitionTriggeredByNextCardOnDeck() {
        Board board = game.getBoard();

        while (!board.getTribeDeck().isEmpty()) board.getTribeDeck().draw();
        board.getUpperRow().clear();
        board.getLowerRow().clear();

        board.getTribeDeck().put(new Hunter(Era.ERA_II, 0, false));

        // Inseriamo le 6 carte che verranno pescate nel refill (Era I)
        for (int i = 0; i < 6; i++) {
            board.getTribeDeck().put(new Hunter(Era.ERA_I, 0, false));
        }

        assertEquals(7, board.getTribeDeck().size());
        TribeCard top = board.getTribeDeck().draw();
        assertEquals(Era.ERA_I, top.getEra(), "La prima pescata deve essere Era I");
        board.getTribeDeck().put(top);

        BuildingCard edificioVecchio = new BuildingCard(Era.ERA_I, 1, 1, null);
        board.getUpperRow().add(edificioVecchio);

        // Pescherà le 6 carte Era I.
        // Finite le pescate, deve guardare la 7a e attivare l'Era II.
        board.refillRows(6, game);


        assertEquals(Era.ERA_II, game.getCurrentEra(),
                "L'era doveva cambiare perché la carta rimasta sul mazzo è di Era II");

        // Gli edifici di Era I devono essere scesi sotto
        boolean edificiScesi = board.getLowerRow().stream()
                .anyMatch(c -> c instanceof BuildingCard && c.getEra() == Era.ERA_I);
        assertTrue(edificiScesi, "Gli edifici devono scendere anche se il cambio è triggerato dal mazzo");

    }




    @Test
    void extraDraw() {

        marco.addFood(20);
        sofia.addFood(10);

        Board board = game.getBoard();
        board.getUpperRow().clear();
        board.getLowerRow().clear();

        BuildingCard extraCard = new BuildingCard(Era.ERA_I, 2, 5, null);
        Hunter standardCardMarco = new Hunter(Era.ERA_I, 0, false);
        Hunter standardCardSofia = new Hunter(Era.ERA_I, 0, false);

        board.getUpperRow().add(extraCard);
        board.getLowerRow().add(standardCardSofia);
        board.getUpperRow().add(standardCardMarco);

        board.getTiles().forEach(t -> t.setHost(null));

        OfferTile tileB = board.getTile('B');
        tileB.setHost(sofia);

        OfferTile tileC = board.getTile('C');
        tileC.setHost(marco);

        marco.setExtraDraw();

        ResolvingState state = new ResolvingState();
        game.changeState(state);

        assertEquals(sofia, state.getActivePlayer(game));
        state.takeCard(game, 0, false);

        assertEquals(marco, state.getActivePlayer(game), "Deve toccare a Marco (Turno Standard)");
        state.takeCard(game, 0, true);

        System.out.println("Marco ha finito il turno standard. Controllo attivazione fase extra...");

        assertEquals(marco, state.getActivePlayer(game), "Deve toccare ANCORA a Marco (Potere Extra)");

        int cartePrimaExtra = marco.getTribe().getBuildings().size();
        state.takeCard(game, 0, true);
        int marcosTribe = marco.getTribe().getBuildings().size() + marco.getTribe().getCharacters().size();
        int sofiasTribe = sofia.getTribe().getBuildings().size() + sofia.getTribe().getCharacters().size();


        assertEquals(cartePrimaExtra + 1, marcosTribe,
                "Marco deve aver preso la carta personaggio extra");

        assertEquals(1, sofiasTribe);

        assertNotEquals(GameState.RESOLVING_ACTIONS, game.getCurrentState().getStateId(),
                "Il round dovrebbe essere terminato");
    }

    @Test
    void testSkipExtraDrawTransitions() {

        sofia.setExtraDraw();
        marco.setExtraDraw();

        game.getBoard().getUpperRow().clear();
        game.getBoard().getUpperRow().add(new BuildingCard(Era.ERA_I, 1, 0, null));

        ResolvingState rs = new ResolvingState();
        rs.execute(game);

        Player primo = rs.getActivePlayer(game);
        rs.skipExtraDraw(game);

        Player secondo = rs.getActivePlayer(game);
        assertNotEquals(primo, secondo);
    }

    @Test
    void notifyBuildingeffect(){

        ResourceBonusEffect huntEffect = new ResourceBonusEffect(
                EventType.HUNT,
                CharacterType.HUNTER,
                ResourceType.FOOD,
                1
        );

        ResourceBonusEffect paintingFoodBonus = new ResourceBonusEffect(
                EventType.PAINTING, CharacterType.ARTIST, ResourceType.FOOD, 1
        );

        marco.getTribe().addBuilding(new BuildingCard(Era.ERA_I, 0, 0, huntEffect));
        sofia.getTribe().addBuilding(new BuildingCard(Era.ERA_I, 0, 0, paintingFoodBonus));

        marco.getTribe().addCharacter(new Hunter(Era.ERA_I, 0, false));
        marco.getTribe().addCharacter(new Hunter(Era.ERA_I, 0, false));

        assertEquals(2, marco.getTribe().getCharacters().size());

        sofia.getTribe().addCharacter(new Artist(Era.ERA_I, 0));
        sofia.getTribe().addCharacter(new Artist(Era.ERA_I, 0));
        sofia.getTribe().addCharacter(new Artist(Era.ERA_I, 0));

        // --- CASO 1: EVENTO CACCIA ---
        int foodPrima = marco.getFood();
        int puntiPrima = marco.getPrestigePoints();

        HuntEvent event = new HuntEvent(Era.ERA_I, 2,false,3);

        event.resolve(game);

        assertEquals(foodPrima + 4, marco.getFood(), "Marco dovrebbe aver ricevuto 4 Cibo sia per evento che per edificio");
        assertEquals(puntiPrima + 2 + (3*2), marco.getPrestigePoints(), "Marco dovrebbe aver ricevuto 8 Punti Prestigio");

        // Sofia parte con 10 Cibo e 10 Punti Prestigio
        sofia.setFood(10);
        sofia.setPrestigePoints(10);

        CavePaintingEvent cevent = new CavePaintingEvent(Era.ERA_I, 2, false, 1, 2, 3, 2);
        cevent.resolve(game);

        assertEquals(3, sofia.getTribe().getCharactersTypeCount(CharacterType.ARTIST));

        assertEquals(13, sofia.getFood(), "Dovrebbe avere 13 cibo");
        assertEquals(16, sofia.getPrestigePoints(), "Dovrebbe avere 16 punti prestigio");

    }


    @Test
    void notifyPlayersBuildingEffect(){

        ResourceBonusEffect effect = new ResourceBonusEffect(
                null,
                null,
                ResourceType.FOOD,
                5
        );

        BuildingCard building = new BuildingCard(Era.ERA_I, 0, 0, effect);

        marco.payFood(marco.getFood());
        assertEquals(0, marco.getFood());


        marco.getTribe().addCharacter(new Hunter(Era.ERA_I, 0, false));
        marco.getTribe().addCharacter(new Artist(Era.ERA_I, 0));
        marco.getTribe().addCharacter(new Inventor(Era.ERA_I, 0, InventionIcon.BOAT));
        marco.getTribe().addCharacter(new Builder(Era.ERA_I, 0, 2, 2));
        marco.getTribe().addCharacter(new Gatherer(Era.ERA_I, 0, 2));

        marco.getTribe().addBuilding(building);

        int foodPrima = marco.getFood();

        marco.getTribe().addCharacter(new Shaman(Era.ERA_I,0,1));

        game.notifyPlayersBuildingEffects(TriggerType.ON_CHARACTER_ADDED, marco);

        assertEquals(foodPrima + 5, marco.getFood(), "Marco dovrebbe aver ottenuto 5 cibo per il set completo");

        // TEST SECONDO SET
        // Se aggiungiamo un altro Hunter, il set non è completo (serve di nuovo uno per tipo)
        marco.getTribe().addCharacter(new Hunter(Era.ERA_I, 0, false));
        game.notifyPlayersBuildingEffects(TriggerType.ON_CHARACTER_ADDED, marco);

        assertEquals(foodPrima + 5, marco.getFood(), "Il cibo NON deve aumentare se il set non è di nuovo bilanciato");
    }

    @Test
    void EventState() {
        Board board = game.getBoard();
        board.getUpperRow().clear();
        board.getLowerRow().clear();

        int pp = marco.getPrestigePoints();
        int food = marco.getFood();

        board.getTurnOrderTrack().setEffectsActive(true);

        board.getTurnOrderTrack().setPlayerAt(0, sofia);
        board.getTurnOrderTrack().setPlayerAt(1, marco);

        // ---  SETUP MARCO ---
        ResourceBonusEffect huntEffect = new ResourceBonusEffect(EventType.HUNT, CharacterType.HUNTER, ResourceType.FOOD, 1);
        marco.getTribe().addBuilding(new BuildingCard(Era.ERA_I, 0, 0, huntEffect));

        marco.getTribe().addCharacter(new Hunter(Era.ERA_I, 2, false));
        marco.getTribe().addCharacter(new Hunter(Era.ERA_I, 2, false));
        assertEquals(2, marco.getTribe().getCharacters().size());

        HuntEvent event = new HuntEvent(Era.ERA_I, 2, false, 3);
        board.getLowerRow().add(event);

        int foodPrima = marco.getFood();
        int puntiPrima = marco.getPrestigePoints();


        // --- SETUP SOFIA ---
        sofia.setFood(10);
        sofia.setPrestigePoints(10);

        // Aggiungiamo l'effetto pittura all'edificio di Sofia
        ResourceBonusEffect paintingFoodBonus = new ResourceBonusEffect(EventType.PAINTING, CharacterType.ARTIST, ResourceType.FOOD, 1);
        sofia.getTribe().addBuilding(new BuildingCard(Era.ERA_I, 0, 0, paintingFoodBonus));

        // Sofia ha 3 artisti
        sofia.getTribe().addCharacter(new Artist(Era.ERA_I, 0));
        sofia.getTribe().addCharacter(new Artist(Era.ERA_I, 0));
        sofia.getTribe().addCharacter(new Artist(Era.ERA_I, 0));

        CavePaintingEvent cevent = new CavePaintingEvent(Era.ERA_I, 2, false, 1, 2, 3, 2);
        board.getLowerRow().add(cevent);

        game.changeState(new EventState());

        //marco ha pagato 1 di cibo perchè si trovava sull'ultima casella della turnordertrack
        //marco dovrebbe prendere 8 punti dato evento caccia + effetto edificio durante l'evento e dovrebbe perdere anche 3 punti causa evento pitture rupestri
        assertEquals(food + 4 - 1, marco.getFood(), "Marco dovrebbe aver ricevuto 4 Cibo");
        assertEquals(puntiPrima + 8 - 3, marco.getPrestigePoints(), "Marco dovrebbe aver ricevuto 8 Punti");


        assertEquals(13, sofia.getFood(), "Dovrebbe avere 13 cibo (10 base + 3 bonus edificio)");
        assertEquals(16, sofia.getPrestigePoints(), "Dovrebbe avere 16 punti prestigio");
    }


    @Test
    void testNonRetroactiveSetBonus() {

        sofia.payFood(sofia.getFood());
        int ciboIniziale = sofia.getFood();

        // 2. COMPLETAMENTO SET 1 (Senza edificio)
        sofia.getTribe().addCharacter(new Hunter(Era.ERA_I, 0, false));
        sofia.getTribe().addCharacter(new Artist(Era.ERA_I, 0));
        sofia.getTribe().addCharacter(new Inventor(Era.ERA_I, 0, InventionIcon.BOAT));
        sofia.getTribe().addCharacter(new Builder(Era.ERA_I, 0, 2, 2));
        sofia.getTribe().addCharacter(new Gatherer(Era.ERA_I, 0, 2));
        sofia.getTribe().addCharacter(new Shaman(Era.ERA_I, 0, 1));

        assertEquals(6, sofia.getTribe().getCharacters().size());

        ResourceBonusEffect setEffect = new ResourceBonusEffect(null, null, ResourceType.FOOD, 5);
        sofia.getTribe().addBuilding(new BuildingCard(Era.ERA_I, 0, 0, setEffect));

        assertEquals(ciboIniziale, sofia.getFood());

        // Aggiungiamo di nuovo i 6 tipi
        sofia.getTribe().addCharacter(new Hunter(Era.ERA_I, 0, false));

        game.notifyPlayersBuildingEffects(TriggerType.ON_CHARACTER_ADDED, sofia);
        assertEquals(ciboIniziale, sofia.getFood());

        sofia.getTribe().addCharacter(new Artist(Era.ERA_I, 0));
        sofia.getTribe().addCharacter(new Inventor(Era.ERA_I, 0, InventionIcon.BOWL));
        sofia.getTribe().addCharacter(new Builder(Era.ERA_I, 0, 1, 1));
        sofia.getTribe().addCharacter(new Gatherer(Era.ERA_I, 0, 1));

        // Al 5 personaggio del secondo set (11 totali), ancora niente bonus
        game.notifyPlayersBuildingEffects(TriggerType.ON_CHARACTER_ADDED, sofia);
        assertEquals(ciboIniziale, sofia.getFood());

        // Aggiungiamo il 12 (il 2 shaman) che chiude il secondo set
        sofia.getTribe().addCharacter(new Shaman(Era.ERA_I, 0, 1));
        game.notifyPlayersBuildingEffects(TriggerType.ON_CHARACTER_ADDED, sofia);

        // VERIFICA FINALE: Ora deve aver attivato il bonus (+5)
        assertEquals(ciboIniziale + 5, sofia.getFood(), "Deve dare 5 cibo per il nuovo set completato");
    }


    @Test
    void skipWhenNotCardsAffordable() {
        Board board = game.getBoard();
        board.getLowerRow().clear();
        board.getUpperRow().clear();

        // 1. SETUP BOARD: Solo una carta costo 5
        BuildingCard card = new BuildingCard(Era.ERA_I, 5, 0, null);
        board.getLowerRow().add(card);

        marco.setFood(0);

        // Mettiamo Marco sulla tessera B
        OfferTile tileB = board.getTile('E');
        tileB.setHost(marco);

        // 3. SETUP SOFIA: Lei è sulla tessera C
        OfferTile tileC = board.getTile('F');
        tileC.setHost(sofia);

        game.changeState(new ResolvingState());

        // --- VERIFICHE ---

        // L'indice della tessera deve essere avanzato oltre la B
        // Il giocatore attivo ora deve essere SOFIA

        assertTrue(game.getCurrentState() instanceof PlacingState,
                "Il gioco dovrebbe aver saltato tutto ed essere tornato alla fase di piazzamento del Round 2");

        // Verifichiamo che Marco e Sofia non abbiano effettivamente preso carte
        assertEquals(0, marco.getTribe().getBuildingsCount() + marco.getTribe().getCharactersCount(),
                "Marco non dovrebbe aver preso nulla perché è stato saltato");
        assertEquals(0, sofia.getTribe().getBuildingsCount() + sofia.getTribe().getCharactersCount(),
                "Sofia non dovrebbe aver preso nulla perché è stata saltata");
    }


    @Test
    void gameFinished(){

        game.setCurrentRound(10);

        // Verifichiamo di essere effettivamente all'ultimo round
        assertEquals(10, game.getCurrentRound(), "Il gioco dovrebbe essere al round 10");

        // 2. SETUP BOARD FINALE
        Board board = game.getBoard();
        board.getUpperRow().clear();
        board.getLowerRow().clear();

        // Aggiungiamo gli eventi richiesti
        // Evento Sciamanico (punti in base agli edifici/spiriti)
        ShamanicRitualEvent shamanic = new ShamanicRitualEvent(Era.ERA_III, 2, true, 0, 0);
        // Evento Sostentamento (deve essere risolto per ULTIMO)
        SustenanceEvent sustenance = new SustenanceEvent(Era.ERA_III, 2, true, 0);

        board.getUpperRow().add(shamanic);
        board.getLowerRow().add(sustenance);

        EventState eventState = new EventState();
        eventState.execute(game);

        assertTrue(game.getCurrentState() instanceof FinishedState,
                "Al round 10, dopo gli eventi, il gioco deve passare a FinishedState");

    }

}