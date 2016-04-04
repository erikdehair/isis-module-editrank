package nl.pocos.wicket.ui.components.collectioncontents.ajaxtable;

import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.viewer.wicket.model.models.EntityModel;
import org.apache.isis.viewer.wicket.ui.components.collectioncontents.ajaxtable.columns.ColumnAbstract;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

public class IndentationColumn extends ColumnAbstract<ObjectAdapter>
{
	private static final long serialVersionUID = 1L;
	private final Component collectionContents;
	
	public IndentationColumn(Component collectionContents)
	{
		super(null);
		this.collectionContents = collectionContents;
	}

	@Override
	public void populateItem(Item<ICellPopulator<ObjectAdapter>> cellItem,
			String componentId, IModel<ObjectAdapter> rowModel)
	{
		final Component component = new IndentationButtonsPanel(componentId, (EntityModel)rowModel, collectionContents);
		cellItem.add(component);
	}
}