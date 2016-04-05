package nl.pocos.wicket.ui.components.collectioncontents.ajaxtable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.isis.viewer.wicket.ui.components.collectioncontents.ajaxtable.columns.ObjectAdapterTitleColumn;
import org.apache.isis.viewer.wicket.ui.util.Components;
import org.apache.isis.viewer.wicket.ui.util.CssClassAppender;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;

import nl.pocos.wicket.ui.components.instruction.InfoPanel;
import nl.pocos.wicket.ui.components.instruction.InstructionPanel;

public class UnsortableHeadersToolbar<S> extends AbstractToolbar
{
	private static final long serialVersionUID = 1L;
	
	private static final String ID_INSTRUCTION_PANEL = "instructionPanel";

    static abstract class CssAttributeBehavior extends Behavior
    {
        private static final long serialVersionUID = 1L;

        protected abstract String getCssClass();

        /**
         * @see Behavior#onComponentTag(Component, ComponentTag)
         */
        @Override
        public void onComponentTag(final Component component, final ComponentTag tag)
        {
            String className = getCssClass();
            if (!Strings.isEmpty(className))
            {
                tag.append("class", className, " ");
            }
        }
    }
    
    /**
     * Constructor
     * 
     * @param <T>
     *            the column data type
     * @param table
     *            data table this toolbar will be attached to
     * @param stateLocator
     *            locator for the ISortState implementation used by sortable headers
     */
    public <T> UnsortableHeadersToolbar(final DataTable<T, S> table)
    {
        super(table);
        table.setOutputMarkupId(true);

        RefreshingView<IColumn<T, S>> headers = new RefreshingView<IColumn<T, S>>("headers")
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected Iterator<IModel<IColumn<T, S>>> getItemModels()
            {
                List<IModel<IColumn<T, S>>> columnsModels = new LinkedList<IModel<IColumn<T, S>>>();

                for (IColumn<T, S> column : table.getColumns())
                {
                    columnsModels.add(Model.of(column));
                }

                return columnsModels.iterator();
            }

            @Override
            protected void populateItem(Item<IColumn<T, S>> item)
            {
                final IColumn<T, S> column = item.getModelObject();

                WebMarkupContainer header = new WebMarkupContainer("header");
                
                item.add(header);
                item.setRenderBodyOnly(true);
                Component label = column.getHeader("label");
                header.add(label);
                
                boolean hideInstructionPanel = true;
                
                if(column instanceof ObjectAdapterTitleColumn) {
                    header.add(new CssClassAppender("title-column"));
                }
                else if(column instanceof SortingColumn)
                {
                	header.add(new InstructionPanel(ID_INSTRUCTION_PANEL,
                			Model.of(getString("sorting.helpText")),
                			UnsortableHeadersToolbar.this));
                	hideInstructionPanel = false;
                }
                else if(column instanceof IndentationColumn)
                {
                	header.add(new InstructionPanel(ID_INSTRUCTION_PANEL,
                			Model.of(getString("indentation.helpText")),
                			UnsortableHeadersToolbar.this));
                	hideInstructionPanel = false;
                }
                
                if(hideInstructionPanel)
                {
                	Components.permanentlyHide(header, ID_INSTRUCTION_PANEL);
                }
            }
        };
        add(headers);
    }
    
    @Override
    protected void onInitialize() {
        super.onInitialize();
    }
}
