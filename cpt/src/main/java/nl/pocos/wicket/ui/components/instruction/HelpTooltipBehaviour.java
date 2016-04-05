/**
 * 
 */
package nl.pocos.wicket.ui.components.instruction;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.tooltip.CustomTooltipBehavior;

/**
 * @author "Erik de Hair <e.dehair@pocos.nl>"
 *
 */
public class HelpTooltipBehaviour extends CustomTooltipBehavior
{
	private static final long serialVersionUID = 1L;

	private final String content;
	private final MarkupContainer markupProvider;

	public HelpTooltipBehaviour(String content, MarkupContainer markupProvider)
	{
		super(newOptions());

		this.content = escape(content);
		this.markupProvider = markupProvider;
	}

	@Override
	protected WebMarkupContainer newContent(String markupId)
	{
		Fragment fragment = new Fragment(markupId, "tooltip-fragment", markupProvider);
		fragment.add(new Label("content", Model.of(this.content)));

		return fragment;
	}

	//@Override
	private String escape(String content)
	{
		if(content == null)
		{
			return null;
		}
		else if(content.startsWith("\n"))
		{
			content = content.replaceFirst("\n", "");
		}
		return content.replace("\t", "").replace("\"", "'");
	}

	private static Options newOptions()
	{
		Options options = new Options();
		options.set("track", false);
		options.set("hide", "{ effect: 'fade', delay: 100 }");
		options.set("tooltipClass","\"myTooltip\"");

		return options;
	}
}