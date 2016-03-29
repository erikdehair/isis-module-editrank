package nl.pocos.wicket.ui.components.collectioncontents.ajaxtable;

import org.apache.isis.viewer.wicket.model.hints.UiHintContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.IAjaxCallListener;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.sort.AjaxFallbackOrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByLink;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;

public class PortalAjaxFallbackOrderByBorder<T> extends AjaxFallbackOrderByBorder<T>
{
private static final long serialVersionUID = 1L;
    
    private final T sortProperty;
    private final SortableAjaxDataTable<?, ?> dataTable;

    private final ISortStateLocator<T> stateLocator;
    
    public PortalAjaxFallbackOrderByBorder(String id, SortableAjaxDataTable<?, ?> dataTable, T sortProperty, ISortStateLocator<T> stateLocator, IAjaxCallListener ajaxCallListener) {
        super(id, sortProperty, stateLocator, new OrderByLink.VoidCssProvider<T>(), ajaxCallListener);
        this.dataTable = dataTable;
        this.stateLocator = stateLocator;
        this.sortProperty = sortProperty;
    }

    @Override
    protected void onAjaxClick(final AjaxRequestTarget target)
    {
        target.add(dataTable);

        final UiHintContainer uiHintContainer = getUiHintContainer();
        if(uiHintContainer == null) {
            return;
        }
        
        final ISortState<T> state = stateLocator.getSortState();
        final SortOrder order = state.getPropertySortOrder(sortProperty);
        
        dataTable.setSortOrderHintAndBroadcast(order, sortProperty.toString(), target);
        dataTable.setPageNumberHintAndBroadcast(target);
    }

    @Override
    protected void onSortChanged()
    {
        super.onSortChanged();
        // UI hint & event broadcast in onAjaxClick
        dataTable.setCurrentPage(0); 
    }
    
    public UiHintContainer getUiHintContainer() {
        return UiHintContainer.Util.hintContainerOf(dataTable);
    }
}
