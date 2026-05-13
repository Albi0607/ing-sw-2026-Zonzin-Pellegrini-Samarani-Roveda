package it.polimi.ingsw.mesos.DB;

public class DBTest {
    public static void main(String[] args) throws Exception {
        String username = "root" ;
        String pw = "1234" ;
        DBManager.init(username,pw); // inizializza DB e crea tabella

        GameResultDAO dao = new GameResultDAO(); // costruttore corretto
        LeaderboardService service = new LeaderboardService(dao);

        System.out.println("=== RESET TABELLA ===");
        dao.clearAll();
        System.out.println("Tabella pulita.\n");

        System.out.println("=== INSERIMENTO DATI ===");
        service.addResult("Mario", 100, 3);
        service.addResult("Luigi", 80, 3);
        service.addResult("Peach", 120, 3);
        System.out.println("Dati inseriti.\n");

        System.out.println("=== LETTURA CLASSIFICA ===");
        var leaderboard = service.getLeaderboard(3);
        for (int i = 0; i < leaderboard.size(); i++) {
            var r = leaderboard.get(i);
            System.out.println((i+1) + ") " + r.getNickname() + " - " + r.getPoints());
        }

        System.out.println("\n=== TEST POSIZIONE ===");
        int posMario = service.getPosition("Mario", 3);
        int posPeach = service.getPosition("Peach", 3);
        int posLuigi = service.getPosition("Luigi", 3);

        System.out.println("Mario → posizione: " + posMario);
        System.out.println("Peach → posizione: " + posPeach);
        System.out.println("Luigi → posizione: " + posLuigi);

        System.out.println("\n=== TEST COMPLETATO ===");
    }
}
