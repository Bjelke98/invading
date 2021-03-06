/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *                                                                                                         *
 * Spill navn: Invading Space                                                                              *
 *                                                                                                         *
 * Fil: Main.java                                                                                          *
 * Utvider: Application (JavaFX)                                                                           *
 * Anbefalt og eneste supporterte oppløning: 1920x1080                                                     *
 *                                                                                                         *
 * Java Ver: JDK 14.0.2                                                                                    *
 * JavaFX Ver: SDK 15.0.1                                                                                  *
 *                                                                                                         *
 * Filer:                                                                                                  *
 * * Nødvendig:                                                                                            *
 * * * Main.java                                                                                           *
 * * * explo.gif (Åpen lisens for bruk. Ingen behov for opphavsreferanse)                                  *
 * * * sprite1.png (Laget av meg selv (Krister Iversen))                                                   *
 * * * sprite3.png (Laget av meg selv (Krister Iversen))                                                   *
 * * Valgfri:                                                                                              *
 * * * lb5.txt (Tekstfil som lagrer leaderboard (Genereres automatisk nytt om det ikke er til stede.))     *
 *                                                                                                         *
 * Skrevet av: Krister Iversen                                                                             *
 * Dato: 05.03.2021-06.03.2021                                                                             *
 * Stilling: Student                                                                                       *
 * Studieplass: USN Bø                                                                                     *
 * Linje: IT og Informasjonssystemer (bachelorgrad)                                                        *
 * Trinn: 1.år                                                                                             *
 *                                                                                                         *
 * Kilder:                                                                                                 *
 * * http://tutorials.jenkov.com/javafx/index.html Sider: 1-10, 40, 65, 68, 69, 71                         *
 * * https://docs.oracle.com/javafx/2/api/index.html                                                       *
 * * https://www.w3schools.com/java/java_arraylist.asp                                                     *
 * * https://www.geeksforgeeks.org/find-two-rectangles-overlap/                                            *
 *                                                                                                         *
 * Inspirasjon:                                                                                            *
 * * 80-talls arkade verdensrom spill                                                                      *
 *                                                                                                         *
 * Programfeil:                                                                                            *
 * * Ugjevn "hitbox" mot hindring                                                                          *
 * * Laserstråle ved skudd fungerer bare av og til (Skuddene er der, men syntes ikke)                      *
 *                                                                                                         *
 * Om du lurte!                                                                                            *
 * * Hvorfor bruker jeg "Inner Classes / Nested Classes"?                                                  *
 * *    Jeg er ikke så rutta på OOP så det gjorde det enklere å få tilgang til klassevariabler.            *
 * * Hva brukte jeg mest tid på?                                                                           *
 * *    Finne ut av om 2 rektangler overlappet                                                             *
 *                                                                                                         *
 * Spill instrukser:                                                                                       *
 * * Bruk piltastene til å kontrollere karakteren                                                          *
 * * Trykk space for å skyte                                                                               *
 * * Runden er over når karakteren din har eksplodert og du blir vist din poengsum                         *
 *                                                                                                         *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package v1;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.*;

public class Main extends Application {
    private final int P_SPEED = 10;
    private final int ROCK_SPEED = 5;
    private final int ENEMY_SPEED = 7;
    private int screenX, screenY, ph, pw, points, count, count2, shotAnim;
    private boolean canShoot = true;
    private boolean coll = false;
    private boolean shotFire = false;
    private ImageView iv, explo;
    private ArrayList<ImageView> enemys;
    private ArrayList<Rectangle> rz;
    private Color primaryColor, rock;
    private Group root;
    private Rectangle shot;
    private Runner game;
    private Move move1, move2, move3, move4;
    private Text t1, t2, t3, livePoints;

