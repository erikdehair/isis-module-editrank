package nl.pocos.wicket.ui.components.collectioncontents.ajaxtable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.CallbackParameter;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.JQueryEvent;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.core.ajax.IJQueryAjaxAware;
import com.googlecode.wicket.jquery.core.ajax.JQueryAjaxBehavior;
import com.googlecode.wicket.jquery.core.utils.RequestCycleUtils;

public abstract class JQuerySortableBehavior extends JQueryBehavior
	implements IJQueryAjaxAware, ITableSortJQueryListener
{
		private static final long serialVersionUID = 1L;
		private JQueryAjaxBehavior onUpdateAjaxBehavior;
		private JQueryAjaxBehavior onStopAjaxBehavior;

		public JQuerySortableBehavior(String selector, String method)
		{
			super(selector, method);
		}

		public void bind(Component component)
		{
			super.bind(component);

			this.selector += " tbody";
			
			component.add(this.onUpdateAjaxBehavior = this.newJQueryOnUpdateAjaxBehavior(this));
			component.add(this.onStopAjaxBehavior = this.newJQueryOnStopAjaxBehavior(this));
		}

		// Events //
		public void onConfigure(Component component)
		{
			super.onConfigure(component);

			this.setOptions(newOptions());
			this.setOption("stop", this.onStopAjaxBehavior.getCallbackFunction());
			this.setOption("update", this.onUpdateAjaxBehavior.getCallbackFunction()); 			
		}
		
		private Options newOptions()
		{
			Options options = new Options();		
			options.set("items", Options.asString("> tr"));
			options.set("appendTo", Options.asString("parent"));
			options.set("helper", Options.asString("clone"));
			options.set("cursor", Options.asString("move"));
			options.set("handle", Options.asString(".dragHandle"));
			
			Options cursor = new Options();
			cursor.set("left", 5);
			cursor.set("top", 5);
			options.set("cursorAt", cursor);
			
			options.set("revert", true);
			return options;
		}

		public void onAjax(AjaxRequestTarget target, JQueryEvent event)
		{
			if (event instanceof JQuerySortableBehavior.StopEvent)
			{
				this.onStop(target, event);
			}
			else if(event instanceof JQuerySortableBehavior.UpdateEvent)
			{
				this.onUpdate(target, event);
			}
		}
		
		// Factory //
		protected JQueryAjaxBehavior newJQueryOnUpdateAjaxBehavior(IJQueryAjaxAware source)
		{
			return new JQueryAjaxBehavior(source) {

				private static final long serialVersionUID = 1L;

				protected CallbackParameter[] getCallbackParameters()
				{
					return new CallbackParameter[] { CallbackParameter.context("event"),
							CallbackParameter.context("ui"), // lf
							CallbackParameter.resolved("sortableobjectid", "ui.item.data('sortableobjectid')"), // lf
							CallbackParameter.resolved("index", "ui.item.index()")};
				} 				

				protected JQueryEvent newEvent()
				{
					return new UpdateEvent();
				}
			};
	}

	// Factory //
	protected JQueryAjaxBehavior newJQueryOnStopAjaxBehavior(IJQueryAjaxAware source)
	{
		return new JQueryAjaxBehavior(source)
		{
			private static final long serialVersionUID = 1L;

			protected CallbackParameter[] getCallbackParameters()
			{
				return new CallbackParameter[] {
						CallbackParameter.context("event"),
						CallbackParameter.context("ui") };
			}

			protected JQueryEvent newEvent()
			{
				return new StopEvent();
			}
		};
	}
		
		/**
		 * Provides a base class for {@link JQuerySortableBehavior} event objects
		 */
		protected class SortableEvent extends JQueryEvent
		{
			private final String sortableobjectid;
			private final int newIndex;

			public SortableEvent()
			{
				this.sortableobjectid = RequestCycleUtils.getQueryParameterValue("sortableobjectid").toString();
				this.newIndex = RequestCycleUtils.getQueryParameterValue("index").toInt(-1); // remove-behavior will default to -1
			}

			/**
			 * Gets the sortableobjectid
			 *
			 * @return the sortableobjectid
			 */
			public String getSortableobjectid()
			{
				return this.sortableobjectid;
			}

			/**
			 * Gets the newIndex
			 *
			 * @return the newIndex
			 */
			public int getNewIndex()
			{
				return this.newIndex;
			}
		}

		// Event objects //
		protected class StopEvent extends SortableEvent
		{

		}

		// Event objects //
		protected class UpdateEvent extends SortableEvent
		{

		}
	}
