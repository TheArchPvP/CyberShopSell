package CyberTech.CyberShopSell;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.SignChangeEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.RegisteredListener;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.Utils;
import jdk.internal.org.objectweb.asm.signature.SignatureVisitor;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Vector;

/**
 * Created by carlt_000 on 4/23/2016.
 */
public class Main extends PluginBase implements Listener{

    public Config Shop;
    public Config Sell;
    public String ShopTemp;
    public String SellTemp;

    @Override
    public void onLoad() {
        this.getLogger().info(TextFormat.WHITE + "I've been loaded!");
    }

    @Override
    public void onEnable() {
        this.getLogger().info(TextFormat.DARK_GREEN + "I've been enabled!");
        new File(this.getDataFolder().toString()).mkdirs();
        Shop = new Config(new File(this.getDataFolder(), "shop.yml"),Config.YAML);
        Sell = new Config(new File(this.getDataFolder(), "sell.yml"),Config.YAML);
        Shop.save();
        Sell.save();
        getServer().getPluginManager().registerEvents(this,this);
    }

    @EventHandler
    public void OnSignPlaceEvent(SignChangeEvent event){
        LinkedHashMap<String, Object> settings = new LinkedHashMap<String, Object>(){{
            put("x",event.getBlock().getX());
            put("y",event.getBlock().getY());
            put("z",event.getBlock().getZ());
            put("level",event.getBlock().getLevel().getFolderName());
        }};
        if(event.getLine(0).equalsIgnoreCase("shop") && event.getPlayer().hasPermission("CyberTech.CyberShopSell.AddShop")){
            event.setLine(0,TextFormat.GREEN+"Tap Me!");
            event.setLine(1,TextFormat.AQUA+"To Add Item");
            event.setLine(2,TextFormat.AQUA+"Price");
            event.setLine(3,TextFormat.AQUA+"Count!");
            Block b = event.getBlock();
            String key = String.format("%s&%s&%s&%s",b.getX(),b.getY(),b.getZ(),b.getLevel().getFolderName());
            Shop.set(key,settings);
            event.getPlayer().sendMessage(TextFormat.GREEN+"Sign Set!");
        }else if(event.getLine(0).equalsIgnoreCase("sell") && event.getPlayer().hasPermission("CyberTech.CyberShopSell.AddSell")){
            event.setLine(0,TextFormat.GREEN+"Tap Me!");
            event.setLine(1,TextFormat.AQUA+"To Add Item");
            event.setLine(2,TextFormat.AQUA+"Price");
            event.setLine(3,TextFormat.AQUA+"Count!");
            Block b = event.getBlock();
            String key = String.format("%s&%s&%s&%s",b.getX(),b.getY(),b.getZ(),b.getLevel().getFolderName());
            Sell.set(key,settings);
            event.getPlayer().sendMessage(TextFormat.GREEN+"Sign Set!");
        }
        if(event.getPlayer().hasPermission("CyberTech.CyberShopSell.AddShop") || event.getPlayer().hasPermission("CyberTech.CyberShopSell.AddSell"))event.getPlayer().sendMessage("TEAsda sdasdasds123123123");
    }

