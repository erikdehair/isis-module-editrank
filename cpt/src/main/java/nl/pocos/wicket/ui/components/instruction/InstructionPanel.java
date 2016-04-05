/**
 * 
 */
package nl.pocos.wicket.ui.components.instruction;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;

/**
 * @author "Erik de Hair <e.dehair@pocos.nl>"
 *
 */
public class InstructionPanel extends AbstractTooltipPanel
{
	private static final long serialVersionUID = 1L;
	
	public InstructionPanel(String id, Model<String> instruction, MarkupContainer markupProvider)
    {
		super(id, instruction, markupProvider);
    }
	
	@Override
    public void renderHead(final IHeaderResponse response)
	{
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new CssResourceReference(InstructionPanel.class, "instruction-panel.css")));
	}
}
