package it.polimi.ingsw.mesos.model;
import it.polimi.ingsw.mesos.model.board.Board;
import it.polimi.ingsw.mesos.model.board.OfferTile;
import it.polimi.ingsw.mesos.model.board.TurnOrderTrack;
import it.polimi.ingsw.mesos.model.card.building.BuildingCard;
import it.polimi.ingsw.mesos.model.card.building.BuildingEffect;
import it.polimi.ingsw.mesos.model.card.building.ResourceBonusEffect;
import it.polimi.ingsw.mesos.model.card.character.*;
import it.polimi.ingsw.mesos.model.card.event.CavePaintingEvent;
import it.polimi.ingsw.mesos.model.card.event.EventCard;
import it.polimi.ingsw.mesos.model.card.event.HuntEvent;
import it.polimi.ingsw.mesos.model.deck.*;
import it.polimi.ingsw.mesos.model.enums.*;
import it.polimi.ingsw.mesos.model.state.EventState;
import it.polimi.ingsw.mesos.model.state.PlacingState;
import it.polimi.ingsw.mesos.model.state.ResolvingState;
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

        int personaggiInizialiMarco = marco.getTribe().getCharactersCount();

        // Marco deve prendere una carta dalla fila INFERIORE perché è sulla tessera B
        // Prendiamo la prima carta disponibile (indice 0)
        rs.takeCard(game, 0, false);

        // VERIFICHE:
        assertEquals(personaggiInizialiMarco + 1, marco.getTribe().getCharactersCount(),
                "La tribù di Marco dovrebbe essere cresciuta di 1");

        System.out.println("Marco ha preso la carta correttamente. Tribù attuale: " + marco.getTribe().getCharactersCount());

        // Ora il turno dovrebbe passare a Sofia (Tessera C)
        Player prossimoGiocatore = rs.getActivePlayer(game);
        assertEquals(sofia.getNickname(), prossimoGiocatore.getNickname(),
                "Dopo Marco, deve toccare a Sofia");


        int personaggiInizialiSofia = sofia.getTribe().getCharactersCount();

        // Sofia deve prendere una carta dalla fila superiore perché è sulla tessera C
        // Prendiamo la prima carta disponibile (indice 0)
        rs.takeCard(game, 3, true);

        // VERIFICHE:
        assertEquals(personaggiInizialiSofia + 1, sofia.getTribe().getCharactersCount(),
                "La tribù di Marco dovrebbe essere cresciuta di 1");

        System.out.println("Sofia ha preso la carta correttamente. Tribù attuale: " + sofia.getTribe().getCharactersCount());

        // 1. Recuperiamo l'ultima carta aggiunta alla tribù di Marco
        TribeCard cartaPresa = marco.getTribe().getCharacters().get(marco.getTribe().getCharacters().size() - 1);

        /*

        CharacterCard hunter = cartaPresa.getAsCharacterCard();
        assertNotNull(hunter, "La carta presa dovrebbe essere un personaggio");
        assertEquals(CharacterType.HUNTER, hunter.getCharacterType(), "Marco doveva prendere un Hunter");

        // 3. Verifichiamo la logica dell'icona e del cibo
        int ciboPrimaDellaPresa = 2; // Valore iniziale di Marco come 1° giocatore
        boolean haIcona = ((Hunter) hunter).hasIcon(); // Cast a Hunter per vedere l'icona

        if (haIcona) {
            System.out.println("🏹 L'Hunter ha l'icona! Verifico il bonus cibo...");
            // Se la tua logica prevede +1 cibo immediato:
            // assertEquals(ciboPrimaDellaPresa + 1, marco.getFood(), "Marco dovrebbe avere 1 cibo in più grazie all'icona");
        } else {
            System.out.println("👤 L'Hunter non ha icone. Il cibo non deve cambiare.");
            assertEquals(ciboPrimaDellaPresa, marco.getFood(), "Il cibo non dovrebbe essere cambiato");
        }
        */

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


        // A. L'Era del gioco deve essere cambiata
        assertEquals(Era.ERA_II, game.getCurrentEra(), "Il gioco dovrebbe essere passato all'Era II");

        // B. Gli edifici dell'Era I devono essere "scesi" nella fila inferiore
        boolean edificiInLower = game.getBoard().getLowerRow().stream()
                .anyMatch(c -> c instanceof BuildingCard && c.getEra() == Era.ERA_I);
        assertTrue(edificiInLower, "Gli edifici dell'Era I dovrebbero essere stati spostati nella fila inferiore");

        // C. La fila superiore deve ora contenere gli edifici dell'Era II
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



        System.out.println("edifici era I " + era1Buildings);
        System.out.println("edifici era II " + era2Buildings);


        System.out.println("Transizione Era I -> Era II verificata con successo!");
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




}