package nl.pocos.wicket.ui.components.collectioncontents.ajaxtable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.adapter.oid.RootOid;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.runtime.persistence.adapter.PojoAdapter;
import org.apache.isis.viewer.wicket.model.hints.IsisUiHintEvent;
import org.apache.isis.viewer.wicket.model.hints.UiHintContainer;
import org.apache.isis.viewer.wicket.model.models.EntityModel;
import org.apache.isis.viewer.wicket.ui.util.CssClassAppender;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.repeater.IItemFactory;
import org.apache.wicket.markup.repeater.IItemReuseStrategy;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Generics;

import com.googlecode.wicket.jquery.core.IJQueryWidget;
import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.JQueryEvent;

import nl.pocos.applib.editrank.Sortable;
import nl.pocos.wicket.behavior.DataAppender;
import nl.pocos.wicket.ui.components.collectioncontents.ajaxtable.JQuerySortableBehavior.SortableEvent;

public class SortableAjaxDataTable<T, S> extends DataTable<T, S> implements IJQueryWidget, ITableSortJQueryListener
{
private static final long serialVersionUID = 1L;
    
    static final String UIHINT_PAGE_NUMBER = "pageNumber";

    private PortalUnsortableHeadersToolbar<S> headersToolbar;

	public SortableAjaxDataTable(String id, List<? extends IColumn<T, S>> columns,
			ISortableDataProvider<T, S> dataProvider)
	{
		super(id, columns, dataProvider, Integer.MAX_VALUE);
        setOutputMarkupId(true);
        setVersioned(false);
        setItemReuseStrategy(new PreserveModelReuseStrategy());
	}
	
	@Override
    protected void onInitialize() {
        super.onInitialize();
        buildGui();
    }
    
    private void buildGui() {
        headersToolbar = new PortalUnsortableHeadersToolbar<S>(this);
        addTopToolbar(headersToolbar);
        addBottomToolbar(new NoRecordsToolbar(this));
        this.add(JQueryWidget.newWidgetBehavior(this)); // cannot be in ctor as the markupId may be set manually afterward
    }
	
	@Override
    protected Item<T> newRowItem(final String id, final int index, final IModel<T> model)
    {
        return new OddEvenItem<T>(id, index, model)
        {
            private static final long serialVersionUID = 1L;

			@Override
            protected void onComponentTag(ComponentTag tag)
			{
                super.onComponentTag(tag);

                if (model instanceof EntityModel)
                {
                    EntityModel entityModel = (EntityModel) model;
                    final ObjectAdapter objectAdapter = entityModel.getObject();
                    final ObjectSpecification typeOfSpecification = entityModel.getTypeOfSpecification();
                    String cssClass = typeOfSpecification.getCssClass(objectAdapter);
                    CssClassAppender.appendCssClassTo(tag, cssClass);
                    DataAppender.appendDataTo(tag, "sortableobjectid", ((RootOid)objectAdapter.getOid()).toString());
                }
            }
        };
    }
	
	public void onStop(AjaxRequestTarget target, JQueryEvent event)
	{
		//System.out.println("SortableAjaxDataTable.onStop");
		// do something here
	}
	
	public void onUpdate(AjaxRequestTarget target, JQueryEvent event)
	{
		SortableEvent ev = (SortableEvent) event;
		String objectId = ev.getSortableobjectid();
		int newIndex = ev.getNewIndex();

		IDataProvider<T> dataProvider = SortableAjaxDataTable.this.getDataProvider();		
		
		int indexOfFirstItemOnCurrentPage = Math.toIntExact(getCurrentPage()) * Math.toIntExact(getItemsPerPage());
		
		Iterator<? extends T> it = dataProvider.iterator(indexOfFirstItemOnCurrentPage, getItemsPerPage());
		while(it.hasNext())
		{
			T item = it.next();
			PojoAdapter adapter = (PojoAdapter)item; 
			String identifier = ((RootOid)adapter.getOid()).toString();
			
			if(objectId.equals(identifier))
			{
				((Sortable)adapter.getObject()).updateRank(newIndex);
				break;
			}
		}
	}

	@Override
	public void onConfigure(JQueryBehavior behavior)
	{
		// noop
	}

	@Override
	public void onBeforeRender(JQueryBehavior behavior)
	{
		// noop
	}

