package jp.ngt.rtm;

import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.rtm.entity.train.EntityBogie;
import jp.ngt.rtm.entity.train.EntityTrainBase;
import jp.ngt.rtm.entity.train.util.TrainState.TrainStateType;
import jp.ngt.rtm.network.PacketNotice;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandRTM extends CommandBase {
	@Override
	public String getCommandName() {
		return "rtm";
	}

	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		return "commands.rtm.usage";
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] s) {
		EntityPlayerMP player = getCommandSenderAsPlayer(commandSender);
		if (s.length >= 1) {
			if (s[0].equalsIgnoreCase("use1122marker")) {
				RTMCore.NETWORK_WRAPPER.sendTo(new PacketNotice(PacketNotice.Side_CLIENT, "use1122marker," + (s.length == 2 ? Boolean.parseBoolean(s[1]) : "flip")), player);
				return;
			}
		}

		if (s.length == 2) {

			int state = Integer.parseInt(s[1]);

			double d0 = 16.0D;
			List<Entity> list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, AxisAlignedBB.getBoundingBox(player.posX - d0, player.posY - d0, player.posZ - d0, player.posX + d0, player.posY + d0, player.posZ + d0));
			list.stream().filter(EntityTrainBase.class::isInstance).map(EntityTrainBase.class::cast).forEach(train -> {
				if (s[0].equalsIgnoreCase("door")) {
					train.setTrainStateData(TrainStateType.State_Door.id, (byte) state);
				} else if (s[0].equalsIgnoreCase("pan")) {
					train.setTrainStateData(TrainStateType.State_Pantograph.id, (byte) state);
				} else if (s[0].equalsIgnoreCase("speed")) {
					train.setSpeed(state / 72.0f);
				}
			});
		} else {
			if (s[0].equalsIgnoreCase("delAllTrain")) {
				int count = 0;
				List<Entity> list = player.worldObj.loadedEntityList;
				for (Entity entity0 : list) {
					Entity entity1 = null;
					if (entity0 instanceof EntityTrainBase) {
						entity1 = entity0;
						++count;
					} else if (entity0 instanceof EntityBogie) {
						entity1 = entity0;
					}

					if (entity1 != null && !entity1.isDead) {
						entity1.setDead();
					}
				}

				NGTLog.sendChatMessage(player, "Delete " + count + "trains.");
			}
		}
	}

	private static final List<String> commandList = Arrays.asList("use1122marker", "door", "pan", "speed", "delAllTrain");

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 1) {
			//入力されている文字列と先頭一致
			if (args[0].length() == 0) {
				return commandList;
			}
			return commandList.stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		}
		return null;
	}

}