package com.template.states;

import com.template.contracts.CurrencyContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;

import java.util.Arrays;
import java.util.List;

// *********
// * State *
// *********
@BelongsToContract(CurrencyContract.class)
public class CurrencyState implements ContractState {

    //private variables
    private final String currencyType;
    private final Party owner;
    private final Party manufacturer;

    public String getCurrencyType() {
        return currencyType;
    }

    public Party getOwner() {
        return owner;
    }

    public Party getManufacturer() {
        return manufacturer;
    }

    /* Constructor of your Corda state */
    public CurrencyState(String currencyType, Party owner, Party manufacturer) {
        this.currencyType = currencyType;
        this.owner = owner;
        this.manufacturer = manufacturer;
    }

    //getters


    /* This method will indicate who are the participants and required signers when
     * this state is used in a transaction. */
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(owner, manufacturer);
    }
}