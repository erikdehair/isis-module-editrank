package nl.pocos.wicket.ui.components.collectioncontents.ajaxtable;

import org.apache.isis.viewer.wicket.model.models.EntityCollectionModel;
import org.apache.isis.viewer.wicket.ui.CollectionContentsAsFactory;
import org.apache.isis.viewer.wicket.ui.ComponentFactoryAbstract;
import org.apache.isis.viewer.wicket.ui.ComponentType;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import nl.pocos.applib.editrank.Sortable;

public class CollectionContentsAsSortableAjaxTablePanelFactory
		extends ComponentFactoryAbstract implements CollectionContentsAsFactory
{

	private static final long serialVersionUID = 1L;

	private static final String NAME = "table";

	public CollectionContentsAsSortableAjaxTablePanelFactory()
	{
		super(ComponentType.COLLECTION_CONTENTS, NAME, CollectionContentsAsSortableAjaxTablePanel.class);
	}

	@Override
	public ApplicationAdvice appliesTo(final IModel<?> model)
	{
		ApplicationAdvice advice = appliesIf(model instanceof EntityCollectionModel);
		
		if(advice.applies())
		{
			EntityCollectionModel ecm = (EntityCollectionModel)model;
			Class<?> clazz = ecm.getTypeOfSpecification().getCorrespondingClass();
			advice = appliesIf(Sortable.class.isAssignableFrom(clazz));
			
			return advice;
		}
		else
		{
			return ApplicationAdvice.DOES_NOT_APPLY;
		}
	}

	@Override
	public Component createComponent(final String id, final IModel<?> model)
	{
		final EntityCollectionModel collectionModel = (EntityCollectionModel) model;
		return new CollectionContentsAsSortableAjaxTablePanel(id, collectionModel);
	}

	@Override
	public IModel<String> getTitleLabel()
	{
		return new ResourceModel("CollectionContentsAsSortableAjaxTablePanelFactory.Table", "Sortable");
	}

	@Override
	public IModel<String> getCssClass()
	{
		return Model.of("fa fa-fw fa-table");
	}
}
