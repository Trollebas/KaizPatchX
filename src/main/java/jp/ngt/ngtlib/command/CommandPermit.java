package jp.ngt.ngtlib.command;

import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.util.PermissionManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandPermit extends CommandBase {
	@Override
	public int getRequiredPermissionLevel() {
		return 3;//OP権限
	}

	@Override
	public String getCommandName() {
		return "permit";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "commands.permit.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length > 0) {
			if (args[0].equals("list")) {
				PermissionManager.INSTANCE.showPermissionList(sender);
				return;
			} else if (args[0].equals("myname")) {
				NGTLog.sendChatMessage(sender, "My name is " + sender.getCommandSenderName());
				return;
			} else if (args.length >= 3) {
				String playerName = args[1];
				String target = args[2];

				if (args[0].equals("add")) {
					PermissionManager.INSTANCE.addPermission(sender, playerName, target);
					return;
				} else if (args[0].equals("remove")) {
					PermissionManager.INSTANCE.removePermission(sender, playerName, target);
					return;
				}
			}
		}

		NGTLog.sendChatMessage(sender, "/permit add <player> <category>");
		NGTLog.sendChatMessage(sender, "/permit remove <player> <category>");
		NGTLog.sendChatMessage(sender, "/permit list");
		NGTLog.sendChatMessage(sender, "/permit myname");
	}

	private static final List<String> commandList = Arrays.asList("add", "remove", "list", "myname");

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