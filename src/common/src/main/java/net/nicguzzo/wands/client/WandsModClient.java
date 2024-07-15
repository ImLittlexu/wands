package net.nicguzzo.wands.client;

#if MC=="1165"
import me.shedaniel.architectury.event.events.GuiEvent;
import me.shedaniel.architectury.event.events.client.ClientLifecycleEvent;
import me.shedaniel.architectury.event.events.client.ClientTickEvent;
import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.architectury.networking.NetworkManager.Side;
import me.shedaniel.architectury.registry.KeyBindings;
import me.shedaniel.architectury.registry.MenuRegistry;
#else
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.NetworkManager.Side;
import dev.architectury.registry.menu.MenuRegistry;
#endif
import io.netty.buffer.Unpooled;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.nicguzzo.wands.WandsMod;
import net.nicguzzo.wands.client.render.ClientRender;
import net.nicguzzo.wands.client.screens.MagicBagScreen;
import net.nicguzzo.wands.client.screens.PaletteScreen;
import net.nicguzzo.wands.client.screens.WandScreen;
import net.nicguzzo.wands.items.WandItem;
import net.nicguzzo.wands.utils.Compat;
import net.nicguzzo.wands.utils.WandUtils;
import net.nicguzzo.wands.wand.Wand;
import net.nicguzzo.wands.wand.WandProps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