    @EventHandler
    public void OnPlayerInteractEvent(PlayerInteractEvent event){
        if(event.getBlock().getId() == Block.SIGN_POST || event.getBlock().getId() == Block.WALL_SIGN){
            Block b = event.getBlock();
            String key = String.format("%s&%s&%s&%s",b.getX(),b.getY(),b.getZ(),b.getLevel().getFolderName());
            if(Shop.exists(key)&& event.getPlayer().hasPermission("CyberTech.CyberShopSell.AddShop") && !((LinkedHashMap<String, Object>)Shop.get(key)).containsKey("id")){
                if(event.getPlayer().getInventory().getItemInHand().getId() == 0)return;
                LinkedHashMap<String, Object> settings =  (LinkedHashMap<String, Object>)Shop.get(key);
                String value = event.getPlayer().getInventory().getItemInHand().getId()+"";
                if(event.getPlayer().getInventory().getItemInHand().getDamage() != 0)value = value + ":"+event.getPlayer().getInventory().getItemInHand().getDamage();
                settings.put("id",value);
                BlockEntitySign be = (BlockEntitySign)event.getBlock().getLevel().getBlockEntity(b);
                if(be != null){
                    be.setText(TextFormat.GREEN+"~~|SHOP|~~",TextFormat.AQUA+event.getPlayer().getInventory().getItemInHand().getName(),"$N/A For 0",TextFormat.GREEN+"--[BUY]--");
                    be.saveNBT();
                }
                ShopTemp = key;
            }else if(Sell.exists(key)&& event.getPlayer().hasPermission("CyberTech.CyberShopSell.AddSell") && !((LinkedHashMap<String, Object>)Shop.get(key)).containsKey("id")){
                if(event.getPlayer().getInventory().getItemInHand().getId() == 0)return;
                LinkedHashMap<String, Object> settings =  (LinkedHashMap<String, Object>)Sell.get(key);
                String value = event.getPlayer().getInventory().getItemInHand().getId()+"";
                if(event.getPlayer().getInventory().getItemInHand().getDamage() != 0)value = value + ":"+event.getPlayer().getInventory().getItemInHand().getDamage();
                settings.put("id",value);
                BlockEntitySign be = (BlockEntitySign)event.getBlock().getLevel().getBlockEntity(b);
                if(be != null){
                    be.setText(TextFormat.GREEN+"~~|SELL|~~",TextFormat.AQUA+event.getPlayer().getInventory().getItemInHand().getName(),"$N/A For 0",TextFormat.GREEN+"--[SELL]--");
                    be.saveNBT();
                }
                SellTemp = key;
            }else if(Shop.exists(key) && ((LinkedHashMap<String, Object>)Shop.get(key)).containsKey("id") && ((LinkedHashMap<String, Object>)Shop.get(key)).containsKey("cost")){
                LinkedHashMap<String, Object> settings =  (LinkedHashMap<String, Object>)Shop.get(key);
                Double cost = Double.parseDouble(settings.get("cost")+"");
                Integer amount = Integer.parseInt(settings.get("amount")+"");
                Double tc = cost * amount;
                event.getPlayer().sendMessage("TEASD asd a $"+tc);
            }else{
                event.getPlayer().sendMessage("SHOP ERROR 85!");
            }
        }
    }

    @Override
    public void onDisable() {
        Shop.save();
        Sell.save();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName()) {
            case "shop":
                if(args.length == 2) {
                    if (ShopTemp != null && ShopTemp != "") {
                        LinkedHashMap<String, Object> settings = (LinkedHashMap<String, Object>) Shop.get(ShopTemp);
                        settings.put("cost",args[0]);
                        settings.put("amount",args[1]);
                        String[] split = ShopTemp.split("&");
                        Vector3 b = new Vector3(Double.parseDouble(split[0]),Double.parseDouble(split[1]),Double.parseDouble(split[2]));
                        BlockEntitySign be = (BlockEntitySign)((Player)sender).getLevel().getBlockEntity(b);
                        if(be != null){
                            be.setText(be.getText()[0],be.getText()[1],"$"+args[0]+" For "+args[1],be.getText()[3]);
                            be.saveNBT();
                        }else{
                            sender.sendMessage("WHATTTTT");
                        }
                        sender.sendMessage(TextFormat.GREEN+"SHOP SET!");
                        ShopTemp = null;
                    }else{
                        sender.sendMessage(TextFormat.YELLOW+"Please Set a Sign Before you do this!");
                    }
                }else{
                    return false;
                }
                break;
            case "sell":
                if(args.length == 2) {
                    if (SellTemp != null && SellTemp != "") {
                        LinkedHashMap<String, Object> settings = (LinkedHashMap<String, Object>) Sell.get(SellTemp);
                        settings.put("cost",args[0]);
                        settings.put("amount",args[1]);
                        String[] split = SellTemp.split("&");
                        Vector3 b = new Vector3(Double.parseDouble(split[0]),Double.parseDouble(split[1]),Double.parseDouble(split[2]));
                        Level l = getServer().getLevelByName(split[3]);
                        if(l == null){
                            sender.sendMessage("Error Setting the Sell");
                            return true;
                        }
                        BlockEntitySign be = (BlockEntitySign)l.getBlockEntity(b);
                        if(be != null){
                            be.setText(be.getText()[0],be.getText()[1],"$"+args[0]+" For "+args[1],be.getText()[3]);
                            be.saveNBT();
                        }
                        SellTemp = null;
                        sender.sendMessage(TextFormat.GREEN+"SELL SET!");
                    }else{
                        sender.sendMessage(TextFormat.YELLOW+"Please Set a Sign Before you do this!");
                    }
                }else{
                    return false;
                }
                break;
        }
        return true;
    }

}