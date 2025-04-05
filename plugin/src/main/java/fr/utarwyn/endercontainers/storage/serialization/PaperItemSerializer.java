package fr.utarwyn.endercontainers.storage.serialization;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Base64;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PaperItemSerializer implements ItemSerializer {
    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Base64.Decoder decoder = Base64.getMimeDecoder();

    @Override
    public String serialize(ConcurrentMap<Integer, ItemStack> items) throws IOException {
        StringJoiner joiner = new StringJoiner("$");

        try {
            items.forEach((key, itemStack) -> {
                if (itemStack != null && !itemStack.getType().isAir() && itemStack.getAmount() > 0) {
                    joiner.add(key + "_" + encoder.encodeToString(itemStack.serializeAsBytes()));
                }
            });

            return joiner.toString();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public ConcurrentMap<Integer, ItemStack> deserialize(String data) throws IOException {
        ConcurrentMap<Integer, ItemStack> items = new ConcurrentHashMap<>();

        try {
            String[] split = StringUtils.split(data, '$');

            for (String row : split) {
                String[] parts = StringUtils.split(row, '_');
                Integer integer = Integer.parseInt(parts[0]);
                items.put(integer, ItemStack.deserializeBytes(decoder.decode(parts[1])));
            }
        } catch (Exception e) {
            throw new IOException(e);
        }

        return items;
    }
}
