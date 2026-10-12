package org.antarcticgardens.cna.config;



public class ClientConfig {
    public final ConfigBuilder.ConfigValue<Integer> wireSectionsPerMeter;
    public final ConfigBuilder.ConfigValue<Double> wireThickness;

    public ClientConfig(ConfigBuilder builder) {
        wireSectionsPerMeter = builder
                .comment(
                        "Choose how many wire sections are rendered in one meter (block).",
                        "Decreasing this value can theoretically improve performance"
                ).defineInRange("wireSectionsPerMeter", 10, 1, Integer.MAX_VALUE);

        wireThickness = builder
                .comment("...wire thickness...")
                .defineInRange("wireThickness", 0.03, 0, Double.MAX_VALUE);
    }
}
