package com.ambrosia.add.discord.profile;

import com.ambrosia.add.database.client.ClientEntity;
import com.ambrosia.add.database.client.ClientStorage;
import com.ambrosia.add.discord.log.DiscordLog;
import com.ambrosia.add.discord.util.CommandBuilder;
import com.ambrosia.add.discord.util.SendMessage;
import discord.util.dcf.slash.DCFSlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.Nullable;

public class CreateProfileCommand extends DCFSlashCommand implements CommandBuilder, SendMessage {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        if (this.isBadPermission(event)) return;
        @Nullable String clientName = findOptionProfileName(event);
        if (clientName == null) return;
        long conductorId = event.getUser().getIdLong();
        ClientEntity client = ClientStorage.get().createClient(conductorId, clientName);
        if (client == null) {
            event.replyEmbeds(this.error(String.format("'%s' is already a user", clientName))).queue();
            return;
        }
        event.replyEmbeds(this.success(String.format("Successfully created %s", client.displayName))).queue();
        DiscordLog.log().createAccount(client, event.getUser());
    }


    public SlashCommandData getData() {
        SlashCommandData command = Commands.slash("create", "Create a profile for a customer");
        addOptionProfileName(command);
        return command.setDefaultPermissions(DefaultMemberPermissions.DISABLED).setGuildOnly(true);
    }


}