package com.github.AndrewAlbizati.command;

import com.ambrosia.add.api.AmbrosiaAPI;
import com.ambrosia.add.api.CreditReservation;
import com.ambrosia.add.discord.util.CommandBuilder;
import com.github.AndrewAlbizati.Blackjack;
import com.github.AndrewAlbizati.game.BlackjackGame;
import discord.util.dcf.gui.base.gui.DCFGui;
import discord.util.dcf.slash.DCFSlashCommand;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Handles when a user executes the /blackjack command
 */
public class BlackjackCommand extends DCFSlashCommand implements CommandBuilder {


    @Override
    public SlashCommandData getData() {
        SlashCommandData command = Commands.slash("blackjack", "Plays a game of Blackjack with the credits you bet");
        addOptionAmount(command);
        return command;
    }

    /**
     * Creates and registers a Blackjack game.
     *
     * @param event The event.
     */
    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        User user = event.getUser();

        Integer bet = findOptionAmount(event);
        if (bet == null) return;
        // try to reserve with double-down
        CreditReservation reservation = AmbrosiaAPI.get().reserve(Blackjack.GAME_NAME, user.getIdLong(), bet);
        if (reservation.noPlayer()) {
            this.errorRegisterWithStaff(event);
            return;
        }
        if (reservation.alreadyPlaying()) {
            event.reply("Please finish your previous game before starting a new one.").queue();
            return;
        }
        if (reservation.notEnoughCredits()) {
            long difference = reservation.getReserved() - reservation.getClientCredits();
            event.reply("Sorry, you need " + difference + " more " + "credit" + (difference == 1 ? "." : "s.")).queue();
            return;
        }
        BlackjackGame game = new BlackjackGame(bet, reservation);

        DCFGui gui = new DCFGui(dcf, event::reply);
        gui.addPage(new BlackjackGameGui(gui, user, game)).send();
    }
}
