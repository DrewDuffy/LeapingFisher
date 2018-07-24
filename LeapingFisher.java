package BarbOutpost;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.Script;
import org.rspeer.script.ScriptMeta;
import org.rspeer.ui.Log;


@ScriptMeta(developer = "werd", desc = "Barbarian Outpost fishing bot. Have feathers and barbarian rod in inventory.", name = "Leaping Fisher")
public class BarbarianFisher extends Script {

    public static final Position BARB_POS = new Position(2498,3507);

    // Group together all the fish in an array
    public static final int[] FISH = {11330, 11328, 11332};
    public static String status = "";
    @Override
    public int loop() {
        if(!Movement.isRunEnabled()){
            if (Movement.getRunEnergy() > 20){
                Movement.toggleRun(true);
            }
        }
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
        //Continues level-up message
        if(Interfaces.canContinue()){
            Interfaces.processContinue();
        }

        //if inventory is full, drop all the fish
        if(Inventory.isFull()){
            while(Inventory.contains(FISH)){
                Inventory.getFirst(FISH).interact("Drop");
                Time.sleep(100);
                status = "Dropping";
                Log.info(status);
            }
        }
        return 1000;
    }
}