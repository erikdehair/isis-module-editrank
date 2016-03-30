package nl.pocos.wicket.ui.components.collectioncontents.ajaxtable;

import org.apache.wicket.ajax.AjaxRequestTarget;

import com.googlecode.wicket.jquery.core.JQueryEvent;

public interface ITableSortJQueryListener
{
	void onStop(AjaxRequestTarget target, JQueryEvent event);
	
	void onUpdate(AjaxRequestTarget target, JQueryEvent event);
}
