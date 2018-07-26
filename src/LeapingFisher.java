package BarbOutpost;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.script.Script;
import org.rspeer.script.ScriptMeta;
import org.rspeer.ui.Log;

import java.awt.*;


@ScriptMeta(developer = "werd", desc = "Barbarian Outpost fishing bot. Have feathers and barbarian rod in inventory.", name = "Leaping Fisher")
public class LeapingFisher extends Script implements RenderListener {
    //GUI
    private final Color color1 = new Color(209, 193, 159);
    private final Color color2 = new Color(0, 0, 0);
    private final Font font1 = new Font("Segoe UI Historic", 0, 11);
    private final Font font2 = new Font("Segoe UI Historic", 1, 11);
    private final Font font3 = new Font("Segoe UI Emoji", 0, 12);

    //The Barbarian Outpost fishing spot
    public static final Position BARB_POS = new Position(2498,3507);
    //get start time
    final long startTime = System.nanoTime();

    // Group together all the fish in an array
    public static final int[] FISH = {11330, 11328, 11332};
    public static String status;
    public static int fishing = Skills.getCurrentLevel(Skill.FISHING);
    public static int agility = Skills.getCurrentLevel(Skill.AGILITY);
    public static int strength = Skills.getCurrentLevel(Skill.STRENGTH);
    public static final int startFish = Skills.getCurrentLevel(Skill.FISHING);
    public static final int startAgil = Skills.getCurrentLevel(Skill.AGILITY);
    public static final int startStr = Skills.getCurrentLevel(Skill.STRENGTH);
    public static final int fishXp = Skills.getExperience(Skill.FISHING);
    public static final int strXp = Skills.getExperience(Skill.STRENGTH);
    public static final int agilXp = Skills.getExperience(Skill.AGILITY);



    @Override
    public int loop() {
        // what is repeated when the script is running

        if(!Movement.isRunEnabled()){
            if (Movement.getRunEnergy() > 20){
                Movement.toggleRun(true);
            }
        }
        //Continues level-up message
        if(Interfaces.canContinue()){
            Interfaces.processContinue();
        }

        //if inventory is full, drop all the fish
        if(Inventory.isFull()){
                while (Inventory.contains(FISH)){
                    Inventory.getFirst(FISH).interact("Drop");
                    Time.sleep(100);
                    status = "Dropping";
                    Log.info(status);
            }
        }
        if(Inventory.contains("Feather") || Inventory.contains("Fishing bait") && Inventory.contains("Barbarian rod")) {
            //If the player is too far, walk to the fishing spot
            if(BARB_POS.distance() > 20){
                Movement.walkTo(BARB_POS);
                status = "Walking to fishing spot";
                Log.info(status);
            }else{
                if(!Movement.isDestinationSet()){
                    //if player is not fishing, then fish at nearest spot
                    if (Players.getLocal().getTargetIndex() == -1) {
                        Npcs.getNearest(1542).interact(npc -> true);
                    }
                    status = "Fishing";
                    Log.info(status);
                }
            }

        } else{
            Log.fine("You must have Barbarian rod and Feathers/bait in inventory.. Script stopping");
            setStopping(true);
        }
        return 1000;
    }

    public void notify(RenderEvent renderEvent) {
        Graphics g = renderEvent.getSource();
        long milliseconds = (System.nanoTime() - startTime) / (1000 * 1000);
        long seconds = (milliseconds / 1000) % 60;
        long minutes = (milliseconds / (1000*60) % 60);
        long hours = (milliseconds / (1000 * 60 * 60)) % 24;

        g.setColor(color1);
        g.fillRect(5, 340, 500, 135);
        g.setColor(color2);
        g.drawRect(5, 340, 500, 135);
        g.setFont(font1);
        g.drawString("Time Running: " + String.format("%02d:%02d:%02d", hours, minutes, seconds) , 75, 380);
        g.setFont(font2);
        g.drawString("Leaping Fisher", 200, 355);
        g.setFont(font3);
        g.drawString("Status: " + status, 300, 380);
        g.drawString("Fishing Level: " + Skills.getCurrentLevel(Skill.FISHING) + "(" +(Skills.getCurrentLevel(Skill.FISHING) - startFish) + ")", 75, 405);
        g.drawString("Strength Level: " + Skills.getCurrentLevel(Skill.STRENGTH) + "(" +(Skills.getCurrentLevel(Skill.STRENGTH) - startStr) + ")", 75, 430);
        g.drawString("Agility Level: " + Skills.getCurrentLevel(Skill.AGILITY) + "(" +(Skills.getCurrentLevel(Skill.AGILITY) - startAgil) + ")", 75, 455);
        g.drawString((Skills.getExperience(Skill.FISHING) - fishXp) + " Fishing experience gained", 300, 405);
        g.drawString((Skills.getExperience(Skill.STRENGTH) - strXp) + " Strength experience gained", 300, 430);
        g.drawString((Skills.getExperience(Skill.AGILITY) - agilXp) + " Agility experience gained", 300, 455);

    }

}