#if MC >= "1200"
import net.minecraft.client.gui.GuiGraphics;
#endif
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WandsModClient {
    static boolean shift =false;
    static boolean alt =false;
    public static boolean has_optifine=false;
    public static boolean has_opac=false;
    public static KeyMapping wand_menu_km;
    //public static KeyMapping palette_menu_km;
    public static final Logger LOGGER = LogManager.getLogger();
    static final public Map keys=new HashMap<KeyMapping, WandsMod.WandKeys>();
    static final public int wand_menu_key        = GLFW.GLFW_KEY_Y;// InputConstants.KEY_Y;
    static final public int wand_mode_key        = GLFW.GLFW_KEY_V;//InputConstants.KEY_V;
    static final public int wand_action_key      = GLFW.GLFW_KEY_H;//InputConstants.KEY_H;
    static final public int wand_orientation_key = GLFW.GLFW_KEY_X;//InputConstants.KEY_X;
    static final public int wand_undo_key        = GLFW.GLFW_KEY_U;//InputConstants.KEY_U;
    static final public int wand_invert_key      = GLFW.GLFW_KEY_I;//InputConstants.KEY_I;
    static final public int wand_fill_circle_key = GLFW.GLFW_KEY_K;//InputConstants.KEY_K;
    static final public int wand_rotate           = GLFW.GLFW_KEY_R;//InputConstants.KEY_R;
    static final public int palette_mode_key     = GLFW.GLFW_KEY_P;//InputConstants.KEY_P;
    static final public int palette_menu_key     = GLFW.GLFW_KEY_J;//InputConstants.KEY_J;
    static final public int wand_conf_key  = -1;
    static final public int wand_m_inc_key = GLFW.GLFW_KEY_RIGHT;//InputConstants.KEY_RIGHT;
    static final public int wand_m_dec_key = GLFW.GLFW_KEY_LEFT;//InputConstants.KEY_LEFT;
    static final public int wand_n_inc_key = GLFW.GLFW_KEY_UP;//InputConstants.KEY_UP;
    static final public int wand_n_dec_key = GLFW.GLFW_KEY_DOWN;//InputConstants.KEY_DOWN;
    static final public int toggle_stair_slab_key = GLFW.GLFW_KEY_PERIOD;//InputConstants.KEY_PERIOD;
    static final public int area_diagonal_spread = GLFW.GLFW_KEY_COMMA;//InputConstants.KEY_COMMA;
    static final public int inc_sel_block=GLFW.GLFW_KEY_Z;//InputConstants.KEY_Z;
    static final String tab="key.categories.wands";
    static final String k="key.wands.";

    public static void initialize() {

        wand_menu_km=new KeyMapping(k+"wand_menu",wand_menu_key,tab);
        keys.put(wand_menu_km,WandsMod.WandKeys.MENU);
        keys.put(new KeyMapping(k+"wand_mode",wand_mode_key,tab),WandsMod.WandKeys.MODE);
        keys.put(new KeyMapping(k+"palette_menu",palette_menu_key,tab),WandsMod.WandKeys.PALETTE_MENU);
        keys.put(new KeyMapping(k+"wand_action",wand_action_key,tab),WandsMod.WandKeys.ACTION);
        keys.put(new KeyMapping(k+"wand_orientation",wand_orientation_key,tab),WandsMod.WandKeys.ORIENTATION);
        keys.put(new KeyMapping(k+"wand_invert",wand_invert_key,tab),WandsMod.WandKeys.INVERT);
        keys.put(new KeyMapping(k+"wand_fill_circle",wand_fill_circle_key,tab),WandsMod.WandKeys.FILL);
        keys.put(new KeyMapping(k+"wand_undo",wand_undo_key,tab),WandsMod.WandKeys.UNDO);
        keys.put(new KeyMapping(k+"wand_palette_mode",palette_mode_key,tab),WandsMod.WandKeys.PALETTE_MODE);
        keys.put(new KeyMapping(k+"wand_rotate",wand_rotate,tab),WandsMod.WandKeys.ROTATE);
        keys.put(new KeyMapping(k+"m_inc",wand_m_inc_key,tab),WandsMod.WandKeys.M_INC);
        keys.put(new KeyMapping(k+"m_dec",wand_m_dec_key,tab),WandsMod.WandKeys.M_DEC);
        keys.put(new KeyMapping(k+"n_inc",wand_n_inc_key,tab),WandsMod.WandKeys.N_INC);
        keys.put(new KeyMapping(k+"n_dec",wand_n_dec_key,tab),WandsMod.WandKeys.N_DEC);
        keys.put(new KeyMapping(k+"toggle_stair_slab",toggle_stair_slab_key,tab),WandsMod.WandKeys.TOGGLE_STAIRSLAB);
        keys.put(new KeyMapping(k+"area_diagonal_spread",area_diagonal_spread,tab),WandsMod.WandKeys.DIAGONAL_SPREAD);
        keys.put(new KeyMapping(k+"inc_sel_block",inc_sel_block,tab),WandsMod.WandKeys.INC_SEL_BLK);
        keys.put(new KeyMapping(k+"clear_wand",GLFW.GLFW_KEY_C,tab),WandsMod.WandKeys.CLEAR);
        /*keys.put(new KeyMapping(k+"clear_wand",
            InputConstants.Type.MOUSE,
            InputConstants.MOUSE_BUTTON_LEFT,
            tab),WandsMod.WandKeys.CLEAR);*/

        keys.forEach((km,v) -> Compat.register_key((KeyMapping)km));

        ClientTickEvent.CLIENT_PRE.register(e -> {
            boolean any=false;
            Iterator<Map.Entry<KeyMapping,WandsMod.WandKeys>> itr = keys.entrySet().iterator();
            while(itr.hasNext())
            {
                Map.Entry<KeyMapping,WandsMod.WandKeys> me=itr.next();
                KeyMapping km=me.getKey();
                WandsMod.WandKeys key=me.getValue();
                if (km.consumeClick()) {
                    if(!any) any=true;
                    if(key== WandsMod.WandKeys.CLEAR){
                        cancel_wand();
                    }else {
                        send_key(key.ordinal(), Screen.hasShiftDown(), Screen.hasAltDown());
                    }
                }
            }

            if(!any){
                if(alt !=Screen.hasAltDown() || shift !=Screen.hasShiftDown()){
                    alt =Screen.hasAltDown();
                    shift =Screen.hasShiftDown();
                    ClientRender.wand.is_alt_pressed=alt;
                    ClientRender.wand.is_shift_pressed=shift;
                    send_key(-1, shift, alt);
                }
            }
        });

        Compat.render_info();

        if(WandsMod.is_forge) {
            ClientLifecycleEvent.CLIENT_SETUP.register(e -> {
                WandsMod.LOGGER.info("registering menues...");
                try {
                    MenuRegistry.registerScreenFactory(WandsMod.PALETTE_CONTAINER.get(), PaletteScreen::new);
                    MenuRegistry.registerScreenFactory(WandsMod.WAND_CONTAINER.get(), WandScreen::new);
                    MenuRegistry.registerScreenFactory(WandsMod.MAGIC_WAND_CONTANIER.get(), MagicBagScreen::new);
                } catch (Exception ex) {
                    WandsMod.LOGGER.error(ex.getMessage());
                }
                WandsMod.LOGGER.info("registering menues.");
            });
        }else {
            MenuRegistry.registerScreenFactory(WandsMod.PALETTE_CONTAINER.get(), PaletteScreen::new);
            MenuRegistry.registerScreenFactory(WandsMod.WAND_CONTAINER.get(), WandScreen::new);
            MenuRegistry.registerScreenFactory(WandsMod.MAGIC_WAND_CONTANIER.get(), MagicBagScreen::new);
        }
        NetworkManager.registerReceiver(Side.S2C, WandsMod.SND_PACKET, (packet, context)->{
            BlockPos pos=packet.readBlockPos();
            boolean destroy=packet.readBoolean();
            ItemStack item_stack=packet.readItem();
            boolean no_tool=packet.readBoolean();
            boolean damaged_tool=packet.readBoolean();
            int i_sound=packet.readInt();
            context.queue(()->{
                //WandsMod.LOGGER.info("got sound msg "+item_stack);
                if(i_sound> -1 && i_sound< Wand.Sounds.values().length){
                    Wand.Sounds snd=Wand.Sounds.values()[i_sound];
                    SoundEvent sound = snd.get_sound();
                    Compat.player_level(context.getPlayer()).playSound(context.getPlayer(), pos, sound, SoundSource.BLOCKS, 1.0f, 1.0f);
                }else {
                    if (!item_stack.isEmpty()) {
                        Block block = Block.byItem(item_stack.getItem());
                        SoundType sound_type = block.getSoundType(block.defaultBlockState());
                        SoundEvent sound = (destroy ? sound_type.getBreakSound() : sound_type.getPlaceSound());
                        Compat.player_level(context.getPlayer()).playSound(context.getPlayer(), pos, sound, SoundSource.BLOCKS, 1.0f, 1.0f);
                    }
                }
                if(no_tool){
                    Minecraft.getInstance().getToasts().addToast(new WandToast("no tool"));
                }
                if (damaged_tool) {
                    Minecraft.getInstance().getToasts().addToast(new WandToast("invalid or damaged"));
                }                
            });
        });
        NetworkManager.registerReceiver(Side.S2C,WandsMod.STATE_PACKET, (packet,context)->{
            long seed=packet.readLong();
            int  mode=packet.readInt();
            int  slot=packet.readInt();
            boolean  xp=packet.readBoolean();
            int  levels=packet.readInt();
            float prog=packet.readFloat();
            context.queue(()->{
                if(ClientRender.wand!=null) {
                    //ClientRender.wand.palette.seed = seed;
                    ClientRender.wand.mode= WandProps.Mode.values()[mode];
                    if(ClientRender.wand.mode== WandProps.Mode.DIRECTION)
                        ClientRender.wand.palette.slot = slot;
                    if(xp){
                        context.getPlayer().experienceLevel=levels;
                        context.getPlayer().experienceProgress=prog;
                    }
                }
            });
        });

        NetworkManager.registerReceiver(Side.S2C,WandsMod.CONF_PACKET, (packet,context)->{
            ServerData srv = Minecraft.getInstance().getCurrentServer();
            if(srv!=null && WandsMod.config!=null){
                WandsMod.config.blocks_per_xp=packet.readFloat();
                WandsMod.config.destroy_in_survival_drop=packet.readBoolean();
                WandsMod.config.survival_unenchanted_drops=packet.readBoolean();
                WandsMod.config.allow_wand_to_break=packet.readBoolean();
                WandsMod.config.allow_offhand_to_break=packet.readBoolean();
                WandsMod.config.mend_tools=packet.readBoolean();
                LOGGER.info("got config");
                context.queue(()->{


                });
            }
        });

        
    }
    public static void send_key(int key,boolean shift, boolean alt){
        Minecraft client=Minecraft.getInstance();
        if(client.getConnection() != null) {
            FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
            packet.writeInt(key);
            packet.writeBoolean(shift);
            packet.writeBoolean(alt);
            NetworkManager.sendToServer(WandsMod.KB_PACKET, packet);
        }
    }
    public static void send_palette(boolean next_mode,boolean toggle_rotate){
        FriendlyByteBuf packet=new FriendlyByteBuf(Unpooled.buffer());            
        packet.writeBoolean(next_mode);
        packet.writeBoolean(toggle_rotate);
        NetworkManager.sendToServer(WandsMod.PALETTE_PACKET, packet);
    }

    public static void send_wand(ItemStack item) {
        FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeItem(item);
        NetworkManager.sendToServer(WandsMod.WAND_PACKET, packet);
    }

#if MC < "1200"
    public static void render_wand_info(PoseStack poseStack){
#else
    public static void render_wand_info(GuiGraphics gui) {
#endif
        Minecraft client = Minecraft.getInstance();
        if(client!=null && client.player!=null){
            ItemStack stack=client.player.getMainHandItem();
            ItemStack offhand_stack=client.player.getOffhandItem();
            boolean main=stack!=null && !stack.isEmpty() && stack.getItem() instanceof WandItem;
            boolean off =offhand_stack!=null && !offhand_stack.isEmpty() && offhand_stack.getItem() instanceof WandItem;
            if(main || off){
                
                
                
                int screenWidth =client.getWindow().getGuiScaledWidth();
                int screenHeight = client.getWindow().getGuiScaledHeight();
                if(main){
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    Font font = client.font;

                    Compat.set_color(1.0F, 1.0F, 1.0F, 1.0F);
                    Compat.set_pos_tex_shader();

                    Wand wand=ClientRender.wand;
                    WandProps.Mode mode=WandProps.getMode(stack);
                    WandProps.Action action=WandProps.getAction(stack);
                    Rotation r = WandProps.getRotation(stack);
                    String rot = "";
                    switch (r) {
                        case NONE:
                            rot = "0°";
                            break;
                        case CLOCKWISE_90:
                            rot = "90°";
                            break;
                        case CLOCKWISE_180:
                            rot = "180°";
                            break;
                        case COUNTERCLOCKWISE_90:
                            rot = "270°";
                            break;
                    }
                    String p1="";
                    String p2="";
                    BlockPos bp1= wand.getP1();
                    BlockPos bp2= wand.getP2();
                    if(wand.getP1() !=null) {
                        p1 = "p1:[" + bp1.getX() + "," + bp1.getY() + "," + bp1.getZ() + "]";
                    }
                    if(wand.getP2() !=null) {
                        p2 = "p2:[" + bp2.getX() + "," + bp2.getY() + "," + bp2.getZ() + "]";
                    }else{
                        if(wand.getP1() !=null) {
                            p2 = "p2:[" + ClientRender.last_pos.getX() + "," + ClientRender.last_pos.getY() + "," + ClientRender.last_pos.getZ() + "]";
                        }
                    }

                    String ln1="";
                    String ln2="Action: "+Compat.translatable(action.toString()).getString();
                    String ln3="Mode: "+Compat.translatable(mode.toString()).getString()+" Rot:"+rot;
                    if(wand.valid) {
                        switch(mode){
                            case DIRECTION:
                                int mult=WandProps.getVal(stack, WandProps.Value.MULTIPLIER);
                                ln1="pos: ["+wand.pos.getX()+","+wand.pos.getY()+","+wand.pos.getZ()+"] x"+mult;
                                break;
                            case GRID:
                                int gm=WandProps.getVal(stack, WandProps.Value.GRIDM);
                                int gn=WandProps.getVal(stack, WandProps.Value.GRIDN);
                                int gms=WandProps.getVal(stack, WandProps.Value.GRIDMS);
                                int gns=WandProps.getVal(stack, WandProps.Value.GRIDNS);
                                String skp="";
                                if(gms>0||gns>0){
                                    skp=" - ("+gms+"x"+gns+")";
                                }
                                ln1="Grid "+gm+"x"+gn+ skp;
                                break;

                            case ROW_COL:
                            case LINE:
                            case AREA:
                            case VEIN:
                            case FILL:
                                if(mode== WandProps.Mode.FILL){
                                    int nx=wand.fill_nx+1;
                                    int ny=wand.fill_ny+1;
                                    int nz=wand.fill_nz+1;
                                    ln1+="volume ["+nx+","+ny+","+nz+"] ";
                                }
                                int arealim=WandProps.getVal(stack, WandProps.Value.AREALIM);
                                ln1+="Blocks: "+wand.block_buffer.get_length();
                                if(arealim>0){
                                    ln1+=" Limit: "+arealim;
                                }
                                break;
                            case CIRCLE:
                                ln1="Radius: "+wand.radius + " N: "+wand.block_buffer.get_length();
                                break;
                            case COPY:
                            case PASTE:
                                ln1="Copied Blocks: "+wand.copy_paste_buffer.size();
                                break;
                        }
                    }
                    int h=3*font.lineHeight;
                    float x=(int)(screenWidth* (((float)WandsMod.config.wand_mode_display_x_pos)/100.0f));
                    float y=(int)((screenHeight-h)* (((float)WandsMod.config.wand_mode_display_y_pos)/100.0f));
                    #if MC < "1200"
                        font.draw(poseStack,ln1,x,y,0xffffff);
                        font.draw(poseStack,ln2,x,y+font.lineHeight,0xffffff);
                        font.draw(poseStack,ln3,x,y+font.lineHeight*2,0xffffff);
                        font.draw(poseStack,p1,x,y-font.lineHeight*2,0xffffff);
                        font.draw(poseStack,p2,x,y-font.lineHeight,0xffffff);
                    #else
                        gui.drawString(font,ln1,(int)x,(int)y,0xffffff);
                        gui.drawString(font,ln2,(int)x,(int)y+font.lineHeight,0xffffff);
                        gui.drawString(font,ln3,(int)x,(int)y+font.lineHeight*2,0xffffff);
                        gui.drawString(font,p1 ,(int)x,(int)y-font.lineHeight*2,0xffffff);
                        gui.drawString(font,p2 ,(int)x,(int)y-font.lineHeight,0xffffff);
                    #endif
                }
                if(WandsMod.config.show_tools_info) {
                    Font font = client.font;
                    ItemRenderer itemRenderer = client.getItemRenderer();
                    if(!main){
                        stack=offhand_stack;
                    }
                    CompoundTag ctag = stack.getOrCreateTag();
                    ListTag tag = ctag.getList("Tools", Compat.NbtType.COMPOUND);
                    //int ix = 4;
                    //int iy = screenHeight-20;
                    int ix=(int)(screenWidth* (((float)WandsMod.config.wand_tools_display_x_pos)/100.0f));
                    int iy=(int)((screenHeight-20)* (((float)WandsMod.config.wand_tools_display_y_pos)/100.0f));
                    

                    tag.forEach(element -> {
                        CompoundTag stackTag = (CompoundTag) element;
                        int slot = stackTag.getInt("Slot");
                        ItemStack item = ItemStack.of(stackTag.getCompound("Tool"));
                        int yoff=0;
                        if(ClientRender.has_target && slot==ClientRender.wand.digger_item_slot){
                            yoff=-5;
                        }
                        #if MC < "1200"
                            #if MC <= "1193"
                            itemRenderer.renderAndDecorateItem(item, ix + slot * 16, iy+yoff);
                            itemRenderer.renderGuiItemDecorations(font, item, ix + slot * 16, iy, null);
                            #else
                            itemRenderer.renderAndDecorateItem(poseStack,item, ix + slot * 16, iy+yoff);
                            itemRenderer.renderGuiItemDecorations(poseStack,font, item, ix + slot * 16, iy, null);
                            #endif
                        #else
                            gui.renderFakeItem(item, ix + slot * 16, iy+yoff);
                            gui.renderItemDecorations(font, item, ix + slot * 16, iy);
                        #endif
                        int fortune= EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE,item);
                        int silk= EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH,item);
                        if(fortune!=0) {
                            #if MC < "1200"
                                font.draw(poseStack, Compat.literal("F" + fortune), ix + slot * 16, iy + yoff - 5, 0xffffff);
                            #else
                                gui.drawString(font, Compat.literal("F" + fortune), ix + slot * 16, iy + yoff - 5, 0xffffff);
                            #endif
                        } else if (silk != 0) {
                            #if MC < "1200"
                                font.draw(poseStack, Compat.literal("S"), ix + slot * 16, iy + yoff - 5, 0xffffff);
                            #else
                                gui.drawString(font, Compat.literal("S"), ix + slot * 16, iy + yoff - 5, 0xffffff);
                            #endif
                        }
                    });
                }
            }
        }
    }
    public static void cancel_wand(){
        if(ClientRender.wand!=null && ClientRender.wand.wand_stack != null && WandUtils.is_wand(ClientRender.wand.wand_stack)){
            ClientRender.wand.clear();
            if(ClientRender.wand.player!=null) {
                ClientRender.wand.player.displayClientMessage(Compat.literal("wand cleared"), false);
            }
        }
    }
}