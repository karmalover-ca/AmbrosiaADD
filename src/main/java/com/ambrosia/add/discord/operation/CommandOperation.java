package com.ambrosia.add.discord.operation;

import com.ambrosia.add.database.client.ClientEntity;
import com.ambrosia.add.database.operation.OperationReason;
import com.ambrosia.add.database.operation.OperationStorage;
import com.ambrosia.add.discord.util.CommandBuilder;
import lib.DCFSlashCommand;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.Nullable;

public abstract class CommandOperation extends DCFSlashCommand implements CommandBuilder {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        if (!this.isBadPermission(event)) return;
        @Nullable String clientName = getOptionProfileName(event);
        if (clientName == null) return;
        @Nullable Integer amount = getOptionAmount(event);
        if (amount == null) return;
        @Nullable ClientEntity client = findClient(event, clientName);
        if (client == null) return;
        long conductorId = event.getUser().getIdLong();
        client = OperationStorage.get().saveOperation(conductorId, client, amount, operationReason());
        event.replyEmbeds(successMessage(client, amount)).queue();
    }


    @Override
    public CommandData getData() {
        SlashCommandData command = Commands.slash(commandName(), "Deposits credits to a profile");
        addOptionProfileName(command);
        addOptionAmount(command);
        return command.setDefaultPermissions(DefaultMemberPermissions.DISABLED).setGuildOnly(true);
    }

    protected abstract MessageEmbed successMessage(ClientEntity client, int amount);

    protected abstract OperationReason operationReason();

    protected abstract String commandName();

}
