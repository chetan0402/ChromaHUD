/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandBase
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommandSender
 */
package me.chetan.chromahud.commands;

import me.chetan.chromahud.ChromaHUD;
import me.chetan.chromahud.ElementRenderer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class CommandChromaHud
extends CommandBase {
    private ChromaHUD mod;

    public CommandChromaHud(ChromaHUD mod) {
        this.mod = mod;
    }

    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    public String getCommandName() {
        return "chromahud";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "/chromahud";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            this.mod.setup();
        } else {
            ElementRenderer.display();
        }
    }
}

