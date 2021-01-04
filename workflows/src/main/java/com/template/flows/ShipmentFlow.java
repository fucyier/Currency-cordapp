package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.CurrencyContract;
import com.template.states.CurrencyState;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import static com.template.contracts.CurrencyContract.CURRENCY_CONTRACT_ID;

// ******************
// * Initiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class ShipmentFlow extends FlowLogic<SignedTransaction> {

    // We will not use these ProgressTracker for this Hello-World sample
   // private final ProgressTracker progressTracker = new ProgressTracker();


    private final ProgressTracker.Step RETRIEVING_NOTARY = new ProgressTracker.Step("Retrieving the Notary.");
    private final ProgressTracker.Step GENERATING_TRANSACTION = new ProgressTracker.Step("Generating transaction.");
    private final ProgressTracker.Step SIGNING_TRANSACTION = new ProgressTracker.Step("Signing transaction with our private key.");
    private final ProgressTracker.Step COUNTERPARTY_SESSION = new ProgressTracker.Step("Sending flow to counterparty.");
    private final ProgressTracker.Step FINALISING_TRANSACTION = new ProgressTracker.Step("Obtaining notary signature and recording transaction");

    private final ProgressTracker progressTracker = new ProgressTracker(
            RETRIEVING_NOTARY,
            GENERATING_TRANSACTION,
            SIGNING_TRANSACTION,
            COUNTERPARTY_SESSION,
            FINALISING_TRANSACTION
    );
    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    //private variables

    private String currencyType;
    private Party owner;

    //public constructor
    public ShipmentFlow(String currencyType, Party owner) {
        this.currencyType = currencyType;
        this.owner = owner;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        if (getOurIdentity().getName().getOrganisation().equals("Trader1")) {
            System.out.println("identity verified-this is Trader1");
        } else
            throw new FlowException("this is not Trader1");

        // Step 1. Get a reference to the notary service on our network and our key pair.
        // Note: ongoing work to support multiple notary identities is still in progress.
        progressTracker.setCurrentStep(RETRIEVING_NOTARY);
        final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        //Compose the State that carries the Hello World message
        final CurrencyState outputState = new CurrencyState(currencyType, owner, getOurIdentity());

        // Step 3. Create a new TransactionBuilder object.
        progressTracker.setCurrentStep(GENERATING_TRANSACTION);
        final TransactionBuilder builder = new TransactionBuilder(notary);

        // Step 4. Add the iou as an output state, as well as a command to the transaction builder.
        builder.addOutputState(outputState, CURRENCY_CONTRACT_ID);
        builder.addCommand(new CurrencyContract.Shipment(), getOurIdentity().getOwningKey());


        // Step 5. Verify and sign it with our KeyPair.
        progressTracker.setCurrentStep(SIGNING_TRANSACTION);
        SignedTransaction shipmentTx = getServiceHub().signInitialTransaction(builder);
        progressTracker.setCurrentStep(COUNTERPARTY_SESSION);
        FlowSession otherPartySession = initiateFlow(owner);

        progressTracker.setCurrentStep(FINALISING_TRANSACTION);
        return subFlow(new FinalityFlow(shipmentTx, otherPartySession));


    }
}
