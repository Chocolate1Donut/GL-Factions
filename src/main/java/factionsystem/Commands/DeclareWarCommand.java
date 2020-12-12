package factionsystem.Commands;

import factionsystem.MedievalFactions;
import factionsystem.Objects.Faction;
import factionsystem.Data.PersistentData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static factionsystem.Subsystems.UtilitySubsystem.createStringFromFirstArgOnwards;
import static factionsystem.Subsystems.UtilitySubsystem.invokeAlliances;

public class DeclareWarCommand {

    public void declareWar(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (sender.hasPermission("mf.declarewar") || sender.hasPermission("mf.default")) {
                boolean owner = false;
                for (Faction faction : PersistentData.getInstance().getFactions()) {
                    // if player is the owner or officer
                    if (faction.isOwner(player.getUniqueId()) || faction.isOfficer(player.getUniqueId())) {
                        owner = true;
                        // if there's more than one argument
                        if (args.length > 1) {

                            // get name of faction
                            String factionName = createStringFromFirstArgOnwards(args);

                            // check if faction exists
                            for (int i = 0; i < PersistentData.getInstance().getFactions().size(); i++) {
                                if (PersistentData.getInstance().getFactions().get(i).getName().equalsIgnoreCase(factionName)) {

                                    if (!(faction.getName().equalsIgnoreCase(factionName))) {

                                        // check that enemy is not already on list
                                        if (!(faction.isEnemy(factionName))) {

                                            // if trying to declare war on a vassal
                                            if (PersistentData.getInstance().getFactions().get(i).hasLiege()) {

                                                // if faction is vassal of declarer
                                                if (faction.isVassal(factionName)) {
                                                    player.sendMessage(ChatColor.RED + "You can't declare war on your own vassal!");
                                                    return;
                                                }

                                                // if lieges aren't the same
                                                if (!PersistentData.getInstance().getFactions().get(i).getLiege().equalsIgnoreCase(faction.getLiege())) {
                                                    player.sendMessage(ChatColor.RED + "You can't declare war on this faction as they are a vassal! You must declare war on their liege " + PersistentData.getInstance().getFactions().get(i).getLiege() + " instead!");
                                                    return;
                                                }

                                            }

                                            // disallow if trying to declare war on liege
                                            if (faction.isLiege(factionName)) {
                                                player.sendMessage(ChatColor.RED + "You can't declare war on your liege! Try '/mf declareindependence' instead!");
                                                return;
                                            }

                                            // check to make sure we're not allied with this faction
                                            if (!faction.isAlly(factionName)) {

                                                // add enemy to declarer's faction's enemyList and the enemyLists of its allies
                                                faction.addEnemy(factionName);

                                                // add declarer's faction to new enemy's enemyList
                                                PersistentData.getInstance().getFactions().get(i).addEnemy(faction.getName());

                                                for (int j = 0; j < PersistentData.getInstance().getFactions().size(); j++) {
                                                    if (PersistentData.getInstance().getFactions().get(j).getName().equalsIgnoreCase(factionName)) {
                                                        MedievalFactions.getInstance().utilities.sendAllPlayersOnServerMessage(ChatColor.RED + faction.getName() + " has declared war against " + factionName + "!");
                                                    }
                                                }

                                                // invoke alliances
                                                invokeAlliances(PersistentData.getInstance().getFactions().get(i).getName(), faction.getName(), PersistentData.getInstance().getFactions());
                                            }
                                            else {
                                                player.sendMessage(ChatColor.RED + "You can't declare war on your ally!");
                                            }

                                        }
                                        else {
                                            player.sendMessage(ChatColor.RED + "Your faction is already at war with " + factionName);
                                        }

                                    }
                                    else {
                                        player.sendMessage(ChatColor.RED + "You can't declare war on your own faction.");
                                    }
                                }
                            }

                        }
                        else {
                            player.sendMessage(ChatColor.RED + "Usage: /mf declarewar (faction-name)");
                        }
                    }
                }
                if (!owner) {
                    player.sendMessage(ChatColor.RED + "You have to own a faction or be an officer of a faction to use this command.");
                }
            }
            else {
                sender.sendMessage(ChatColor.RED + "Sorry! You need the following permission to use this command: 'mf.declarewar'");
            }
        }
    }
}
