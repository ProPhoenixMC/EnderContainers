package fr.utarwyn.endercontainers.storage.serialization;

import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

public class ConverterItemSerializer implements ItemSerializer {
    private final PaperItemSerializer paperItemSerializer = new PaperItemSerializer();
    private final Base64ItemSerializer base64ItemSerializer = new Base64ItemSerializer();

    @Override
    public String serialize(ConcurrentMap<Integer, ItemStack> items) throws IOException {
        return paperItemSerializer.serialize(items);
    }

    @Override
    public ConcurrentMap<Integer, ItemStack> deserialize(String data) throws IOException {
        if (data.contains("_")) {
            return paperItemSerializer.deserialize(data);
        } else {
            return base64ItemSerializer.deserialize(data);
        }
    }
}
