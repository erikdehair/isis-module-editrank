package nl.pocos.wicket.behavior;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.google.common.base.Strings;

public class DataAppender extends AttributeAppender
{
	private static final long serialVersionUID = 1L;

    public DataAppender(final IModel<String> appendModel) {
        super("data-", appendModel, " ");
    }

    public DataAppender(final String append) {
        this(Model.of(append));
    }

    /**
     * Adds CSS class to tag (providing that the class is non-null and non-empty).
     */
    public static void appendDataTo(
            final ComponentTag tag,
            final String dataItem,
            final String value) {
        if(Strings.isNullOrEmpty(dataItem)) {
            return;
        }
        tag.append("data-"+dataItem, value, "");
    }

    /**
     * Adds CSS class to container (providing that the class is non-null and non-empty).
     */
    public static void appendDataTo(
            final MarkupContainer markupContainer,
            final String dataItem,
            final String value) {
        if(Strings.isNullOrEmpty(value)) {
            return;
        }
        markupContainer.add(new DataAppender(value));
    }
}
