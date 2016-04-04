package nl.pocos.wicket.ui.components.collectioncontents.ajaxtable;

import org.apache.isis.viewer.wicket.model.models.EntityModel;
import org.apache.isis.viewer.wicket.ui.panels.PanelAbstract;
import org.apache.isis.viewer.wicket.ui.util.CssClassAppender;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;

import nl.pocos.applib.editrank.Sortable;



public class IndentationButtonsPanel extends PanelAbstract<EntityModel>
{
	private static final int MAX_INDENTATION_LEVEL = 5;
	private static final long serialVersionUID = 1L;
	private static final String INDENT_BUTTON_ID = "indentButton";
	private static final String OUTDENT_BUTTON_ID = "outdentButton";
	private final Component collectionContents;
	
	public IndentationButtonsPanel(String id, EntityModel model, Component collectionContents)
	{
		super(id, model);
		this.collectionContents = collectionContents;
	}
	
	@Override
	protected void onInitialize()
	{
		super.onInitialize();
		buildGui();
	}
	
	private void buildGui()
	{
		addButtons();
		this.collectionContents.setOutputMarkupId(true);
	}
	
	private Component getCollectionContents()
	{
		return this.collectionContents;
	}
	
	private void addButtons()
	{
		add(createIndentButton(), createOutdentButton());
	}
	
	private Sortable getSortable()
	{
		return (Sortable)getModel().getObject().getObject();
	}
	
	private void increaseIndentation()
	{
		Sortable object = getSortable();
		if(object.getLevel() < MAX_INDENTATION_LEVEL)
		{
			object.increaseIndentation();
		}
	}
	
	private void decreaseIndentation()
	{
		getSortable().decreaseIndentation();
	}
	
	private AjaxFallbackLink<Object> createIndentButton()
	{
		AjaxFallbackLink<Object> button = new AjaxFallbackLink<Object>(INDENT_BUTTON_ID)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target)
			{
				//target.addChildren(getCollectionContents().getParent(), Component.class);
				increaseIndentation();
				target.add(getCollectionContents());
			}			
		};
		button.setOutputMarkupId(true);
		determineEnabled(getSortable().getLevel() < MAX_INDENTATION_LEVEL, button);
		return button;
	}
	
	@Override
	protected void onModelChanged() {
		super.onModelChanged();
		buildGui();
	}

	private AjaxFallbackLink<Object> createOutdentButton()
	{
		AjaxFallbackLink<Object> button = new AjaxFallbackLink<Object>(OUTDENT_BUTTON_ID)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target)
			{
				//target.addChildren(getCollectionContents().getParent(), Component.class);
				decreaseIndentation();
				target.add(getCollectionContents());
			}
		};
		button.setOutputMarkupId(true);
		determineEnabled(getSortable().getLevel() > 0, button);
		return button;
	}
	
	private void determineEnabled(boolean enabled, AjaxFallbackLink<Object> button)
	{
		button.setEnabled(enabled);
		if(!enabled)
		{
			button.add(new CssClassAppender("disabled"));
		}
	}	
}
