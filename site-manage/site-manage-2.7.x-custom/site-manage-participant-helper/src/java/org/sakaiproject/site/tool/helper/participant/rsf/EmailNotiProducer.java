package org.sakaiproject.site.tool.helper.participant.rsf;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.tool.helper.participant.impl.SiteAddParticipantHandler;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.user.api.UserDirectoryService;

import uk.ac.cam.caret.sakai.rsf.producers.FrameAdjustingProducer;
import uk.ac.cam.caret.sakai.rsf.util.SakaiURLUtil;
import uk.org.ponder.messageutil.MessageLocator;
import uk.org.ponder.messageutil.TargettedMessage;
import uk.org.ponder.messageutil.TargettedMessageList;
import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutputMany;
import uk.org.ponder.rsf.components.UISelect;
import uk.org.ponder.rsf.components.UISelectChoice;
import uk.org.ponder.rsf.components.UISelectLabel;
import uk.org.ponder.rsf.components.decorators.UILabelTargetDecorator;
import uk.org.ponder.rsf.flow.ARIResult;
import uk.org.ponder.rsf.flow.ActionResultInterceptor;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCaseReporter;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.RawViewParameters;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.stringutil.StringList;

/**
 * Assign same role while adding participant
 * @author
 *
 */
public class EmailNotiProducer implements ViewComponentProducer, NavigationCaseReporter, ActionResultInterceptor {

	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(EmailNotiProducer.class);
	
    public SiteAddParticipantHandler handler;
    public static final String VIEW_ID = "EmailNoti";
    public MessageLocator messageLocator;
    public FrameAdjustingProducer frameAdjustingProducer;
    public SessionManager sessionManager;
    
    private SiteService siteService = null;
    public void setSiteService(SiteService siteService)
	{
		this.siteService = siteService;
	}

    public String getViewID() {
        return VIEW_ID;
    }
    
    private TargettedMessageList targettedMessageList;
	public void setTargettedMessageList(TargettedMessageList targettedMessageList) {
		this.targettedMessageList = targettedMessageList;
	}
	
	public UserDirectoryService userDirectoryService;
	public void setUserDirectoryService(UserDirectoryService userDirectoryService)
	{
		this.userDirectoryService = userDirectoryService;
	}

	private DeveloperHelperService developerHelperService;
    public void setDeveloperHelperService(
			DeveloperHelperService developerHelperService) {
		this.developerHelperService = developerHelperService;
	}

    
	public void fillComponents(UIContainer tofill, ViewParameters arg1, ComponentChecker arg2) {
    	
    	String locationId = developerHelperService.getCurrentLocationId();
    	String siteTitle = "";
    	try {
			Site s = siteService.getSite(locationId);
			siteTitle = s.getTitle();
		} catch (IdUnusedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	UIMessage.make(tofill, "add.addpart", "add.addpart", new Object[]{siteTitle});
    	
    	UIBranchContainer content = UIBranchContainer.make(tofill, "content:");
        
    	UIForm emailNotiForm = UIForm.make(content, "emailNoti-form");
    	
    	// role choice
    	String[] values = new String[] { Boolean.TRUE.toString(), Boolean.FALSE.toString()};
	    String[] labels = new String[] {
	    		messageLocator.getMessage("addnoti.sendnow"), 
	    		messageLocator.getMessage("addnoti.dontsend")
	    		};	    
	    StringList notiItems = new StringList();
	    UISelect notiSelect = UISelect.make(emailNotiForm, "select-noti", null,
		        "#{siteAddParticipantHandler.emailNotiChoice}", handler.emailNotiChoice);
	    String selectID = notiSelect.getFullID();
	    notiSelect.optionnames = UIOutputMany.make(labels);
	    for (int i = 0; i < values.length; i++) {
	    	
		    UIBranchContainer notiRow = UIBranchContainer.make(emailNotiForm, "noti-row:", Integer.toString(i));
           
		    UISelectLabel lb = UISelectLabel.make(notiRow, "noti-label", selectID, i);
            UISelectChoice choice = UISelectChoice.make(notiRow, "noti-select", selectID, i);
            UILabelTargetDecorator.targetLabel(lb, choice);
            
            notiItems.add(values[i]);
        }
        notiSelect.optionlist.setValue(notiItems.toStringArray());   
        
    	// buttons
    	UICommand.make(emailNotiForm, "continue", messageLocator.getMessage("gen.continue"), "#{siteAddParticipantHandler.processEmailNotiContinue}");
    	UICommand.make(emailNotiForm, "back", messageLocator.getMessage("gen.back"), "#{siteAddParticipantHandler.processEmailNotiBack}");
    	UICommand.make(emailNotiForm, "cancel", messageLocator.getMessage("gen.cancel"), "#{siteAddParticipantHandler.processCancel}");
   
    	//process any messages
        if (targettedMessageList != null && targettedMessageList.size() > 0) {
			for (int i = 0; i < targettedMessageList.size(); i ++ ) {
				UIBranchContainer errorRow = UIBranchContainer.make(tofill,"error-row:", Integer.valueOf(i).toString());
				TargettedMessage msg = targettedMessageList.messageAt(i);
		    	if (msg.args != null ) 
		    	{
		    		UIMessage.make(errorRow,"error", msg.acquireMessageCode(), (Object[]) msg.args);
		    	} 
		    	else 
		    	{
		    		UIMessage.make(errorRow,"error", msg.acquireMessageCode());
		    	}
			}
        }
    }
    
    public ViewParameters getViewParameters() {
    	AddViewParameters params = new AddViewParameters();

        params.id = null;
        return params;
    }
    
    public List<NavigationCase> reportNavigationCases() {
        List<NavigationCase> togo = new ArrayList<NavigationCase>();
        togo.add(new NavigationCase("continue", new SimpleViewParameters(ConfirmProducer.VIEW_ID)));
        togo.add(new NavigationCase("backSameRole", new SimpleViewParameters(SameRoleProducer.VIEW_ID)));
        togo.add(new NavigationCase("backDifferentRole", new SimpleViewParameters(DifferentRoleProducer.VIEW_ID)));
        return togo;
    }
    
    public void interceptActionResult(ARIResult result, ViewParameters incoming,
            Object actionReturn) 
    {
        if ("done".equals(actionReturn)) {
          Tool tool = handler.getCurrentTool();
           result.resultingView = new RawViewParameters(SakaiURLUtil.getHelperDoneURL(tool, sessionManager));
        }
    }
}
