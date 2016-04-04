package nl.pocos.wicket.ui.components.collectioncontents.ajaxtable.columns;

import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.viewer.wicket.model.mementos.ObjectAdapterMemento;
import org.apache.isis.viewer.wicket.model.models.EntityModel;
import org.apache.isis.viewer.wicket.model.models.EntityModel.RenderingHint;
import org.apache.isis.viewer.wicket.ui.ComponentFactory;
import org.apache.isis.viewer.wicket.ui.ComponentType;
import org.apache.isis.viewer.wicket.ui.components.collectioncontents.ajaxtable.columns.ObjectAdapterTitleColumn;
import org.apache.isis.viewer.wicket.ui.util.CssClassAppender;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

import nl.pocos.applib.editrank.Sortable;

public class SortableObjectAdapterTitleColumn extends ObjectAdapterTitleColumn
{
	private static final long serialVersionUID = 1L;
	private final ObjectAdapterMemento parentAdapterMementoIfAny;
	private static final String ID_CELL_ITEM = "cellItem";

	public SortableObjectAdapterTitleColumn(ObjectAdapterMemento parentAdapterMementoIfAny, int maxTitleLength)
	{
		super(parentAdapterMementoIfAny, maxTitleLength);
		this.parentAdapterMementoIfAny = parentAdapterMementoIfAny;
	}
	
	@Override
    public void populateItem(final Item<ICellPopulator<ObjectAdapter>> cellItem, final String componentId,
    		final IModel<ObjectAdapter> rowModel)
	{
		final ObjectAdapter adapter = rowModel.getObject();
        
		boolean addDefaultCell = true;
        if(Sortable.class.isAssignableFrom(adapter.getObject().getClass()))
        {
        	Sortable sortable = (Sortable)adapter.getObject();
        	if(sortable.getLevel() > 0)
        	{
        		addDefaultCell = false;
        		cellItem.add(new CssClassAppender("title-column indented level"+sortable.getLevel()));

        		IndentedPanel indentedPanel = new IndentedPanel(componentId);
        		indentedPanel.add(createComponent(ID_CELL_ITEM, rowModel));

        		cellItem.add(indentedPanel);
        	}
        }
        
        if(addDefaultCell)
        {
        	super.populateItem(cellItem, componentId, rowModel);
        }
	}

	private Component createComponent(final String id, final IModel<ObjectAdapter> rowModel)
	{
		final ObjectAdapter adapter = rowModel.getObject();
		final EntityModel model = new EntityModel(adapter);
		model.setRenderingHint(parentAdapterMementoIfAny != null? RenderingHint.PARENTED_TITLE_COLUMN: RenderingHint.STANDALONE_TITLE_COLUMN);
		model.setContextAdapterIfAny(parentAdapterMementoIfAny);
		// will use EntityLinkSimplePanelFactory as model is an EntityModel
		final ComponentFactory componentFactory = findComponentFactory(ComponentType.ENTITY_LINK, model);
		return componentFactory.createComponent(id, model);
	}
}
