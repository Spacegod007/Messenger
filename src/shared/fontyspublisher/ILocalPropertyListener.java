package shared.fontyspublisher;

import java.beans.*;

/**
 * ILocalPropertyListener. Interface to local property listener in order to
 * inform the listener about changes in the domain of properties that the
 * listener is subscribed to. 
 * 
 * @author Frank Peeters, Nico Kuijpers
 */
interface ILocalPropertyListener extends IPropertyListener  {

    /**
     * Inform listener about change of a property in the domain. On the basis
     * of the fileserver provided by the instance of PropertyChangeEvent the observer
     * is synchronized with respect to the domain.
     * 
     * @param evt PropertyChangeEvent @see java.beans.PropertyChangeEvent
     */
    void propertyChange(PropertyChangeEvent evt);
}
