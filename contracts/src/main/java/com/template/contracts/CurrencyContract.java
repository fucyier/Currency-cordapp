package com.template.contracts;

import com.sun.istack.NotNull;
import com.template.states.CurrencyState;
import net.corda.core.contracts.*;
import net.corda.core.transactions.LedgerTransaction;

import java.security.PublicKey;
import java.util.List;

// ************
// * Contract *
// ************
public class CurrencyContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String CURRENCY_CONTRACT_ID = "com.template.contracts.CurrencyContract";

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    @Override
    public void verify(@NotNull LedgerTransaction tx) {

        if (tx.getCommands().size() != 1)
            throw new IllegalArgumentException("There can only one command ");
        Command command = tx.getCommand(0);
        CommandData commandData = command.getValue();
        List<PublicKey> requiredSigners = command.getSigners();

        if (commandData instanceof Shipment) {
            if (tx.getInputStates().size() != 0)
                throw new IllegalArgumentException("there cannot be input states");
            if (tx.getOutputStates().size() != 1)
                throw new IllegalStateException("only one currency shipped at a time");
            ContractState outputStates = tx.getOutput(0);
            if (!(outputStates instanceof CurrencyState))
                throw new IllegalStateException("output has to be of type currencyState");
            CurrencyState currencyState = (CurrencyState) outputStates;
            if (!currencyState.getCurrencyType().equals("Dolar")&&!currencyState.getCurrencyType().equals("Euro"))
                throw new IllegalStateException("this is not a dolar or Euro");
            PublicKey manufacturerKey = currencyState.getManufacturer().getOwningKey();
            if (!requiredSigners.contains(manufacturerKey))
                throw new IllegalArgumentException("manufacturer must sign the transaction");
        }
    }

    public static class Shipment implements CommandData {
    }
}