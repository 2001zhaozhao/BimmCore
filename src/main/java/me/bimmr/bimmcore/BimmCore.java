package me.bimmr.bimmcore;

import me.bimmr.bimmcore.events.timing.TimedEvent;
import me.bimmr.bimmcore.messages.ActionBar;
import me.bimmr.bimmcore.messages.BossBar;
import me.bimmr.bimmcore.messages.MessageDisplay;
import me.bimmr.bimmcore.messages.Title;
import me.bimmr.bimmcore.reflection.Reflection;
import me.bimmr.bimmcore.scoreboard.Board;
import me.bimmr.bimmcore.scoreboard.BoardLine;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.logging.Level;

/**
 * Created by Randy on 12/25/2015.
 */
public class BimmCore extends JavaPlugin {

    private static Plugin instance;

    public static Plugin getInstance() {
        return instance;
    }

    public static boolean checkBimmLibVersion(final Plugin plugin, int versionNeeded) {

        int mcVersion = Integer.valueOf(instance.getDescription().getVersion().replaceAll("\\.", "").substring(0, 3));

        if (mcVersion < versionNeeded) {
            Bukkit.getLogger().log(Level.SEVERE, plugin.getName() + " requires a newer BimmLib version.");
            new BukkitRunnable() {
                public void run() {
                    Bukkit.getPluginManager().disablePlugin(plugin);
                }
            }.runTaskLater(instance, 1);
        }
        return (mcVersion >= versionNeeded);

    }

    /**
     * Get the online players returning an array
     * Safe for versions below 1.7.10
     *
     * @return
     */
    public static Player[] getOnlinePlayers() {
        // Try the 1.7.10+ Method of getting players
        try {
            Collection<? extends Player> p = Bukkit.getOnlinePlayers();
            return p.toArray(new Player[p.size()]);
        } catch (NoSuchMethodError e) {
            // Try older way using reflection(This way it works for all other
            // versions)
            try {
                Player[] players = (Player[]) Reflection.getMethod(Bukkit.class, "getOnlinePlayers").invoke(null);
                return players;
            } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public void onEnable() {
        instance = this;

        /*
          Create all the Scrollers
         */
        String message = "&3Welcome &eTo &6My &4Test Server!";
        final StringUtil.Scroller scroller = new StringUtil.Scroller(message, 15, 5);
        final StringUtil.Scroller scroller2 = new StringUtil.Scroller(message, 10, 5);
        final StringUtil.Scroller scroller3 = new StringUtil.Scroller(message, 10, 5);

        /*
         Create the TimedEvent that will run all of the MessageDisplays
         */
        TimedEvent timedEvent = new TimedEvent(3) {
            @Override
            public void run() {
                MessageDisplay bar = (MessageDisplay) this.getAttachedObject();
                bar.setText(scroller.next());
            }
        };
        /*
        Create the TimedEvent that will run the scoreboard
         */
        TimedEvent timedEvent3 = new TimedEvent(1) {
            int i = 0;

            @Override
            public void run() {
                Board board = (Board) getAttachedObject();
                board.setTitle(scroller3.next());
                if (i % 2 == 0)
                    board.setText(0, scroller2.next());
                if (i % 5 == 0)
                    board.setText(9, scroller2.current());
                board.getBoardLine(11).setValue(board.getBoardLine(11).getValue() + 1);

                if (i == Integer.MAX_VALUE)
                    i = 0;
                i++;
            }
        };

        /*
         Create the Board
         */
        Board board = new Board("Test", timedEvent3);
        board.add(new BoardLine(scroller2.next()));
        board.add(new BoardLine("Test2          "));
        board.add(new BoardLine("Test3"));
        board.add(new BoardLine("Test4"));
        board.add(new BoardLine("Test5"));
        board.add(new BoardLine("Test6"));
        board.add(new BoardLine("Test7"));
        board.add(new BoardLine("Test8"));
        board.add(new BoardLine("Test9"));
        board.add(new BoardLine("Test10"));
        board.add(new BoardLine("Test12"));
        board.add(new BoardLine("Test13"));
        board.add(new BoardLine("Test14"));

        //Start the Board's timedTask(Not active by default to prevent un-needed lag
        board.startTask();


        /*
        Create all the MessageDisplays
         */
        Title title = new Title("Test", "", 0, 30, timedEvent);
        ActionBar actionBar = new ActionBar("Test", 30, timedEvent.clone());
        BossBar bossBar = new BossBar("Test", 30, timedEvent.clone());

        /*
        Send it to all players online
         */
        for (Player player : Bukkit.getOnlinePlayers()) {
            title.send(player);
            actionBar.send(player);
            bossBar.send(player);
            board.addPlayer(player);

        }

    }
}
