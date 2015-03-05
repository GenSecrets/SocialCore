import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.UUID;

/**
 * Created by john on 3/4/15.
 */
public class Serial {
    public static void main(String[] args) {
        System.out.println("Testing serialization");
        ConfigurationSerialization.registerClass(Citizen.class);
        Citizen citizen = new Citizen("Lol", UUID.randomUUID());
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("test",citizen);
        String serialized = yamlConfiguration.saveToString();
        YamlConfiguration yamlConfiguration1 = new YamlConfiguration();
        try {
            yamlConfiguration1.loadFromString(serialized);
            System.out.println(yamlConfiguration1.get("test"));
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }
}
