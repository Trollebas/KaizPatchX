package jp.ngt.rtm.sound;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.modelpack.RTMResourceHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundList;
import net.minecraft.client.audio.SoundListSerializer;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.sound.SoundLoadEvent;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SideOnly(Side.CLIENT)
public class RTMSoundHandler {
    public static final List<String> ALL_OGG_FILES = new ArrayList<>();
    private static final Gson gson = (new GsonBuilder()).registerTypeAdapter(SoundList.class, new SoundListSerializer()).create();
    private static final ParameterizedType parmType = new ParameterizedType() {
        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{String.class, SoundList.class};
        }

        @Override
        public Type getRawType() {
            return Map.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    };

    private final Minecraft mc = NGTUtilClient.getMinecraft();

    public void onLoadSound(SoundLoadEvent event) {
        NGTLog.debug("[RTM](Client) Start loading sounds");

        List<String> domainList = RTMResourceHandler.registerSounds();
        IResourceManager mcResourceManager = this.mc.getResourceManager();
        if (domainList.isEmpty() || mcResourceManager == null) {
            NGTLog.debug("[RTM](Client) Cant't start loading sounds");
            return;
        }

        domainList.forEach(domain -> {
            try {
                List list = mcResourceManager.getAllResources(new ResourceLocation(domain, "sounds.json"));
                for (Object value : list) {
                    IResource iresource = (IResource) value;
                    try {
                        Map map = gson.fromJson(new InputStreamReader(iresource.getInputStream()), parmType);

                        for (Object o : map.entrySet()) {
                            Entry entry = (Entry) o;
                            ALL_OGG_FILES.add(domain + ":" + entry.getKey());
                            this.registerSound(event.manager.sndHandler, new ResourceLocation(domain, (String) entry.getKey()), (SoundList) entry.getValue());
                        }
                    } catch (RuntimeException e) {
                        throw new SoundLoadException(e);
                    }
                }
            } catch (IOException e) {
                throw new SoundLoadException(e);
            }
        });
    }

    private void registerSound(SoundHandler par1, ResourceLocation par2, SoundList par3) {
        NGTUtil.getMethod(SoundHandler.class, par1, new String[]{"loadSoundResource", "func_147693_a"}, new Class[]{ResourceLocation.class, SoundList.class}, par2, par3);
    }

    private static class SoundLoadException extends RuntimeException {
        public SoundLoadException(Throwable arg) {
            super(arg);
        }
    }
}