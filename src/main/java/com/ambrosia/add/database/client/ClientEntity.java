package com.ambrosia.add.database.client;

import io.ebean.DB;
import io.ebean.Model;
import io.ebean.Transaction;
import java.sql.Timestamp;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ClientEntity extends Model {

    @Id
    @Column
    public long uuid;

    @Column
    @Embedded
    public ClientMinecraftDetails minecraft;
    @Column
    @Embedded
    public ClientDiscordDetails discord;

    @Column(unique = true, nullable = false)
    public String displayName;
    @Column(nullable = false)
    public Timestamp dateCreated;

    @Column(nullable = false)
    public long credits;
    @Column(nullable = false)
    public long creator;

    public ClientEntity(long creator, String displayName) {
        this.displayName = displayName;
        this.dateCreated = Timestamp.from(Instant.now());
        this.credits = 0;
        this.creator = creator;
    }

    public ClientEntity() {

    }

    public void setCredits(long credits) {
        this.credits = credits;
    }

    public void setDiscord(ClientDiscordDetails discord) {
        this.discord = discord;
        this.displayName = discord.guildName;
    }

    public void setMinecraft(ClientMinecraftDetails minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void save() {
        try (Transaction transaction = DB.getDefault().beginTransaction()) {
            super.save();
            transaction.commit();
        }
    }
}