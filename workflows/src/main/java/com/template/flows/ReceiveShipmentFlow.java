package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

// ******************
// * Responder flow *
// ******************
@InitiatedBy(ShipmentFlow.class)
public class ReceiveShipmentFlow extends FlowLogic<SignedTransaction> {

    //private variable
    private FlowSession otherPartySession;

    //Constructor
    public ReceiveShipmentFlow(FlowSession otherPartySession) {

        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        System.out.println("received currency");
        return subFlow(new ReceiveFinalityFlow(otherPartySession));
        
    }
}
