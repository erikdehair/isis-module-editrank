package nl.pocos.wicket.ui.components.instruction;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.core.IJQueryWidget;
import com.googlecode.wicket.jquery.core.JQueryBehavior;

public abstract class AbstractTooltipPanel extends WebMarkupContainer implements IJQueryWidget
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String instruction;
	private MarkupContainer markupProvider;
	/**
	 * @param id
	 */
	public AbstractTooltipPanel(String id, Model<String> instruction, MarkupContainer markupProvider)
	{
		super(id);
		this.instruction = instruction.getObject();
		this.markupProvider = markupProvider;
	}

	@Override
	protected void onInitialize()
	{
		super.onInitialize();
		final JQueryBehavior newWidgetBehavior = JQueryWidget.newWidgetBehavior(this);
		this.add(newWidgetBehavior); //cannot be in ctor as the markupId may be set manually afterward
	}

	@Override
	public void onConfigure(JQueryBehavior behavior)
	{               
	}

	// IJQueryWidget //
	@Override
	public void onBeforeRender(JQueryBehavior behavior) {
	}

	// IJQueryWidget //
	@Override
	public JQueryBehavior newWidgetBehavior(String selector)
	{
		final HelpTooltipBehaviour tooltipBehavior = new HelpTooltipBehaviour(instruction, markupProvider);
		return tooltipBehavior;
	}       
}
