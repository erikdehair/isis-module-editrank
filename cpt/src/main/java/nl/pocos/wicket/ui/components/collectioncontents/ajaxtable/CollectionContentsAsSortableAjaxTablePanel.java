package nl.pocos.wicket.ui.components.collectioncontents.ajaxtable;

import java.util.List;

import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.filter.Filter;
import org.apache.isis.applib.filter.Filters;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.adapter.mgr.AdapterManager.ConcurrencyChecking;
import org.apache.isis.core.metamodel.adapter.version.ConcurrencyException;
import org.apache.isis.core.metamodel.facets.all.hide.HiddenFacet;
import org.apache.isis.core.metamodel.facets.all.named.NamedFacet;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.spec.feature.Contributed;
import org.apache.isis.core.metamodel.spec.feature.ObjectAssociation;
import org.apache.isis.viewer.wicket.model.common.OnConcurrencyExceptionHandler;
import org.apache.isis.viewer.wicket.model.hints.UiHintPathSignificant;
import org.apache.isis.viewer.wicket.model.isis.WicketViewerSettings;
import org.apache.isis.viewer.wicket.model.mementos.ObjectAdapterMemento;
import org.apache.isis.viewer.wicket.model.models.EntityCollectionModel;
import org.apache.isis.viewer.wicket.ui.components.collection.bulk.BulkActionsProvider;
import org.apache.isis.viewer.wicket.ui.components.collection.count.CollectionCountProvider;
import org.apache.isis.viewer.wicket.ui.components.collectioncontents.ajaxtable.CollectionContentsSortableDataProvider;
import org.apache.isis.viewer.wicket.ui.components.collectioncontents.ajaxtable.columns.ColumnAbstract;
import org.apache.isis.viewer.wicket.ui.components.collectioncontents.ajaxtable.columns.ObjectAdapterPropertyColumn;
import org.apache.isis.viewer.wicket.ui.components.collectioncontents.ajaxtable.columns.ObjectAdapterTitleColumn;
import org.apache.isis.viewer.wicket.ui.components.collectioncontents.ajaxtable.columns.ObjectAdapterToggleboxColumn;
import org.apache.isis.viewer.wicket.ui.panels.PanelAbstract;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.Model;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class CollectionContentsAsSortableAjaxTablePanel extends PanelAbstract<EntityCollectionModel>
		implements CollectionCountProvider, UiHintPathSignificant
{

	private static final long serialVersionUID = 1L;

	private static final String ID_TABLE = "table";

	private SortableAjaxDataTable<ObjectAdapter, String> dataTable;
	
		public CollectionContentsAsSortableAjaxTablePanel(final String id, final EntityCollectionModel model)
	{
		super(id, model);
	}

	@Override
	protected void onInitialize()
	{
		super.onInitialize();
		buildGui();
	}

	private void buildGui()
	{
		final List<IColumn<ObjectAdapter, String>> columns = Lists.newArrayList();

		// bulkactions
		final BulkActionsProvider bulkActionsProvider = getBulkActionsProvider();

		ObjectAdapterToggleboxColumn toggleboxColumn = null;
		if (bulkActionsProvider != null)
		{
			toggleboxColumn = bulkActionsProvider.createToggleboxColumn();
			if (toggleboxColumn != null)
			{
				columns.add(toggleboxColumn);
			}
			bulkActionsProvider.configureBulkActions(toggleboxColumn);
		}

		final EntityCollectionModel model = getModel();
		addTitleColumn(columns, model.getParentObjectAdapterMemento(),
				getSettings().getMaxTitleLengthInStandaloneTables(),
				getSettings().getMaxTitleLengthInStandaloneTables());
		addSortingColumn(columns);
		addPropertyColumnsIfRequired(columns);

		final SortableDataProvider<ObjectAdapter, String> dataProvider = new CollectionContentsSortableDataProvider(model);
		dataTable = new SortableAjaxDataTable<>(ID_TABLE, columns, dataProvider);
		addOrReplace(dataTable);
		dataTable.honourHints();
		
		//dataTable.getBody();
		
		if (toggleboxColumn != null)
		{
			final OnConcurrencyExceptionHandler handler2 = new OnConcurrencyExceptionHandler()
			{

				private static final long serialVersionUID = 1L;

				@Override
				public void onConcurrencyException(final Component context,
						final ObjectAdapter selectedAdapter,
						final ConcurrencyException ex,
						final AjaxRequestTarget ajaxRequestTarget)
				{

					// this causes the row to be repainted
					// but it isn't possible (yet) to raise any warning
					// because that only gets flushed on page refresh.
					//

					// perhaps something to tackle in a separate ticket....
					ajaxRequestTarget.add(dataTable);
				}
			};
			toggleboxColumn.setOnConcurrencyExceptionHandler(handler2);
		}
	}	

	private BulkActionsProvider getBulkActionsProvider()
	{
		Component component = this;
		while (component != null)
		{
			if (component instanceof BulkActionsProvider)
			{
				return (BulkActionsProvider) component;
			}
			component = component.getParent();
		}
		return null;
	}

	private void addTitleColumn(
			final List<IColumn<ObjectAdapter, String>> columns,
			ObjectAdapterMemento parentAdapterMementoIfAny,
			int maxTitleParented, int maxTitleStandalone)
	{
		int maxTitleLength = getModel().isParented() ? maxTitleParented : maxTitleStandalone;
		columns.add(new ObjectAdapterTitleColumn(parentAdapterMementoIfAny,maxTitleLength));
	}

	private void addPropertyColumnsIfRequired(
			final List<IColumn<ObjectAdapter, String>> columns)
	{
		final ObjectSpecification typeOfSpec = getModel()
				.getTypeOfSpecification();

		final Where whereContext = getModel().isParented()
				? Where.PARENTED_TABLES : Where.STANDALONE_TABLES;

		final ObjectSpecification parentSpecIfAny = getModel().isParented()
				? getModel().getParentObjectAdapterMemento()
						.getObjectAdapter(ConcurrencyChecking.NO_CHECK)
						.getSpecification()
				: null;

		@SuppressWarnings("unchecked")
		final Filter<ObjectAssociation> filter = Filters
				.and(ObjectAssociation.Filters.PROPERTIES,
						ObjectAssociation.Filters.staticallyVisible(whereContext),
						associationDoesNotReferenceParent(parentSpecIfAny));

		final List<? extends ObjectAssociation> propertyList = typeOfSpec
				.getAssociations(Contributed.INCLUDED, filter);
		for (final ObjectAssociation property : propertyList)
		{
			final ColumnAbstract<ObjectAdapter> nopc = createObjectAdapterPropertyColumn(property);
			columns.add(nopc);
		}
	}

	private void addSortingColumn(final List<IColumn<ObjectAdapter, String>> columns)
	{
		columns.add(new SortingColumn());
	}

	static Filter<ObjectAssociation> associationDoesNotReferenceParent(final ObjectSpecification parentSpec)
	{
		if (parentSpec == null)
		{
			return Filters.any();
		}
		return new Filter<ObjectAssociation>()
		{
			@Override
			public boolean accept(ObjectAssociation association)
			{
				final HiddenFacet facet = association.getFacet(HiddenFacet.class);
				if (facet == null)
				{
					return true;
				}
				if (facet.where() != Where.REFERENCES_PARENT)
				{
					return true;
				}
				final ObjectSpecification assocSpec = association.getSpecification();
				final boolean associationSpecIsOfParentSpec = parentSpec.isOfType(assocSpec);
				final boolean isVisible = !associationSpecIsOfParentSpec;
				return isVisible;
			}
		};
	}

	private ObjectAdapterPropertyColumn createObjectAdapterPropertyColumn(final ObjectAssociation property)
	{

		final NamedFacet facet = property.getFacet(NamedFacet.class);
		final boolean escaped = facet == null || facet.escaped();

		return new ObjectAdapterPropertyColumn(Model.of(property.getName()), property.getId(), property.getId(), escaped);
	}

	@Override
	protected void onModelChanged()
	{
		buildGui();
	}

	@Override
	public Integer getCount()
	{
		final EntityCollectionModel model = getModel();
		return model.getCount();
	}
	
	// region > dependencies

	@Inject
	private WicketViewerSettings settings;
	protected WicketViewerSettings getSettings()
	{
		return settings;
	}
	// endregion

}
