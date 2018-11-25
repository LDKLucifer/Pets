package me.petterim1.pets;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import me.petterim1.pets.entities.*;

/*

PPPPPPPPPPPPPPPPP                              tttt                            !!!
P::::::::::::::::P                          ttt:::t                           !!:!!
P::::::PPPPPP:::::P                         t:::::t                           !:::!
PP:::::P     P:::::P                        t:::::t                           !:::!
P::::P     P:::::P  eeeeeeeeeeee    ttttttt:::::ttttttt        ssssssssss     !:::!
P::::P     P:::::Pee::::::::::::ee  t:::::::::::::::::t      ss::::::::::s    !:::!
P::::PPPPPP:::::Pe::::::eeeee:::::eet:::::::::::::::::t    ss:::::::::::::s   !:::!
P:::::::::::::PPe::::::e     e:::::etttttt:::::::tttttt    s::::::ssss:::::s  !:::!
P::::PPPPPPPPP  e:::::::eeeee::::::e      t:::::t           s:::::s  ssssss   !:::!
P::::P          e:::::::::::::::::e       t:::::t             s::::::s        !:::!
P::::P          e::::::eeeeeeeeeee        t:::::t                s::::::s     !!:!!
P::::P          e:::::::e                 t:::::t      ttttt       sss:::::s   !!! 
PP::::::PP        e::::::::e                t::::::tttt:::::ts:::::ssss::::::s
P::::::::P         e::::::::eeeeeeee        tt::::::::::::::ts::::::::::::::s  !!!
P::::::::P          ee:::::::::::::e          tt:::::::::::tt s:::::::::::ss  !!:!!
PPPPPPPPPP            eeeeeeeeeeeeee            ttttttttttt    sssssssssss     !!!

*----------------------*
| Pets for Nukkit      |
| Created by PetteriM1 |
*----------------------*

*/
public class Main extends PluginBase {

    private int configVersion = 1;
    private Config config;
    public static Main instance;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.config = getConfig();

        if (config.getInt("configVersion") != configVersion) {
            this.getServer().getLogger().warning("Config file is outdated");
        }

        this.registerPets();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("pet")) {
            if (args.length == 0) {
                sender.sendMessage("\u00A7d* \u00A7aPets \u00A7d*");
                sender.sendMessage("\u00A76/pet add <player> <pet>");
                sender.sendMessage("\u00A76/pet remove <player>");
                sender.sendMessage("\u00A76/pet list");
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "add":
                    if (args.length != 3) {
                        sender.sendMessage("\u00A76Usage: /pet add <player> <pet>");
                        return true;
                    }

                    if (this.getServer().getPlayerExact(args[1]) == null) {
                        sender.sendMessage("\u00A7d>> \u00A7cUnknown player");
                        return true;
                    }

                    if (config.getString("players." + args[1].toLowerCase()).contains("Cat") || config.getString("players." + args[1].toLowerCase()).contains("Dog")) {
                        sender.sendMessage("\u00A7d>> \u00A7cThis player already have a pet");
                        return true;
                    }

                    Entity ent = Utils.create(args[2], this.getServer().getPlayer(args[1]));

                    if (ent != null) {
                        if (!(ent instanceof EntityPet)) {
                            ent.close();
                            sender.sendMessage("\u00A7d>> \u00A7aAvailable pets: \u00A76Cat & Dog");
                            return true;
                        }

                        ((EntityPet) ent).setOwner(this.getServer().getPlayer(args[1]).getName());
                        ((EntityPet) ent).setRandomType();
                        ent.spawnToAll();

                        config.set("players." + args[1].toLowerCase(), args[2]);
                        config.save();

                        sender.sendMessage("\u00A7d>> \u00A7aPet added");
                        return true;
                    }

                    sender.sendMessage("\u00A7d>> \u00A7cUnable to add pet");
                    return true;
                case "remove":
                    if (args.length != 2) {
                        sender.sendMessage("\u00A76Usage: /pet remove <player>");
                        return true;
                    }

                    if (this.getServer().getPlayerExact(args[1]) == null) {
                        sender.sendMessage("\u00A7d>> \u00A7cUnknown player");
                        return true;
                    }

                    config.set("players." + args[1].toLowerCase(), "null");
                    config.save();

                    for (Level level : this.getServer().getLevels().values()) {
                        for (Entity entity : level.getEntities()) {
                            if (entity instanceof EntityPet) {
                                if (((EntityPet) entity).getOwner() == this.getServer().getPlayer(args[1])) {
                                    entity.close();
                                }
                            }
                        }
                    }

                    sender.sendMessage("\u00A7d>> \u00A7aPet removed");
                    return true;
                case "list":
                    sender.sendMessage("\u00A7d>> \u00A7aAvailable pets: \u00A76Cat & Dog");
                    return true;
                default:
                    sender.sendMessage("\u00A7d* \u00A7aPets \u00A7d*");
                    sender.sendMessage("\u00A76/pet add <player> <pet>");
                    sender.sendMessage("\u00A76/pet remove <player>");
                    sender.sendMessage("\u00A76/pet list");
            }
        }

        return true;
    }

    public Config getPluginConfig() {
        return this.config;
    }

    private void registerPets() {
        Entity.registerEntity("Cat", PetCat.class);
        Entity.registerEntity("Dog", PetDog.class);
    }

    public String getNameTagColor() {
        return config.getString("nameTagColor").replace("§", "\u00A7");
    }
}