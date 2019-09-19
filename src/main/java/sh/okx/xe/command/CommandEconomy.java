package sh.okx.xe.command;

import co.aikar.commands.CommandHelp;
import co.aikar.commands.MessageType;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sh.okx.xe.XeMessageKeys;
import sh.okx.xe.XePlugin;
import sh.okx.xe.data.Currency;
import sh.okx.xe.data.CurrencyManager;
import sh.okx.xe.data.PlayerBalance;
import sh.okx.xe.database.dao.PlayerBalanceDao;

import java.math.BigDecimal;

@CommandAlias("xec|eco|economy")
@Description("Manage the economy of currencies")
public class CommandEconomy extends XeBaseCommand {
  @Dependency
  private XePlugin xe;
  @Dependency
  private PlayerBalanceDao dao;

  @HelpCommand
  public void onHelp(CommandSender sender, CommandHelp help) {
    help.showHelp();
  }

  @Subcommand("add|give|grant")
  @CommandCompletion("@players @nothing @currencies")
  @CommandPermission("xe.admin")
  public void onAdd(CommandSender sender, OfflinePlayer player, @Flags("suffixes") BigDecimal amount, Currency currency) {
    PlayerBalance balance = dao.getPlayerBalance(null, player.getUniqueId(), currency);
    balance.add(amount);
    dao.save(balance);

    sendMsg(MessageType.INFO, XeMessageKeys.ADD_SUCCESS,
        currency.replace("{amount}", currency.getFormatter().format(amount),
            "{player}", player.getName(),
            "{balance}", currency.getFormatter().format(balance.getBalance())));
    if (!xe.option("hide-add-currency") && player.isOnline()) {
      sendMsg((Player) player, MessageType.INFO, XeMessageKeys.ADD_RECEIVE,
          currency.replace("{amount}", currency.getFormatter().format(amount),
              "{balance}", currency.getFormatter().format(balance.getBalance())));
    }
  }

  @CommandAlias("bal|balance")
  @Description("Check a player's balance")
  @CommandCompletion("@players @currencies")
  public void onBalance(CommandSender sender, OfflinePlayer player, @Flags("nullable") Currency currency) {
    if (xe.option("balance-show-all-currencies") && currency == null) {
      if (sender.equals(player)) {
        sendMsg(MessageType.INFO, XeMessageKeys.BAL_ALL_HEADER_SELF);
      } else {
        sendMsg(MessageType.INFO, XeMessageKeys.BAL_ALL_HEADER_OTHER,
            "{player}", player.getName());
      }
      for (Currency currency1 : CurrencyManager.getInstance().getCurrencies()) {
        PlayerBalance balance = dao.getPlayerBalance(null, player.getUniqueId(), currency1);
        if (balance.getBalance().compareTo(BigDecimal.ZERO) == 0
            && xe.option("balance-hide-no-currency")) {
          continue;
        }
        sendMsg(MessageType.INFO, XeMessageKeys.BAL_ALL,
            currency1.replace("{amount}", currency1.getFormatter().format(balance.getBalance())));
      }
    } else {
      if (currency == null) {
        currency = CurrencyManager.getInstance().getPrimaryCurrency();
      }
      PlayerBalance balance = dao.getPlayerBalance(null, player.getUniqueId(), currency);
      String balanceFormatted = currency.getFormatter().format(balance.getBalance());
      if (sender.equals(player)) {
        sendMsg(MessageType.INFO, XeMessageKeys.BAL_SPECIFIC_SELF,
            currency.replace("{amount}", balanceFormatted));
      } else {
        sendMsg(MessageType.INFO, XeMessageKeys.BAL_SPECIFIC_OTHER,
            currency.replace("{amount}", balanceFormatted,
                "{player}", player.getName()));
      }
    }
  }

  @Subcommand("take|subtract|remove|deduct")
  @CommandCompletion("@players @nothing @currencies")
  @CommandPermission("xe.admin")
  public void onTake(CommandSender sender, OfflinePlayer player, @Flags("suffixes") BigDecimal amount, Currency currency) {
    PlayerBalance balance = dao.getPlayerBalance(null, player.getUniqueId(), currency);
    balance.add(amount.negate());
    dao.save(balance);

    sendMsg(MessageType.INFO, XeMessageKeys.TAKE_SUCCESS,
        currency.replace("{amount}", currency.getFormatter().format(amount),
            "{player}", player.getName(),
            "{balance}", currency.getFormatter().format(balance.getBalance())));
    if (!xe.option("hide-take-currency") && player.isOnline()) {
      sendMsg((Player) player, MessageType.INFO, XeMessageKeys.TAKE_RECEIVE,
          currency.replace("{amount}", currency.getFormatter().format(amount),
              "{balance}", currency.getFormatter().format(balance.getBalance())));
    }
  }

  @Subcommand("set")
  @CommandCompletion("@players @nothing @currencies")
  @CommandPermission("xe.admin")
  public void onSet(CommandSender sender, OfflinePlayer player, @Flags("suffixes") BigDecimal amount, Currency currency) {
    PlayerBalance balance = dao.getPlayerBalance(null, player.getUniqueId(), currency);
    balance.setBalance(amount);
    dao.save(balance);

    sendMsg(MessageType.INFO, XeMessageKeys.SET_SUCCESS,
        currency.replace("{amount}", currency.getFormatter().format(amount),
            "{player}", player.getName()));
    if (!xe.option("hide-set-currency") && player.isOnline()) {
      sendMsg((Player) player, MessageType.INFO, XeMessageKeys.SET_RECEIVE,
          currency.replace("{amount}", currency.getFormatter().format(amount)));
    }
  }
}
