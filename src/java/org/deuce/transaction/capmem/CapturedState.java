package org.deuce.transaction.capmem;

import org.deuce.transaction.Context;
import org.deuce.transform.Exclude;


/**
 * This class can be added to the top hierarchy of transactional classes, 
 * to store the information about the transaction that has instantiated an object.
 * If a transactional class is instantiated out of the scope of any transaction,
 * then its currentOwner will be null.
 * This information is useful to identify objects that are private to a specific
 * transaction. 
 *  
 * @author fmcarvalho <mcarvalho@cc.isel.ip.pt>
 */
@Exclude
public class CapturedState {    
    protected final Object owner;

    public CapturedState(Context ctx){
	this.owner = ((ContextFilterCapturedState) ctx).trxFingerprint;
    }
    public CapturedState(){
	this.owner = null;
    }
}