    @Override
    public void start(Stage stage) throws Exception{

        stage.setTitle("Animation Test");

        // Tellere
        count = -400;
        count2 = -250;
        points = 0;
        shotAnim = 0;

        // Bilder
        Image s1 = new Image(new FileInputStream("sprite1.png"));
        Image s2 = new Image(new FileInputStream("explo.gif"));
        Image s3 = new Image(new FileInputStream("sprite3.png"));

        // Spiller
        iv = new ImageView(s1);
        iv.setLayoutX(0);
        iv.setLayoutY(0);
        iv.setRotate(90);
        ph = (int)iv.getImage().getHeight();
        pw = (int)iv.getImage().getWidth();

        // Tekst
        t1 = new Text();
        t1.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        t1.setFill(Color.BLUEVIOLET);
        t2 = new Text();
        t2.setFont(Font.font("Arial", FontWeight.BOLD, 45));
        t2.setFill(Color.AQUA);
        t3 = new Text("Trykk på F4 eller TAB tasten for å avslutte");
        t3.setFont(Font.font("Arial", FontWeight.BOLD, 70));
        t3.setFill(Color.WHITE);
        livePoints = new Text();
        livePoints.setFont(Font.font("Arial", FontWeight.NORMAL, 50));
        livePoints.setFill(Color.WHITE);

        // End state indikator
        explo = new ImageView(s2);

        // Lister
        root = new Group(); // Alle synelige elementer
        enemys = new ArrayList<>(); // Fiender
        rz = new ArrayList<>(); // Hindringer

        // Skjerminstillinger
        Rectangle2D psb = Screen.getPrimary().getBounds();
        screenX = (int) psb.getWidth();
        screenY = (int) psb.getHeight();

        // Starter spill
        game = new Runner();
        game.start();

        // Farger
        primaryColor = new Color(1, 0, 1, 0.5);
        rock = new Color(1, 0.2, 0.3, 1);

        // Hindringer
        for(int i = 0; i<6; i++){ // Lager hindringer
            rz.add(new Rectangle(0, 0, 150, 0));
        }
        rz.forEach((v)->{ // Gjør hindringer klare
            v.setLayoutX(screenX+aagC());
            v.setFill(rock);
            root.getChildren().add(v);
        });

        // Fiender
        for(int i = 0; i<3; i++){ // Lager fiender
            enemys.add(new ImageView(s3));
        }
        enemys.forEach((v)->{ // Gjør fiender klare
            v.setLayoutX(screenX+aagE());
            v.setRotate(270);
            root.getChildren().add(v);
        });

        root.getChildren().add(iv); // Legger til spiller

        shot = new Rectangle(0, 0, screenX, 10); // Klargjør skudd
        shot.setLayoutX(screenX*2); // --||--
        shot.setLayoutY(screenY*2); // --||--
        shot.setFill(primaryColor); // --||--
        root.getChildren().add(shot); // --||--

        // Live points
        root.getChildren().add(livePoints);
        livePoints.setLayoutX(50);
        livePoints.setLayoutY(50);
        livePoints.setText("0");

        // Setter opp scene og legger til alle elementer
        Scene scene = new Scene(root, screenX, screenY, Color.rgb(0, 0, 0));

        // Setter opp 4 bevegelses retninger
        move1 = new Move();
        move2 = new Move();
        move3 = new Move();
        move4 = new Move();
        move1.setDir(1);
        move2.setDir(2);
        move3.setDir(3);
        move4.setDir(4);

        // Lytter etter tast ned
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP -> move1.start();
                case RIGHT -> move2.start();
                case DOWN -> move3.start();
                case LEFT -> move4.start();
                case SPACE -> shoot();
                case F4, TAB -> stage.close();
            }
        });

        // Lytter etter tast opp
        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case UP -> move1.stop();
                case RIGHT -> move2.stop();
                case DOWN -> move3.stop();
                case LEFT -> move4.stop();
            }
        });

        stage.setScene(scene);
        stage.setFullScreen(true); // Setter fullskjerm
        stage.show(); // Viser vindu

    }

    // Increment teller for hindring
    public int aagC(){
        count+=400;
        return count;
    }

    // Increment teller for fiende
    public int aagE(){
        count2+=250;
        return count2;
    }

    // Skyter om skudd er tilgjengelig
    private void shoot(){
        if(canShoot){
            canShoot=false;
            shotFire=true;
            //System.out.println("BAM!");
        }

        // Registrerer om skudd treffer
        enemys.forEach((v)->{
            if(v.getLayoutX()>iv.getLayoutX() &&
               iv.getLayoutY()+ph/2+30>v.getLayoutY()+75/2 &&
               iv.getLayoutY()+ph/2-30<v.getLayoutY()+75/2){
                points+=10; // Om skudd treffer fiende får spiller 10 ekstra poeng
                v.setLayoutX(screenX);
                v.setLayoutY(rand(200, screenY-300));
                //System.out.println("Hit");
            }
        });
    }

    // new Circle(50, 75, 50); // Disse utkommenterte sirklene demonstrerer...
    // new Circle(110, 75, 30); // ... kollisjonsmønsteret til spilleren

    // Kollisjonslogikk
    private void hit(){
        livePoints.setText("Score: "+ points);
        int sr1 = 50;
        int sr2 = 30;
        int sr3 = 37;
        int sr4 = 75;
        enemys.forEach((v)->{
            if(range( // Hitbox a
                    (int)v.getLayoutX()+sr3,
                    (int)v.getLayoutY()+sr3,
                    (int)iv.getLayoutX()+sr1,
                    (int)iv.getLayoutY()+sr4
            )<sr1+sr3 || range( // Hitbox b
                    (int)v.getLayoutX()+sr3,
                    (int)v.getLayoutY()+sr3,
                    (int)iv.getLayoutX()+sr2,
                    (int)iv.getLayoutY()+sr4
            )<sr2+sr3){
                coll = true;
            }
        });
        rz.forEach((v)->{
            double ax = iv.getLayoutX()-10;
            double ay = iv.getLayoutY()-10;
            int awh = 150;
            double bx = v.getLayoutX()+10;
            double by = v.getLayoutY()+10;
            int bw = 150;
            double bh = v.getHeight();
            if(ax < bx + bw && ax + awh > bx && ay < by + bh && ay + awh > by){
                coll = true;
            }
        });
        if(coll){ // Game end state sekvens
            root.getChildren().add(explo);
            explo.setLayoutX(iv.getLayoutX());
            explo.setLayoutY(iv.getLayoutY());
            game.stop();
            move1.setDir(0);
            move2.setDir(0);
            move3.setDir(0);
            move4.setDir(0);
            root.getChildren().add(t1);
            root.getChildren().add(t2);
            root.getChildren().add(t3);
            t1.setLayoutX(screenX/2-200);
            t1.setLayoutY(screenY/2);
            t2.setLayoutX(screenX/2-190);
            t2.setLayoutY(screenY/2);
            t3.setLayoutX(250);
            t3.setLayoutY(screenY/2+400);
            t1.setText("Game over! \nDin score: "+points);
            t2.setText("Game over! \nDin score: "+points);
        }
    }

    @Override
    public void stop(){
        save();
    }

    // Pytagoras rekkevidde regning punk (Rot av (x2-x1)^2 + (y2-y1)^2)
    private int range(int x1, int y1, int x2, int y2 ){
        return (int)Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
    }

    // Spill timer, kjører så lenge spilleren er i live.
    private class Runner extends AnimationTimer {
        long prev1 = 0;
        long prev2 = 0;
        long prev3 = 0;
        @Override
        public void handle(long l) {
            long change1 = l-prev1;
            long change2 = l-prev2;
            long change3 = l-prev3;
            if(change1>15e6){ // En gang per 15 millisekund
                prev1 = l;
                if(shotFire){ // Skudd avfyrt?
                    shotAnim = 5;
                    shotFire = false;
                }
                if(shotAnim>0){
                    shotAnim--;
                    shot.setLayoutY(iv.getLayoutY()+70); // Viser skudd
                    shot.setLayoutX(iv.getLayoutX()+75); // --||--
                } else {
                    shot.setLayoutY(screenY*2); // Sjuler skudd
                    shot.setLayoutX(screenX*2); // --||--
                }
                moveNShow(); // Flytter dødelige objekter
                hit(); // Ser etter kollisjoner
            }
            if(change2>1e9){ // En gang per sekund
                prev2 = l;
                points++; // Legger til 1 score
            }
            if(change3>3e9){ // Klargjør neste skudd (har ventet 3 sekunder siden forrige skudd)
                prev3 = l;
                canShoot = true;
            }
        }
    }

    private void moveNShow(){ // Flytter dødelige gjenstander
        int rh = rand(200, 400); // Genererer tilfeldige tall for å styre størrelse og plassering
        int ry = rand(0, screenY-400); // --||--
        int ey = rand(200, screenY-300); // --||--
        rz.forEach((v)->{
            v.setLayoutX(v.getLayoutX()-ROCK_SPEED);
            if(v.getLayoutX()<-200){
                v.setLayoutX(screenX);
                v.setHeight(rh);
                v.setLayoutY(ry);
            }
        });
        enemys.forEach((v)->{
            v.setLayoutX(v.getLayoutX()-ENEMY_SPEED);
            if(v.getLayoutX()<-75){
                v.setLayoutX(screenX);
                v.setLayoutY(ey);
            }
        });
    }

    private class Move extends AnimationTimer { // Styrer alle spiller bevegelser
        private long prev = 0;
        private int s1;
        private int s2;
        private int s3;
        private int s4;
        @Override
        public void handle(long l) {
            long change = l-prev;
            if(change>15e6){ // Hvert 1ms (1000 i 1 sekund) Flytter spiller i "d" retning.
                prev = l;
                if(iv.getLayoutY()>0){iv.setLayoutY(iv.getLayoutY() - s1);}
                if(iv.getLayoutX()<screenX-pw){iv.setLayoutX(iv.getLayoutX() + s2);}
                if(iv.getLayoutY()<screenY-ph){iv.setLayoutY(iv.getLayoutY() + s3);}
                if(iv.getLayoutX()>0){iv.setLayoutX(iv.getLayoutX() - s4);}
            }
        }
        public void setDir(int d){ // Velger retning
            s1 = 0;
            s2 = 0;
            s3 = 0;
            s4 = 0;
            switch (d) {
                case 0 -> System.out.println("Game over " + points);
                case 1 -> s1 = P_SPEED;
                case 2 -> s2 = P_SPEED;
                case 3 -> s3 = P_SPEED;
                case 4 -> s4 = P_SPEED/2; // Halv fart bakover
            }
        }
    }

    // Oppdaterer leaderboard
    public void save(){
        try {
            File lb = new File("lb5.txt");
            String navn = JOptionPane.showInputDialog("Skriv navn. (La være tom om du ikke ønsker å være på leaderboard-et)");
            ArrayList<Gamer> lbl = new ArrayList<>(); // Gamer liste over alle de beste
            if (lb.createNewFile()) { // Lager ny fil om den ikke allerede eksisterer
                System.out.println("Leaderboard har blitt opprettet");
                PrintWriter pr1 = new PrintWriter("lb5.txt");
                pr1.println("1; First"); // Skriv inn føste data for å unngå enkelte null merker
                pr1.close();
            }

            // Leser leaderboard dokument
            Scanner s = new Scanner(lb);
            while (s.hasNextLine()) {
                String[] str = s.nextLine().split(";");
                if(str.length==2){
                    lbl.add(new Gamer(Integer.parseInt(str[0]), str[1]));
                }
            }
            lbl.add(new Gamer(points, " "+navn));

            // Fjerner ugyldig navn
            for (int i = 0; i < lbl.size(); i++) {
                if(lbl.get(i).navn.equals(" ")){
                    lbl.remove(i);
                }
            }

            // Sorterer leaderboard listen
            Collections.sort(lbl);
            PrintWriter print = new PrintWriter("lb5.txt");
            lbl.forEach((v)->{
                print.println(v.toCSV());
            });
            s.close();
            print.close();
            StringBuilder sb = new StringBuilder();
            sb.append("Score - Navn\n");
            lbl.forEach((v)->{
                sb.append(v.toString()).append("\n");
            });
            // Sender en ferdig sortert leaderboard liste ut til bruker
            JOptionPane.showMessageDialog(null, sb.toString());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Genererer "NESTEN", men tilfeldige "NOK" tall
    private static int rand(int min, int max) {
        return(int)(Math.random()*(max-min))+min;
    }
    // Class for å ta vare på leaderboard data  når den blir manipulert
    private static class Gamer implements Comparable<Gamer> {
        int score;
        String navn;
        Gamer(int score, String navn){
            this.score = score;
            this.navn = navn;
        }

        @Override
        public int compareTo(Gamer o) {
            return this.score<o.score?1:-1;
        }

        public String toCSV() {
            return score+";"+navn;
        }

        @Override
        public String toString() {
            return score+" -"+navn;
        }


    }

}


