package org.antarcticgardens.cna.config;

import net.fabricmc.loader.api.FabricLoader;
import org.antarcticgardens.cna.CreateNewAge;

/**
 * Client and server settings, read from create_new_age-client.json and create_new_age-server.json
 * in the config folder. The server file is read on both sides, as the wire sag uses the wire length.
 */
public class CNAConfig {
    private static ClientConfig client;
    private static ServerConfig server;

    public static ClientConfig getClient() {
        if (client == null) {
            ConfigBuilder builder = new ConfigBuilder();
            client = new ClientConfig(builder);
            builder.load(FabricLoader.getInstance().getConfigDir().resolve(CreateNewAge.MOD_ID + "-client.json"));
        }
        return client;
    }

    public static ServerConfig getServer() {
        if (server == null) {
            ConfigBuilder builder = new ConfigBuilder();
            server = new ServerConfig(builder);
            builder.load(FabricLoader.getInstance().getConfigDir().resolve(CreateNewAge.MOD_ID + "-server.json"));
        }
        return server;
    }

    public static void load() {
        getServer();
    }
}
