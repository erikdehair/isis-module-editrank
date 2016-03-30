package nl.pocos.wicket.ui.components.collectioncontents.ajaxtable;

import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.viewer.wicket.ui.components.collectioncontents.ajaxtable.columns.ColumnAbstract;
import org.apache.isis.viewer.wicket.ui.util.CssClassAppender;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

public class SortingColumn extends ColumnAbstract<ObjectAdapter>
{
	private static final long serialVersionUID = 1L;
	
	public SortingColumn()
	{
		super(null);
	}

	@Override
	public void populateItem(Item<ICellPopulator<ObjectAdapter>> cellItem,
			String componentId, IModel<ObjectAdapter> rowModel)
	{
		final Component component = new SortingHandlePanel(componentId);
		cellItem.add(component);
		cellItem.add(new CssClassAppender("dragHandle"));
	}
}