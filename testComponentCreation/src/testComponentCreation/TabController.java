package testComponentCreation;

import java.util.HashMap;
import java.util.concurrent.Callable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.view.ViewDeclarationLanguage;

import org.primefaces.component.tabview.Tab;
import org.primefaces.component.tabview.TabView;

import test.component.creation.Person;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.SecurityManagerFactory;


@ManagedBean
@RequestScoped
public class TabController {
	private TabView view;

	public TabView getView() {
		return view;
	}

	public void setView(TabView view) {
		this.view = view;
	}
    public void initializeTabs(ComponentSystemEvent event) {
        if (view.getChildren().isEmpty()) {
            Person defaultPerson = new Person();
            defaultPerson.setFirstname("Default");
            defaultPerson.setLastname("Person");
            addTab(defaultPerson);
        }
    }
    
	public static void addTab(Person person){
		TabController controller = getCurrentController();
		Tab tab = new Tab();
		tab.setTitle(person.getFirstname());
		UIComponent personDetails = controller.createPersonDetailsComponent(person);
		tab.getChildren().add(personDetails);
		controller.getView().getChildren().add(tab);
	}

	private static TabController getCurrentController() {
		FacesContext fc = FacesContext.getCurrentInstance();
		return (TabController)fc.getApplication().evaluateExpressionGet(fc, 
				"#{tabController}", TabController.class);
	}
	
	private javax.faces.component.UIComponent createPersonDetailsComponent(Person p)
	{
		Ivy.log().info("create component for "+p);
		final HashMap<String, Object> attributes = new HashMap<String,Object>();
		attributes.put("person", "#{data.selected}");
		return createIvyComponent("test.component.creation.PersonDetails", attributes);
	}
	

    
	private static javax.faces.component.UIComponent createIvyComponent(final String dialogId, final HashMap<String, Object> attributes)
	{
		final FacesContext fc = FacesContext.getCurrentInstance();
		final ViewDeclarationLanguage vdl = fc.getApplication().getViewHandler()
				.getViewDeclarationLanguage(fc, fc.getViewRoot().getViewId());
		try {
			return SecurityManagerFactory.getSecurityManager().executeAsSystem(new Callable<UIComponent>() {

				@Override
				public UIComponent call() throws Exception {
					return vdl
							.createComponent(fc, "http://ivyteam.ch/jsf/component", dialogId, attributes);
				}
			});
		} catch (Exception e) {
			Ivy.log().error("failed to create ivy component "+dialogId, e);
			return null;
		}
	}

	
}
