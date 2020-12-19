package com.fun.tc.nc.handlers;

import javax.swing.SwingUtilities;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.fun.tc.nc.dialogs.CreateCAMDialog;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;

public class CreateCAMHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		TCComponent com = (TCComponent) AIFUtility.getCurrentApplication().getTargetComponent();
		if (!"AE8Process Revision".equals(com.getType())) {
			MessageBox.post("请选择机加工艺版本进行操作", "提示", MessageBox.INFORMATION);
			return null;
		}
		
		TCComponentItemRevision processRev = (TCComponentItemRevision) com;
		try {
			SwingUtilities.invokeLater(new CreateCAMDialog(processRev));
		} catch (TCException e) {
			e.printStackTrace();
			MessageBox.post(e);
		}
		return null;
	}
}