	@Override
	public JQueryBehavior newWidgetBehavior(String selector)
 	{
 		return new JQuerySortableBehavior(selector, "sortable")
 		{ 
 			private static final long serialVersionUID = 1L;
 
 			public void onStop(AjaxRequestTarget target, JQueryEvent event)
 			{
 				SortableAjaxDataTable.this.onStop(target, event);
 			}
 			
 			public void onUpdate(AjaxRequestTarget target, JQueryEvent event)
 			{
 				SortableAjaxDataTable.this.onUpdate(target, event);
 			}
 		};
 	}
	
	static class PreserveModelReuseStrategy implements IItemReuseStrategy {
        private static final long serialVersionUID = 1L;

        private static IItemReuseStrategy instance = new PreserveModelReuseStrategy();

        /**
         * @return static instance
         */
        public static IItemReuseStrategy getInstance()
        {
            return instance;
        }

        /**
         * @see org.apache.wicket.markup.repeater.IItemReuseStrategy#getItems(org.apache.wicket.markup.repeater.IItemFactory,
         *      java.util.Iterator, java.util.Iterator)
         */
        @Override
        public <T> Iterator<Item<T>> getItems(final IItemFactory<T> factory,
            final Iterator<IModel<T>> newModels, Iterator<Item<T>> existingItems)
        {
            final Map<IModel<T>, Item<T>> modelToItem = Generics.newHashMap();
            while (existingItems.hasNext())
            {
                final Item<T> item = existingItems.next();
                modelToItem.put(item.getModel(), item);
            }

            return new Iterator<Item<T>>()
            {
                private int index = 0;

                @Override
                public boolean hasNext()
                {
                    return newModels.hasNext();
                }

                @Override
                public Item<T> next()
                {
                    final IModel<T> model = newModels.next();
                    final Item<T> oldItem = modelToItem.get(model);

                    final IModel<T> model2 = oldItem != null ? oldItem.getModel() : model;
                    return factory.newItem(index++, model2);
                }

                @Override
                public void remove()
                {
                    throw new UnsupportedOperationException();
                }

            };
        }

    }

    public void honourHints() {
        UiHintContainer uiHintContainer = getUiHintContainer();
        if(uiHintContainer == null) {
            return;
        }
        
        honourPageNumberHint(uiHintContainer);
    }

    private void honourPageNumberHint(final UiHintContainer uiHintContainer) {
        final String pageNumberStr = uiHintContainer.getHint(this, UIHINT_PAGE_NUMBER);
        if(pageNumberStr != null) {
            try {
                long pageNumber = Long.parseLong(pageNumberStr);
                if(pageNumber >= 0) {
                    // dataTable is clever enough to deal with too-large numbers
                    this.setCurrentPage(pageNumber);
                }
            } catch(Exception ex) {
                // ignore.
            }
        }
        uiHintContainer.setHint(this, UIHINT_PAGE_NUMBER, ""+getCurrentPage());
        // don't broadcast (no AjaxRequestTarget, still configuring initial setup)
    }

    public void setPageNumberHintAndBroadcast(AjaxRequestTarget target) {
        final UiHintContainer uiHintContainer = getUiHintContainer();
        if(uiHintContainer == null) {
            return;
        } 
        uiHintContainer.setHint(this, SortableAjaxDataTable.UIHINT_PAGE_NUMBER, ""+getCurrentPage());
        send(getPage(), Broadcast.EXACT, new IsisUiHintEvent(uiHintContainer, target));
    }

    public void setSortOrderHintAndBroadcast(SortOrder order, String property, AjaxRequestTarget target) {
        final UiHintContainer uiHintContainer = getUiHintContainer();
        if(uiHintContainer == null) {
            return;
        }

        // first clear all SortOrder hints...
        for (SortOrder eachSortOrder : SortOrder.values()) {
            uiHintContainer.clearHint(this, eachSortOrder.name());
        }
        // .. then set this one
        uiHintContainer.setHint(this, order.name(), property);
        send(getPage(), Broadcast.EXACT, new IsisUiHintEvent(uiHintContainer, target));
    }

    private EntityModel getUiHintContainer() {
        return UiHintContainer.Util.hintContainerOf(this, EntityModel.class);
    }
}
