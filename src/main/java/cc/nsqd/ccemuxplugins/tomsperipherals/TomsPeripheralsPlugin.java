package cc.nsqd.ccemuxplugins.tomsperipherals;

import cc.nsqd.ccemuxplugins.tomsperipherals.peripherals.GPUExtPeripheral;
import com.google.auto.service.AutoService;
import dan200.computercraft.api.lua.LuaException;
import net.clgd.ccemux.api.plugins.Plugin;
import net.clgd.ccemux.api.plugins.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@AutoService(Plugin.class)
public class TomsPeripheralsPlugin extends Plugin {
	@Override
	public @NotNull String getName() {
		return "Tom's Peripherals";
	}

	@Override
	public @NotNull String getDescription() {
		return "Implements GPU peripheral from Tom's Peripherals";
	}

	@Override
	public @NotNull Optional<String> getVersion() {
		return Optional.of("0.1.0");
	}

	@Override
	public @NotNull Collection<String> getAuthors() {
		return List.of("nosqd");
	}

	@Override
	public @NotNull Optional<String> getWebsite() {
		return Optional.of("https://github.com/nosqd/CCEmuX");
	}

	@Override
	public void setup(@NotNull PluginManager manager) {
		manager.addPeripheral("tm_gpu", (computer, cfg) -> {
			try {
				return new GPUExtPeripheral();
			} catch (LuaException e) {
				throw new RuntimeException(e);
			}
		});
	}
}
